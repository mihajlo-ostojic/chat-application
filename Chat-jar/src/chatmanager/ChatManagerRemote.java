package chatmanager;

import java.util.List;

import javax.ejb.Remote;

import models.User;

@Remote
public interface ChatManagerRemote {

	public boolean login(String username, String password);

	public boolean register(User user);

	public List<User> loggedInUsers();
	
	public List<User> regeisteredUsers();
	
	public boolean logout(String username, String password);

}
