package rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.Message;
import models.User;

@Stateless
@Path("/chat")
public class ChatRestBean implements ChatRest {

	@EJB
	private MessageManagerRemote messageManager;
	
	@Override
	public void register(User user) {
		AgentMessage message = new AgentMessage();
		System.out.println("Register:");
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "REGISTER");
		message.userArgs.put("username", user.getUsername());
		message.userArgs.put("password", user.getPassword());
		message.userArgs.put("sessionId", user.id);
		
//		String sessionId = (String) tmsg.getObjectProperty("sessionId");
		
		System.out.println("ko salje: "+user.getUsername());
		
		messageManager.post(message);
	}

	@Override
	public void login(User user) {
		AgentMessage message = new AgentMessage();
		
		message.userArgs.put("sender", user.getUsername());
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "LOG_IN");
		message.userArgs.put("username", user.getUsername());
		message.userArgs.put("password", user.getPassword());
		message.userArgs.put("sessionId", user.id);
		System.out.println("Login:");
		System.out.println("ko salje: "+user.getUsername());
		System.out.println("kome salje: chat");
		System.out.println("session id: " + user.id);
		
		messageManager.post(message);
	}

	@Override
	public void getloggedInUsers(String id) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "GET_LOGGEDIN");
		message.userArgs.put("sessionId",id);
		
		System.out.println("Neko trazi registrovane session id: " + id);
		messageManager.post(message);
	}
	
	@Override
	public void getRegisteredUsers(String id) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "GET_REGISTERED");
		message.userArgs.put("sessionId",id);
		System.out.println("Neko trazi registrovane logiovane id: " + id);
		messageManager.post(message);
	}

	@Override
	public void sendToAll(Message msg) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("sessionId",msg.getId());
		message.userArgs.put("sender", msg.getSender());
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "SEND_ALL");
		message.userArgs.put("content", msg.getContent());
		LocalDateTime now = LocalDateTime.now();  
        System.out.println("Before Formatting: " + now);  
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
        String formatDateTime = now.format(format);  
        System.out.println("After Formatting: " + formatDateTime);  
		message.userArgs.put("date", formatDateTime);
		message.userArgs.put("subject", msg.getSubject());
		
		messageManager.post(message);
	}

	@Override
	public void sendToUser(Message msg) {
		AgentMessage message = new AgentMessage();

		message.userArgs.put("sender", msg.getSender());
		message.userArgs.put("receiver", msg.getReciver());
		message.userArgs.put("command", "RECIVE_MESSAGE");
		message.userArgs.put("content", msg.getContent());
		LocalDateTime now = LocalDateTime.now();  
        System.out.println("Before Formatting: " + now);  
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
        String formatDateTime = now.format(format);  
        System.out.println("After Formatting: " + formatDateTime);  
		message.userArgs.put("date", formatDateTime);
		message.userArgs.put("subject", msg.getSubject());
		System.out.println("ko salje: "+msg.getSender());
		System.out.println("kome salje: "+msg.getReciver());
		System.out.println("sta salje: "+msg.getContent());
		System.out.println("kada salje: "+msg.getDate().toString());
		System.out.println("zasto salje: "+msg.getSubject());
		
		messageManager.post(message);
		
	}

	@Override
	public void getUserMessages(Message msg) {
		AgentMessage message = new AgentMessage();
		
		message.userArgs.put("sender", msg.getSender());
		message.userArgs.put("receiver", msg.getReciver());
		message.userArgs.put("command", "GET");
		message.userArgs.put("sessionId", msg.getId());
		
		messageManager.post(message);
	}


	public void logOut(User user) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "LOGOUT");
		message.userArgs.put("username", user.getUsername());
		message.userArgs.put("password", user.getPassword());
		messageManager.post(message);
	}

}
