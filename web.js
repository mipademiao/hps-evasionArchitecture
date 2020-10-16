var http = require('http');
var fs = require('fs');
var net = require('net');
var rl = require('readline');

var dataport = parseInt(process.argv[2]);
var webport = parseInt(process.argv[3]);

console.log('starting with args ' + dataport + ' ' + webport);

var webserver = http.createServer(function (request, response)
{
  fs.readFile('index.html', 'utf-8', function (error, data)
  {
    response.writeHead(200, {'Content-Type': 'text/html'})
    response.end(data);
  });
  console.log('web listening on: ' + webport);
}).listen(webport);

var io = require('socket.io')(webserver);

var hunter = "";
var prey = "";
var results = [];
var finalresult = "";
var state = "";

net.createServer(function (socket) {
  var i = rl.createInterface(socket, socket);
  i.on('line', function (line) {
    console.log(line);
    if(line.startsWith("hunter: ")){
      hunter = line.substring(8);
      finalresult = "";
    } else if(line.startsWith("prey: ")) {
      prey = line.substring(6);
      finalresult = "";
    } else if(line.startsWith("result: ")) {
      results.push(line.substring(8));
      finalresult = "";
    } else if(line.startsWith("finalresult: ")) {
      finalresult = line.substring(13);
    } else if(line == "done") {
    } else if(line == "begin") {
      hunter = "";
      prey = "";
      results = [];
      finalresult = "";
      state = "";
    } else {
      state = line;
      finalresult = "";
    }
    var obj = {};
    obj.hunter = hunter;
    obj.prey = prey;
    obj.results = results;
    obj.finalresult = finalresult;
    obj.state = state;
    io.sockets.emit('to_client', obj);
  });
  socket.on('error', function(err){
    console.log('socket error:\n${err.stack}');
    socket.close();
  });

  console.log('data listening on: ' + dataport);
}).listen(dataport);
