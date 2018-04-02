import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {count: 0};

    // This binding is necessary to make `this` work in the callback
    this.send = this.send.bind(this);
  }

  componentDidMount() {
    var socket = new SockJS('http://localhost:7373/stompEndpoint');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
      console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/panelEvents', function (value) {
        document.getElementById('message').innerHTML = value.body
      });
      stompClient.send('/buttonPanel/fullLoad', {}, '');
    });

    this.stompClient = stompClient;
  }

  send(){
    console.log("Sending.. "+this.state.count);
    this.stompClient.send('/buttonPanel/acknowledge', {}, this.state.count);
    this.setState({count: this.state.count + 1});
  }

  render() {
    return (
      <div>
        <div id="message"></div>
        <button onClick={this.send}><h2>Send</h2></button>
      </div>
    );
  }
}

export default App;
