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
		
		//存或更新用户
		function getTendentUser() {
			var openid = getHttpParams("openid");
			var openkey = getHttpParams("openkey");
			var pf = getHttpParams("pf");
			var pfkey = getHttpParams("pfkey");
			console.log(openid+"========"+openkey);
			
			$.post("/watui/watuiapi", {
				'method' : 'getTendentUser',
				'openId' : openid,
				'openKey' : openkey,
				'pf' : pf,
				'pfKey' : pfkey
			}, function(result) {
				console.log(result);
			},"json");
		}

		
		function getQueryStringRegExp(name) { 
			var reg = new RegExp("(^|\\?|&)"+ name +"=([^&]*)(\\s|&|$)", "i"); 
			if (reg.test(location.href)) return unescape(RegExp.$2.replace(/\+/g, " ")); return ""; 
		}; 
    
        // For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection. 
        var swfVersionStr = "11.1.0";
        // To use express install, set to playerProductInstall.swf, otherwise the empty string. 
        var xiSwfUrlStr = "playerProductInstall.swf";
        
        var flashvars = {};
        flashvars.userId = getQueryStringRegExp('userId');
        flashvars.animId = getQueryStringRegExp('animId');                     
        flashvars.debug = 'true';                     
        
        var params = {};
        params.quality = "high";
        params.bgcolor = "#ffffff";
        params.allowscriptaccess = "sameDomain";
        params.allowfullscreen = "true";
        var attributes = {};
        attributes.id = "Watui";
        attributes.name = "Watui";
        attributes.align = "middle";
        swfobject.embedSWF(
            "Watui.swf", "flashContent", 
            "760", "600", 
            swfVersionStr, xiSwfUrlStr, 
            flashvars, params, attributes);
        // JavaScript enabled so display the flashContent div in case it is not replaced with a swf object.
        swfobject.createCSS("#flashContent", "display:block;text-align:left;");
    </script>
</head>

<body onload ="getTendentUser();">
      
    <div id="flashContent">
        <p>
            To view this page ensure that Adobe Flash Player version need flash player11...
        </p>
       
    </div>
        
</body>
</html>
