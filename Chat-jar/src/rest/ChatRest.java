package rest;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import models.User;

@Remote
public interface ChatRest {
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public void register(User user);
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public void login(User user);

	
	@GET
	@Path("/loggedIn")
	public void getloggedInUsers();
	
	@GET
	@Path("/registered")
	public void getRegisteredUsers();
	
	@POST
	@Path("/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendToAll();
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendToUser();
	
	@GET
	@Path("/{user}")
	public void getUserMessages();
	
	@DELETE
	@Path("/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	public void logOut(User user);

	
	
}
