var express=require("express");
var bodyParser = require('body-parser');
var app=express();
var port=process.env.PORT||8000;
var fs=require("fs");
app.listen(port);


app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies
app.post('/', function(req, res) {
    var x_co = req.body.x;
    var y_co = req.body.y;
    var z_co = req.body.z;
    fs.appendFile('data.txt', x_co+" "+y_co+" "+z_co+"\n", encoding='utf8', function (err) {
    	if (err) console.log(err);
	});
    res.send(x_co + ' ' + y_co + ' ' + z_co);
    console.log(x_co + ' ' + y_co + ' ' + z_co);
});

app.get('/data.txt',function(req,res){
	fs.readFile('data.txt','utf8',function(err,contents){
		res.send(contents);
	});
});
console.log("Server is started");