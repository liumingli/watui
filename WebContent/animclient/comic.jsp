<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>指尖动漫--指尖上的动漫创作与分享工具</title>
<meta name="google" value="notranslate" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<style type="text/css" media="screen"> 
	    html, body  { height:100%; }
	    body { margin:0; padding:0; overflow:auto; text-align:center; 
	           background-color: #ffffff; }   
	    object:focus { outline:none; }
	    #flashContent { display:none; }
	</style>
     <script type="text/javascript" src="jquery.js"></script>   
	 <script type="text/javascript" src="swfobject.js"></script>
	 
	 <script type="text/javascript">
	 
    
		function getHttpParams(name)
		 {
		      var r = new RegExp("(\\?|#|&)"+name+"=([^&#]*)(&|#|$)");
		      var m = location.href.match(r);
		      return decodeURIComponent(!m?"":m[2]);
		 }
		
	 	 window.onload = function(){
	 		var openid = getHttpParams("openid");
			var openkey = getHttpParams("openkey");
			console.log(openid+"----------"+openkey);
	 	 	var nick = getHttpParams("nick");
	 	 	var pf = getHttpParams("pf");
	 	 	var oauth2token = getHttpParams("oauth2atoken");
// 	 	 	console.log(oauth2token);
	 	 	
	 	 	var strs = new Array();
	 	 	strs = oauth2token.split("&");
// 	 	 	for(var i=0;i<strs.length;i++){
// 	 	 		console.log(strs[i]);
// 	 	 	}
	 	 	var str = strs[0];
	 	 	var arr = new Array();
	 	 	arr = str.split("=");
	 	 	var accessToken = arr[1];
// 	 	 	console.log(accessToken);
	 	 	
	 	 	if(openid != null && openid !=""&& openkey != null && openkey !=""){
	 	 		//操作用户，正常后返回id，否则返回false
	 	 	  	operateTappUser(nick,pf,accessToken,openid,openkey);
	 	 	}
	 	 };
	 	 
	 	 function operateTappUser(nick,pf,accessToken,openid,openkey){
	 		$.post("/watui/watuiapi", {
				'method' : 'operateTappUser',
				'openId' : openid,
				'nick' : nick,
				'accessToken' : accessToken,
				'pf' : pf
				
			}, function(result) {
				var userId = result;
// 				console.log(userId);
				if(result != "false"){
					 createWatui(userId,pf,openid,openkey);
				}
			});
	 		 
	 	 }
		
		
		function createWatui(userId,pf,openid,openkey){
			var flashvars = {};
			
			flashvars.debug = 'true'; 
			var userId = "abc";
			flashvars.userId = userId;
			flashvars.pf = pf;
			
			var swfVersionStr = "11.1.0";
			var xiSwfUrlStr = "playerProductInstall.swf";
			var params = {};
			params.quality = "high";
			params.bgcolor = "#ffffff";
			params.allowscriptaccess = "always";
			params.allowfullscreen = "true";
			params.wmode = "opaque";
			var attributes = {};
			attributes.id = "flashContent";
			attributes.name = "flashContent";
			attributes.align = "middle";
			swfobject.embedSWF("Watui.swf?v=1.03&date=1227", "flashContent", 760, 600, swfVersionStr, xiSwfUrlStr,
					flashvars, params, attributes);
			swfobject.createCSS("#flashContent", "display:block;text-align:left;");
		}
		
    </script>
</head>

<body>
      
    <div id="flashContent">
        <p>
            To view this page ensure that Adobe Flash Player version need flash player11...
        </p>
       
    </div>
        
</body>
</html>
