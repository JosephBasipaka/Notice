import { Component, OnInit } from '@angular/core';
import { Notice } from '../Notice';

@Component({
  selector: 'app-noticeboard',
  templateUrl: './noticeboard.component.html',
  styleUrls: ['./noticeboard.component.css']
})
export class NoticeboardComponent {

  notices: Notice[] = [];
  newNotice: Notice = new Notice(0, '', '');

  ngOnInit() {
    const storedData = localStorage.getItem('notices');
    if (storedData) {
      this.notices = JSON.parse(storedData);
    }
  }
  
  addNotice() {
    if (this.notices.length < 6) {
      this.notices.push(this.newNotice);
    } else {
      this.notices.shift();
      this.notices.push(this.newNotice);
    }
    localStorage.setItem('notices', JSON.stringify(this.notices));
    this.newNotice = new Notice(0, '', '');
  }

}
