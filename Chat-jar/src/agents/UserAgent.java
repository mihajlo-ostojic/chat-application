package agents;

import java.util.List;
import java.util.Random;

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
import models.ChatMessages;
import models.User;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class UserAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String agentId;
	@EJB
	private CachedAgentsRemote cachedAgents;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private MessageManagerRemote messageManager;
	
	private ChatMessages chatMessages;
	
	@EJB
	private WSChat ws;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created User Agent!");
		chatMessages = new ChatMessages();
	}
	
	public void handleMessage(AgentMessage msg) {
//		TextMessage tmsg = (TextMessage) msg;
		String receiver;
		String sender;
		String message;
		try {
			receiver = (String) msg.getUserArg("receiver");
			sender = (String) msg.getUserArg("sender");
			message = (String) msg.getUserArg("message");
			if (receiver.equals(agentId)) {
				System.out.println("Received from :" + sender);
				
				
				String option = "";
				String response = "";
				try {
					option = (String) msg.getUserArg("command");
					switch (option) {
					case "RECIVE_MESSAGE":
						String temp = (String) msg.getUserArg("content");
						String subjecttemp = (String) msg.getUserArg("subject");
						String datetemp = (String) msg.getUserArg("date");
						models.Message newMsg = new models.Message(sender,receiver,temp,datetemp,subjecttemp);
						chatMessages.receiveMessage(newMsg);
						response = "message:"+newMsg.toString();
						String idGnenta = getAgentId();
						String idSesije = chatManager.getSessionId(idGnenta);
						
						ws.onMessage(idSesije, response);
						break;
					case "GET":
						
						String  sessionId = (String) msg.getUserArg("sessionId");
						String  who = (String) msg.getUserArg("sender");
						
						response = "messages:";
						for (models.Message mess : chatMessages.getChatForUser(who)) {
							response +=  mess.toString()+"|";
						}
						ws.onMessage(sessionId, response);
						break;
					case "NEW_REGISTER":
						sessionId = (String) msg.getUserArg("sessionId");
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.getUsername() + "|";
						}
						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGIN":
						sessionId = (String) msg.getUserArg("sessionId");
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.getUsername() + "|";
						}
						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGOUT":
						sessionId = (String) msg.getUserArg("sessionId");
						response = "loggedInList:";
						List<User> users3 = chatManager.loggedInUsers();
						for (User u : users3) {
							response += u.getUsername() + "|";
						}
						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public void handleMessage(Message msg) {
		TextMessage tmsg = (TextMessage) msg;
		String receiver;
		String sender;
		String message;
		try {
			receiver = (String) tmsg.getObjectProperty("receiver");
			sender = (String) tmsg.getObjectProperty("sender");
			message = (String) tmsg.getObjectProperty("message");
			if (receiver.equals(agentId)) {
				System.out.println("Received from :" + sender);
				
				
				String option = "";
				String response = "";
				try {
					option = (String) tmsg.getObjectProperty("command");
					switch (option) {
					case "RECIVE_MESSAGE":
						String temp = (String) tmsg.getObjectProperty("content");
						String subjecttemp = (String) tmsg.getObjectProperty("subject");
						String datetemp = (String) tmsg.getObjectProperty("date");
						models.Message newMsg = new models.Message(sender,receiver,temp,datetemp,subjecttemp);
						chatMessages.receiveMessage(newMsg);
						response = "message:"+newMsg.toString();
						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "GET":
						
						String  sessionId = (String) tmsg.getObjectProperty("sessionId");
						String  who = (String) tmsg.getObjectProperty("who");
						
						response = "messages:";
						for (models.Message mess : chatMessages.getChatForUser(who)) {
							response +=  mess.toString()+"|";
						}
						ws.onMessage(sessionId, response);
						break;
					case "NEW_REGISTER":
						sessionId = (String) tmsg.getObjectProperty("sessionId");
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.getUsername() + "|";
						}
						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGIN":
						sessionId = (String) tmsg.getObjectProperty("sessionId");
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.getUsername() + "|";
						}
						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGOUT":
						sessionId = (String) tmsg.getObjectProperty("sessionId");
						response = "loggedInList:";
						List<User> users3 = chatManager.loggedInUsers();
						for (User u : users3) {
							response += u.getUsername() + "|";
						}
						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					
				} catch (JMSException e) {
					e.printStackTrace();
				}
				
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}*/

	@Override
	public String init() {
		agentId = generateId();
//		cachedAgents.addRunningAgent(agentId, this);
		return agentId;
	}

	private String generateId() {
		Random r = new Random();
		int low = 10;
		int high = 100;
		return Integer.toString(r.nextInt(high - low) + low);
	}

	@Override
	public String getAgentId() {
		return agentId;
	}
	
	private void sendMessage(Message msg) {
		TextMessage tmsg = (TextMessage) msg;
		try {
			String receiver = (String) tmsg.getObjectProperty("receiver");
			String sender = (String) tmsg.getObjectProperty("sender");
			String content = (String) tmsg.getObjectProperty("content");
			
			chatMessages.sendMessage(new models.Message(sender,receiver,content));
			AgentMessage forwardMessage = new AgentMessage();
			messageManager.post(forwardMessage);
		} catch(Exception e)
		{
			
		}
		
	}

	@Override
	public void init(String agentId) {
		this.agentId = agentId;
	}
}
