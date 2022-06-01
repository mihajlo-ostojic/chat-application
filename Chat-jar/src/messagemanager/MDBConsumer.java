package messagemanager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import agents.Agent;
import agents.CachedAgentsRemote;

/**
 * Message-Driven Bean implementation class for: MDBConsumer
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class MDBConsumer implements MessageListener {


	@EJB
	private CachedAgentsRemote cachedAgents;
	/**
	 * Default constructor.
	 */
	public MDBConsumer() {

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		String receiver;
		try {
			receiver = (String) message.getObjectProperty("receiver");
			String sender = (String)  message.getObjectProperty("sender");
			String content = (String) message.getObjectProperty("content");
			String date = (String) message.getObjectProperty("date");
			String subject = (String) message.getObjectProperty("subject");
			String command = (String) message.getObjectProperty("command");
			String sessionId = (String) message.getObjectProperty("sessionId");
			String username = (String) message.getObjectProperty("username");
			String password = (String) message.getObjectProperty("password");
			System.out.println("salje se poruka za "+receiver);
			HashMap<String,Agent> angenti = cachedAgents.getRunningAgents();
			ArrayList<String> keyList = new ArrayList<String>(cachedAgents.getRunningAgents().keySet());
			AgentMessage newMsg = new AgentMessage();
			newMsg.userArgs.put("command", command);
			newMsg.userArgs.put("receiver", receiver);
			newMsg.userArgs.put("sender", sender);
			newMsg.userArgs.put("content", content);
			newMsg.userArgs.put("date", date);
			newMsg.userArgs.put("subject", subject);
			newMsg.userArgs.put("sessionId", sessionId);
			newMsg.userArgs.put("username", username);
			newMsg.userArgs.put("password", password);
			Agent agent = (Agent) cachedAgents.getRunningAgents().get(receiver);
			agent.handleMessage(newMsg);
			
		

		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
