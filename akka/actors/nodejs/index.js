const axios = require("axios")

setInterval(() => {
    for (let i = 0; i < 1000; i++) {
        axios.get("http://localhost:8080/messages?message=" + i.toString())
            .catch(err => console.error(err));
    }
}, 1000)