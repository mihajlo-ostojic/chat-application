package agents;

import java.io.Serializable;

import javax.jms.Message;

import messagemanager.AgentMessage;

public interface Agent extends Serializable {

	public String init();
	public void init(String agentId);
	public void handleMessage(AgentMessage message);
	public String getAgentId();
}
