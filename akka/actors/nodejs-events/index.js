const EventEmitter = require('events');
const express = require('express');
const app = express();
const port = 8080;

class State {
    constructor() {
        this.requestsCounter = 0;
        this.messagesHandledCounter = 0;
    }

    getRequestsCounter() { return this.requestsCounter; }
    incrementRequestsCounter() { this.requestsCounter += 1; }
    getMessagesHandledCounter() { return this.messagesHandledCounter; } 
    incrementMessagesHandledCounter() { this.messagesHandledCounter += 1; }
    reset() {
        this.requestsCounter = 0;
        this.messagesHandledCounter = 0;
    }
}

const state = new State();
setInterval(() => {
    console.log(`\nRequests: ${state.getRequestsCounter()}`);
    console.log(`Message: ${state.getMessagesHandledCounter()}`);
    state.reset()
}, 1000);

class MessagingEmitter extends EventEmitter {};
const messagingEmitter = new MessagingEmitter();
messagingEmitter.on("message", _ => state.incrementMessagesHandledCounter());

app.get('/messages', (req, res) => {
    state.incrementRequestsCounter();
    messagingEmitter.emit("message", req.query.message);
    res.send(`message ${req.query.message}`);
});

app.listen(port, () => console.log(`Example app listening on port ${port}!`))