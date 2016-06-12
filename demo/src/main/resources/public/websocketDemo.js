//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    data.userlist.forEach(function (user) {
        insert("userlist", "<li>" + user + "</li>");
    });
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

//图片上传预览
function previewImage(file)
{
  var MAXWIDTH  = 350; 
  var MAXHEIGHT = 250;
  var div = document.getElementById('preview');
  if (file.files && file.files[0])
  {
	  var imageType=/image*/;
	  var imageFile=file.files[0];
	  if(!imageFile.type.match(imageType)){
		  alert("please choose valid image again");
		  return;
	  }
	  
      div.innerHTML ='<img id=imghead>';
      var img = document.getElementById('imghead');
      img.onload = function(){
        var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
        img.width  =  rect.width;
        img.height =  rect.height;
         img.style.marginLeft ='5px';
        img.style.marginTop = '5px';
      }
      var reader = new FileReader();
      reader.onload = function(evt){
    	  div.style.display="block";
    	  img.src = evt.target.result;
    	  }
      reader.readAsDataURL(file.files[0]);
  }
}
//对图片进行等比例压缩
function clacImgZoomParam( maxWidth, maxHeight, width, height ){
    var param = {top:0, left:0, width:width, height:height};
    if( width>maxWidth || height>maxHeight )
    {
        rateWidth = width / maxWidth;
        rateHeight = height / maxHeight;
         
        if( rateWidth > rateHeight )
        {
            param.width =  maxWidth;
            param.height = Math.round(height / rateWidth);
        }else
        {
            param.width = Math.round(width / rateHeight);
            param.height = maxHeight;
        }
    }
     
    param.left = Math.round((maxWidth - param.width) / 2);
    param.top = Math.round((maxHeight - param.height) / 2);
    return param;
}

//var imageWebSocket=new WebSocket("ws://" + location.hostname + ":" + location.port + "/image/");
id("imageSend").addEventListener("click", function(e) {
	var inputFile=document.getElementById("imageFile");
	var imageFile=inputFile.files[0];
	var reader=new FileReader();
	reader.onload=function(evt){
		var byteString=this.result;
		webSocket.send(byteString);
		console.log("发送图片成功，图片大小："+imageFile.size)
	}
	reader.readAsDataURL(imageFile);
	var div = document.getElementById('preview');
	div.style.display="none";
})
