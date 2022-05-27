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
			System.out.println("salje se poruka za "+receiver);
			HashMap<String,Agent> angenti = cachedAgents.getRunningAgents();
			ArrayList<String> keyList = new ArrayList<String>(cachedAgents.getRunningAgents().keySet());
			Agent agent = (Agent) cachedAgents.getRunningAgents().get(receiver);
			agent.handleMessage(message);
			
		

		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
