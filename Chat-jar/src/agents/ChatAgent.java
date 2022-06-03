package agents;

import java.util.ArrayList;
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
	private MessageManagerRemote messageMenager;
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
	public void handleMessage(AgentMessage message) {
//		TextMessage tmsg = (TextMessage) message;
		
		String receiver;
		try {
			receiver = (String) message.getUserArg("receiver");
			String sessionId = (String) message.getUserArg("sessionId");
			if (agentId.equals(receiver)) {
				String option = "";
				String response = "";
				try {
					option = (String) message.getUserArg("command");
					switch (option) {
					case "REGISTER":
						String username = (String) message.getUserArg("username");
						String password = (String) message.getUserArg("password");
	
						boolean result = chatManager.register(new User(username, password));

						response = "register:" + (result ? "OK "+username : "NO: Not registered!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							AgentMessage newMessage = new AgentMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_REGISTER");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "LOG_IN":
						username = (String) message.getUserArg("username");
						password = (String) message.getUserArg("password");

						result = chatManager.login(username, password,sessionId);

						response = "login:OK id" + (result ? username : "No!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							AgentMessage newMessage = new AgentMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_LOGIN");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "GET_LOGGEDIN":
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.getUsername() + "|";
						}

						break;
					case "GET_REGISTERED":
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.getUsername() + "|";
						}

						break;
					case "LOGOUT" :
						username = (String) message.getUserArg("username");
						password = (String) message.getUserArg("password");
						result = chatManager.logout(username, password);
						response = "logout:OK id" + (result ? username : "No!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							AgentMessage newMessage = new AgentMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_LOGOUT");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "SEND_ALL" :
						String sender = (String) message.getUserArg("sender");
						String realsender = (String) message.getUserArg("realsender");
						String content = (String) message.getUserArg("content");
						String date = (String) message.getUserArg("date");
						String subject = (String) message.getUserArg("subject");
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							AgentMessage newMessage = new AgentMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", sender);
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "RECIVE_MESSAGE");
							newMessage.userArgs.put("content", content);
							newMessage.userArgs.put("date", date);
							newMessage.userArgs.put("subject", subject);
							newMessage.userArgs.put("realsender", realsender);
							if(!agent.getAgentId().equals("chat")) {
//								agent.handleMessage(message);
								messageMenager.post(newMessage);
							}
						}
						response = "messages:ALL send";
						break;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					System.out.println(response);
					ws.onMessage(sessionId, response);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
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

	@Override
	public void init(String agentId) {
		// TODO Auto-generated method stub
		
	}
}
