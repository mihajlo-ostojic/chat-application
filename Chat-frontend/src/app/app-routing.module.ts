import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ChatlistsComponent } from './chatlists/chatlists.component';

const routes: Routes = [
  {path:'chat', component: ChatlistsComponent},
  {path:'', component: LoginComponent}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
