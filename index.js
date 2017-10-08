var host = process.argv[0] || "localhost";
var port = process.argv[1] || 25565;
var nmp = require("minecraft-protocol");
var mcserver = nmp.createServer({
    host: host,
    port: port
});
var WebSocket = require("ws");
var sqlite3 = require('sqlite3');
var db = new sqlite3.Database("db");
var server = new require("http").createServer();
var wsvr = new WebSocket.Server({
    server: server
});
wsvr.on("connection", function (ws) {
    ws.on("message", (rawd) => {
        var data = JSON.parse(rawd);
        switch (data.name) {
            case "login":
                break;
        }
    });
});
server.listen({
    host: host,
    port: port
});
mcserver.listen(port, host);