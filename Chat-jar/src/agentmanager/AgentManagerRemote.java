package agentmanager;

import javax.ejb.Remote;

import agents.Agent;

@Remote
public interface AgentManagerRemote {
	public String startAgent(String name);
	public void startAgent(String agentId, String name);
	public Agent getAgentById(String agentId);
	public void stopAgent(String agentId);
}
