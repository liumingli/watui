package com.ybcx.watui.facade;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class AssistProcess {
	
	// 由Spring注入
	private ApiAdaptor apiAdaptor;


	private Logger log = Logger.getLogger(AssistProcess.class);
	
	/**
	 * 处理正常用户登录post的请求
	 * 
	 * @param action
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPostProcess(String action, HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {

		// 安全验证：如果不是上传文件请求，取用户id参数，判断是否为正常用户
		if(!GlobalController.isDebug){
		}

		if (action.equals(AppStarter.GETANIMATIONSOF)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			String result= apiAdaptor.getAnimationsOf(userId);
			log.debug(result);
			pw.print(result);
			pw.close();;
				
		}else if (action.equals(AppStarter.GETANIMBYPAGE)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String pageNum = req.getParameter("pageNum");
			String pageSize = req.getParameter("pageSize");
			String result = apiAdaptor.getAnimByPage(pageNum,pageSize);
			log.debug(result);
			pw.print(result);
			pw.close();
			
		}else if (action.equals(AppStarter.GETWEBANIM)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String pageNum = req.getParameter("pageNum");
			String pageSize = req.getParameter("pageSize");
			String callBack = req.getParameter("callback");
			String result = callBack+"("+apiAdaptor.getAnimByPage(pageNum,pageSize)+")";
			log.debug(result);
			pw.print(result);
			pw.close();
			
		}else if (action.equals(AppStarter.GETPRIMARYCOUNT)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
		    int result= apiAdaptor.getYonkomaCount("parent");
			log.info(result);
			pw.print(result);
			pw.close();
			
			
		}else if (action.equals(AppStarter.GETPRIMARYBYPAGE)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String pageSize = req.getParameter("pageSize");
			String pageNum = req.getParameter("pageNum");
		    String result= apiAdaptor.getYonkomaByPage("parent",pageSize,pageNum);
			log.info(result);
			pw.print(result);
			pw.close();
			
		}else if (action.equals(AppStarter.GETENDINGCOUNTBYPRIMARY)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String primary = req.getParameter("primary");
		    int result= apiAdaptor.getYonkomaCount(primary);
			log.info(result);
			pw.print(result);
			pw.close();
			
		}else if (action.equals(AppStarter.GETENDINGBYPRIMARYANDPAGE)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			//主动画id--primary
			String primary = req.getParameter("primary");
			String pageSize = req.getParameter("pageSize");
			String pageNum = req.getParameter("pageNum");
		    String result= apiAdaptor.getYonkomaByPage(primary,pageSize,pageNum);
			log.info(result);
			pw.print(result);
			pw.close();
			
		//操作腾讯微博用户
		}else if (action.equals(AppStarter.OPERATETAPPUSER)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();	
			String openId = req.getParameter("openId");
			String nickName = req.getParameter("nick");
			String accessToken = req.getParameter("accessToken");
			String pf = req.getParameter("pf");
		    String result= apiAdaptor.operateTappUser(openId,nickName,accessToken,pf);
			log.info(result);
			pw.print(result);
			pw.close();
			
		}else if (action.equals(AppStarter.SHARETOTAPP)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();	
			String type = req.getParameter("type");
			String primaryId = req.getParameter("primaryId");
			String endingId = req.getParameter("endingId");
			String userId = req.getParameter("userId");
			String content = req.getParameter("content");
			String openId = req.getParameter("openId");
			String openKey = req.getParameter("openKey");
			String pf = req.getParameter("pf");
			String ip = req.getParameter("ip");
		    String result= apiAdaptor.shareToTapp(type,primaryId,endingId,userId,content,openId,openKey,pf,ip);
			log.info(result);
			pw.print(result);
			pw.close();
			
		}else if (action.equals(AppStarter.GETTAPPUSER)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();	
			String openId = req.getParameter("openId");
			String openKey = req.getParameter("openKey");
			String pf = req.getParameter("pf");
		    String result= apiAdaptor.getTappUser(openId,openKey,pf);
			log.info(result);
			pw.print(result);
			pw.close();
		
		//FIXME 
		//发送视频到腾讯微博
		}else if (action.equals(AppStarter.MOVIECLIPVIDEOTOTAPP)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();	
			log.info("Call method movieClipVideoToTapp>>>>>>");
			String userId = req.getParameter("userId");
			String movieId = req.getParameter("movieId");
			String content = req.getParameter("content");
			String type = req.getParameter("type");
			String url = req.getParameter("url");
			String openId = req.getParameter("openId");
			String openKey = req.getParameter("openKey");
			String pf = req.getParameter("pf");
			String ip = req.getParameter("ip");
		    String result= apiAdaptor.movieClipVideoToTapp(userId,movieId,content,type,url,openId,openKey,pf,ip);
			log.info(result);
			log.info("The movieClipVideoToTapp return is "+result);
			pw.print(result);
			pw.close();
			
		}else{
			
		}

	}

	public void setApiAdaptor(ApiAdaptor apiAdaptor) {
		this.apiAdaptor = apiAdaptor;
	}

}
