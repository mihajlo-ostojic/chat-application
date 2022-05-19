package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private User receiver;
	private User sender;
	private LocalDateTime created;
	private String subject;
	private String content;
	
	public Message()
	{}
	
	public User getReceiver() {
		return receiver;
	}
	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
