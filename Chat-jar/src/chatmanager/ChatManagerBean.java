package chatmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateful;

import agentmanager.AgentManagerRemote;
import models.Message;
import models.User;
import util.JNDILookup;

// TODO Implement the rest of Client-Server functionalities 
/**
 * Session Bean implementation class ChatBean
 */
@Singleton
@LocalBean
public class ChatManagerBean implements ChatManagerRemote, ChatManagerLocal {

	private List<User> registered = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	
	
	@EJB
	private AgentManagerRemote agentManager;
	/**
	 * Default constructor.
	 */
	public ChatManagerBean() {

	}
	
	public String getSessionId(String username)
	{
		for (User u : loggedIn) {
			if(u.getUsername()==username) return u.getId();
		}
		return "";
	}
	

	@Override
	public boolean register(User user) {
		for (User u : registered) {
			if(u.getUsername().equals(user.getUsername()))
			{
				return false;
			}
		}
		registered.add(user);
		return true;
	}

	@Override
	public boolean login(String username, String password, String id) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists) {
			
			for (User u : loggedIn) {
				if(u.getUsername()==username) return false;
			}
			loggedIn.add(new User(username, password, id));
			agentManager.startAgent(username,JNDILookup.UserAgentLookup);
			
			return true;
		}
		return exists;
	}

	@Override
	public List<User> loggedInUsers() {
		return loggedIn;
	}
	
	@Override
	public List<User> regeisteredUsers() {
		return registered;
	}
	
	@Override
	public boolean logout(String username, String password) {
		System.err.println("Username za logout: "+ username);
		
		User foundUser = null;
		for (User user : loggedIn) {
			if(user.getUsername().equals(username))
			{
				foundUser = user;
			}
		}
		if(foundUser!=null) {
			loggedIn.remove(foundUser);
			System.out.println("User romeved from logedIn");
			return true;
		}
		return false;
	
	}

}
