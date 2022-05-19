package agents;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import chatmanager.ChatManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.User;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class ChatAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String agentId;

	@EJB
	private ChatManagerRemote chatManager;
	@EJB
	private CachedAgentsRemote cachedAgents;
	@EJB
	private WSChat ws;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created Chat Agent!");
	}

	//private List<String> chatClients = new ArrayList<String>();

	protected MessageManagerRemote msm() {
		return (MessageManagerRemote) JNDILookup.lookUp(JNDILookup.MessageManagerLookup, MessageManagerRemote.class);
	}

	@Override
	public void handleMessage(Message message) {
		TextMessage tmsg = (TextMessage) message;

		String receiver;
		try {
			receiver = (String) tmsg.getObjectProperty("receiver");
			if (agentId.equals(receiver)) {
				String option = "";
				String response = "";
				try {
					option = (String) tmsg.getObjectProperty("command");
					switch (option) {
					case "REGISTER":
						String username = (String) tmsg.getObjectProperty("username");
						String password = (String) tmsg.getObjectProperty("password");
	
						boolean result = chatManager.register(new User(username, password));

						response = "register:OK " + (result ? username : "No!");
						break;
					case "LOG_IN":
						username = (String) tmsg.getObjectProperty("username");
						password = (String) tmsg.getObjectProperty("password");
						result = chatManager.login(username, password);

						response = "login:OK id" + (result ? username : "No!");
						break;
					case "GET_LOGGEDIN":
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.toString() + "|";
						}

						break;
					case "GET_REGISTERED":
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.toString() + "|";
						}

						break;
					case "LOGOUT" :
						username = (String) tmsg.getObjectProperty("username");
						password = (String) tmsg.getObjectProperty("password");
						result = chatManager.logout(username, password);
						response = "logout:OK id" + (result ? username : "No!");
						break;
					case "x":
						break;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					System.out.println(response);
					ws.onMessage("chat", response);
					
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String init() {
		agentId = "chat";
		cachedAgents.addRunningAgent(agentId, this);
		return agentId;
	}

	@Override
	public String getAgentId() {
		return agentId;
	}
}
