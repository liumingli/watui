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
			String result = apiAdaptor.getAnimByPage(pageNum);
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
			
		}else if (action.equals(AppStarter.GETTENCENTUSER)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String openId = req.getParameter("openId");
			String openKey = req.getParameter("openKey");
			String pf = req.getParameter("pf");
			String pfKey = req.getParameter("pfKey");
		    String result= apiAdaptor.getTencentUser(openId,openKey,pf,pfKey);
			log.info(result);
			pw.print(result);
			pw.close();
			
		}else{
			
		}

	}

	public void setApiAdaptor(ApiAdaptor apiAdaptor) {
		this.apiAdaptor = apiAdaptor;
	}

}
