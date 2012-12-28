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
	 	 	var name = getHttpParams("name");
	 	 	var nick = getHttpParams("nick");
	 	 	var oauth2token = getHttpParams("oauth2atoken");
	 	 	
	 	 	var strs = new Array();
	 	 	strs = oauth2token.split("&");
	 	 	for(var i=0;i<strs.length;i++){
	 	 		console.log(strs[i]);
	 	 	}
	 	 };
		
		
		function createWatui(userId,pf){
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
    
    <form action="https://open.t.qq.com/cgi-bin/oauth2/authorize"
		method="get" target="_top" style="display: none;" id="form1">
		<input type="hidden" id="redirect_uri" name="redirect_uri" value="http://diy.produ.cn/watui/animclient/comic.jsp"/> 
		<input type="hidden" id="client_id" name="client_id"  value="801281774"/> 
		<input type="hidden" id="response_type" name="response_type" value="code"/> 
	</form>
        
</body>
</html>
