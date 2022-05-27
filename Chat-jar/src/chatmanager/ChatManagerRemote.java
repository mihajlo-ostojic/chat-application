package chatmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

import models.Message;
import models.User;

@Remote
public interface ChatManagerRemote {

	public boolean login(String username, String password);

	public boolean register(User user);

	public List<User> loggedInUsers();
	
	public List<User> regeisteredUsers();
	
	public boolean logout(String username, String password);
	
	public String getSessionId(String username);
	
}
