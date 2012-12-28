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
		
		function checkOAuth(){
			var code = getHttpParams("code");
			console.log("code------------------------"+code);
		
			if(code != ""){
				//没有取到code去走授权流程
				//generateTappOauth();
				$('#form1').submit();
			}else{
				//取到code后，请求accesstoken
				var openid = getHttpParams("openid");
				var openkey = getHttpParams("openkey");
				getTendentUser(code,openid,openkey);
			}
		}
		
		function generateTappOauth(){
			alert(1);
			$.post("/watui/watuiapi", {
				'method' : 'generateTappOauth'
				
			}, function(result) {
				
			});
		}
		
		//存或更新用户
		function getTendentUser(code,openid,openkey) {
			
			//调用v3/user/get_info接口返回用户状态
			$.post("/watui/watuiapi", {
				'method' : 'getTappUser',
				'code' : code,
				'openId' : openid,
				'openKey' : openkey
				
			}, function(result) {
				
				console.log(result);
				
			},"json");
		}
		
		
		function createWatui(){
			var flashvars = {};
			
			flashvars.debug = 'true'; 
			var userId = "abc";
			flashvars.userId = userId;
			var pf="tapp";
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
		
		function share(){
			fusion2.dialog.sendStory({
				title : "调试应用",
				img : "http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png",
				summary : "腾讯开放平台全新调试工具现已提供，调试应用更方便！",
				msg : "我刚刚试用了全新调试工具，果然名不虚传，你也来试试吧~",
				context : "send-story",
				onSuccess : function (opt) { console.log("发送成功: " + fusion.JSON.stringify(opt)); },
				onCancel : function (opt) { console.log("用户取消: " + fusion.JSON.stringify(opt)); },
				onClose : function (opt) { console.log("浮层关闭: " + fusion.JSON.stringify(opt)); }
			});
		}
    </script>
</head>

<body onload ="createWatui();">
      
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
