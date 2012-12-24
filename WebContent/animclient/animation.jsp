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
	 <script id="funsion" type="text/javascript" charset="utf-8" 
  				 src="http://fusion.qq.com/fusion_loader?appid=801281774&platform=qzone">
	 </script>
	 
	 <script type="text/javascript">
	 
		function changePlatform(pf) {
			 var o = document.getElementById('funsion');
			 o.setAttribute('src', 'http://fusion.qq.com/fusion_loader?appid=801281774&platform='+pf);
			 console.log(o.getAttribute('src'));
		}
    
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
			
			//取参数赋值script加载funsion
			changePlatform(pf);
			
			//调用v3/user/get_info接口返回用户状态
			$.post("/watui/watuiapi", {
				'method' : 'getTendentUser',
				'openId' : openid,
				'openKey' : openkey,
				'pf' : pf,
				'pfKey' : pfkey
				
			}, function(result) {
				
				//console.log(result);
				operateRet(result,openkey,pf);
				
			},"json");
		}
		
		//处理返回状态码
		function operateRet(result,openkey,pf){
			var ret = result.ret;
			if(ret == 0){
				
				createWatui(result.userId,openkey);
				
			}else if(ret == 1002){
				//用户没有登录态
				fusion2.dialog.relogin();
			}
		}

		
		function getQueryStringRegExp(name) { 
			var reg = new RegExp("(^|\\?|&)"+ name +"=([^&]*)(\\s|&|$)", "i"); 
			if (reg.test(location.href)) return unescape(RegExp.$2.replace(/\+/g, " ")); return ""; 
		}; 
		
		function createWatui(userId,pf){
			var flashvars = {};
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
			swfobject.embedSWF("Watui.swf?v=1.0&date=1204", divId, 760, 600, swfVersionStr, xiSwfUrlStr,
					flashvars, params, attributes);
			swfobject.createCSS("#" + divId, "display:block;text-align:left;");
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

<body onload ="getTendentUser();">
      
    <div id="flashContent">
        <p>
            To view this page ensure that Adobe Flash Player version need flash player11...
        </p>
       
    </div>
        
</body>
</html>
