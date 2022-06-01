import { Injectable } from '@angular/core';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { UserserService } from './userser.service';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  constructor(private http : HttpClient, private userService: UserserService) { }

  send(sender : string, receiver : string, content : string,subject : string) {
    console.log("sender: "+sender);
    console.log("receiver: "+receiver);
    console.log("content: "+content);
    console.log("subject: "+subject);
    
    
    var data:any = new Object();
    data['sender'] = sender;
    data['reciver'] = receiver;
    data['content'] = content;
    data['date'] = "date"
    data['subject'] = subject;
    data['id'] = localStorage.getItem("sessionId");
    
    this.http.post("http://localhost:8080/Chat-war/api/chat/user", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  sendToAll(sender : string, content : string,subject : string) {
    console.log("salje nesto")
    console.log("sender: "+sender);
    console.log("content: "+content);
    console.log("subject: "+subject);
    var data:any = new Object();
    data['sender'] = sender;
    data['reciver'] = "all";
    data['content'] = content;
    data['date'] = "date"
    data['subject'] = subject;
    data['id'] = localStorage.getItem("sessionId");
    this.http.post("http://localhost:8080/Chat-war/api/chat/all", data, {headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  getMessages() {
    var data:any = new Object();
    data['sender'] = this.userService.getUsername();
    data['reciver'] = this.userService.getUsername();
    data['content'] = "content";
    data['date'] = "date"
    data['subject'] = "subject";
    data['id'] = localStorage.getItem("sessionId");
    this.http.post("http://localhost:8080/Chat-war/api/chat/get" ,data ,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  getMessagesForUsername(username:string) {
    var data:any = new Object();
    data['sender'] = username;
    data['reciver'] = this.userService.getUsername();
    data['content'] = "content";
    data['date'] = "date"
    data['subject'] = "subject";
    data['id'] = localStorage.getItem("sessionId");
    this.http.post("http://localhost:8080/Chat-war/api/chat/get" ,data ,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }
}
