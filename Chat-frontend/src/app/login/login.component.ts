import { Component, OnInit } from '@angular/core';
import { UserserService } from '../services/userser.service';
import { WebsocketService } from '../services/websocket.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public username : string;
  public password : string;

  liveData$ = this.wsService.messages$;

  constructor( private wsService : WebsocketService, private userService : UserserService, private router : Router) { 

    this.username = "";
    this.password = "";
    
    this.liveData$.subscribe({
      next : msg => this.handleMessage(msg as string)
    });

  }

  ngOnInit(): void {
    this.wsService.connect();

  }

  login() {
    this.userService.login(this.username, this.password);
  }

  signup() {
    this.userService.signup(this.username, this.password);
  }

  handleMessage(message : string) {
    if(message.match('.+:.*')) {
      var type = message.split(':')[0];
      var content = message.split(':')[1];
      if(type == 'login')
        this.handleLogin(content);
      else if(type == 'register')
        this.handleRegistration(content);
    }
  }

  handleLogin(message : string) {
    if(message.startsWith('OK ')) {
      localStorage.setItem('identifier', message.substring(3));
      this.router.navigate(['chat']);
    }
    else {
      console.log("login handle")
    }

    console.log("login prosao!"+message);
  }

  handleRegistration(message : string) {
    console.log("registration handle:" + message)
    console.log("register prosao!"+message);
  }

}
