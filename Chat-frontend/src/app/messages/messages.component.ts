import { Component, Input, OnInit, SimpleChanges, ViewChild  } from '@angular/core';
import { MessagesService } from '../services/messages.service';
import { WebsocketService } from '../services/websocket.service';
import { UserserService } from '../services/userser.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit {

  constructor(private messageService : MessagesService, private wsService : WebsocketService, private userService : UserserService) { }

  liveData$ = this.wsService.messages$;

  @Input() selected : string = "";

  public allMessages : dto[] = [];
  public displayedMessages : dto[] = [];

  public subject : string = "enter subject here";
  public content : string = "enter text here";

  displayedColumns: string[] = ['date', 'message'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  dataSource = new MatTableDataSource<dto>(this.displayedMessages);

  ngOnInit(): void {
    this.liveData$.subscribe({
      next : msg => this.handleMessage(msg as string)
    });
    this.wsService.connect();
    setTimeout(() => {this.messageService.getMessages()}, 500)
  }

  ngOnChanges(changes: SimpleChanges) {
    this.setUpDisplayedMessages();
  }

  setUpDisplayedMessages() {
    this.displayedMessages = [];
    this.filterMessagesBySelectedUser();
    this.dataSource.data = this.displayedMessages;
    this.dataSource.paginator = this.paginator;
  }

  

  filterMessagesBySelectedUser() {
    for(var message of this.allMessages) {
      if(message.otherUsername == this.selected) {
        this.displayedMessages.push(message);
      }
    }
  }

  handleMessage(message : string) {
    if(message.match('.+:.*')) {
      var type = message.split(':')[0];
      if(type == 'messages') {
        this.handleMessages(message);
      }
      else if(type == 'message') {
        this.handleNewMessage(message);
      }
    }
  }

  handleMessages(message : string) {
    console.log("Stigle poruke: " + message);
    var all = message.split('|');
    var split;
    let data:dto;
    // sender+","+reciver+","+content+","+date+","+subject
    for (let msg in all)
    {
      split = msg.split(",")
      data = new dto(split[1],true,split[4],split[2],split[3])
      this.allMessages.push(data);
    }
    
    this.setUpDisplayedMessages();
  }

  handleNewMessage(message : string) {
    console.log("Stigla poruka: " + message);
    var all = message.split(',');
    let data:dto;
    data = new dto(all[1],true,all[4],all[2],all[3])
    this.allMessages.push(data);
    this.setUpDisplayedMessages();
  }

  send() {
    console.log("selektovano: "+this.selected)
    this.messageService.send(this.userService.getUsername(),this.selected, this.content,this.subject)
    this.subject = ""
    this.content = ""
  }

  sendToAll() {
    this.messageService.sendToAll(this.userService.getUsername(),this.subject, this.content)
    this.subject = ""
    this.content = ""
  }

}

class dto {
  constructor(
      public otherUsername : string,
      public incoming : boolean,
      public subject : string,
      public content : string,
      public dateTime : string
  ) {}
}
