package com.ybcx.watui.facade;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.http.HttpClient;
import weibo4j.http.ImageItem;
import weibo4j.model.PostParameter;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;
import com.qq.open.SnsSigCheck;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.ybcx.watui.beans.Cartoon;
import com.ybcx.watui.beans.User;
import com.ybcx.watui.beans.UserDetail;
import com.ybcx.watui.beans.Weibostat;
import com.ybcx.watui.beans.Yonkoma;
import com.ybcx.watui.dao.DBAccessInterface;
import com.ybcx.watui.tools.ImageHelper;
import com.ybcx.watui.tools.SpliceImage;
import com.ybcx.watui.utils.WatuiUtils;

public class ComicServiceImplement implements ComicServiceInterface {

	private Logger log = Logger.getLogger(ComicServiceImplement.class);
	
	// 由Spring注入
	private DBAccessInterface dbVisitor;
	
	public void setDbVisitor(DBAccessInterface dbVisitor) {
		this.dbVisitor = dbVisitor;
	}

	private String imagePath;
	
	@Override
	public void saveImagePathToProcessor(String filePath) {
	//	this.imgProcessor.setImagePath(filePath);
		imagePath = filePath;
	}
	
	private Properties systemConfigurer;

	public void setSystemConfigurer(Properties systemConfigurer) {
		this.systemConfigurer = systemConfigurer;
	}
	
	// 设定输出的类型
	private static final String GIF = "image/gif;charset=UTF-8";

	private static final String JPG = "image/jpeg;charset=UTF-8";

	private static final String PNG = "image/png;charset=UTF-8";
	
	private static final String SWF = "application/x-shockwave-flash;charset=UTF-8";
		
	
	public String createAdImg(FileItem adData) {
		String type = "";
		if (adData != null) {
			String fileName = adData.getName();
			int dotPos = fileName.lastIndexOf(".");
			type = fileName.substring(dotPos);
		}

		Date date = new Date();//获取当前时间
		SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMddHHmmss");
		String newfileName = sdfFileName.format(date);//文件名称
		
		String path = imagePath + File.separator + newfileName + type;
		try {
			BufferedInputStream in = new BufferedInputStream(adData.getInputStream());
			// 获得文件输入流
			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(new File(path)));// 获得文件输出流
			Streams.copy(in, outStream, true);// 开始把文件写到你指定的上传文件夹
		} catch (IOException e) {
			e.printStackTrace();
		}
		//上传成功，则插入数据库
		if (new File(path).exists()) {
			//保存到数据库
			System.out.println("保存成功"+path);
		}
		return path;
	}

	@Override
	public String createAnimation(FileItem shotData, String userId,
			String name, String content) {
		boolean flag = true;
//		String rawPath = this.saveAnimationRaw(shotData);
//		log.info("Animation raw imagePath  is : "+rawPath);
		String thumbnail = this.saveThumbnailOf(shotData);
		
		Cartoon cartoon = generateCartoon(userId,name,content,thumbnail);
		
		int rows = dbVisitor.saveAnimation(cartoon);
		if(rows < 1){
			flag = false;
			return String.valueOf(flag);
		}else{
			return cartoon.getId();
		}
	}

	private Cartoon generateCartoon(String userId,
			String name, String content, String thumbnail) {
		Cartoon cartoon = new Cartoon();
		cartoon.setId(WatuiUtils.generateUID());
		cartoon.setContent(content);
		cartoon.setOwner(userId);
		cartoon.setName(name);
		cartoon.setThumbnail(thumbnail);
		cartoon.setCreateTime( WatuiUtils.getFormatNowTime());
		cartoon.setEnable(1);
		return cartoon;
	}

	private String saveThumbnailOf(FileItem imgData) {
		String fileName = imgData.getName();
		String filePath = imagePath + File.separator + fileName;
		try {
			ImageHelper.handleImage(imgData, 200, 200, filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

//	private String saveAnimationRaw(FileItem imgData) {
//		String fileName = imgData.getName();
//		int position = fileName.lastIndexOf(".");
//		String extend = fileName.substring(position);
//		String newName = fileName.substring(0,position)+"_Raw"+extend;
//		String filePath = imagePath + File.separator + newName;
//		File file = new File(filePath);
//		try {
//			imgData.write(file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return filePath;
//	}

	

	@Override
	public void getThumbnailFile(String relativePath, HttpServletResponse res) {
		try {
			//默认
			File defaultImg = new File(imagePath + File.separator + "default.png");
			InputStream defaultIn = new FileInputStream(defaultImg);
			
			String type = relativePath.substring(relativePath.lastIndexOf(".") + 1);
			File file = new File(imagePath+File.separator+relativePath);
			
			if (file.exists()) {
				InputStream imageIn = new FileInputStream(file);
				if (type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg")) {
					writeJPGImage(imageIn, res, file);
				} else if (type.toLowerCase().equals("png")) {
					writePNGImage(imageIn, res, file);
				} else if (type.toLowerCase().equals("gif")) {
					writeGIFImage(imageIn, res, file);
				} else {
					writePNGImage(defaultIn, res, file);
				}
			} else {
				writePNGImage(defaultIn, res, defaultImg);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void writeJPGImage(InputStream imageIn, HttpServletResponse res, File file) {
		try {
//			res.addHeader("content-length",String.valueOf(file.length()));
			res.setContentType(JPG);
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn);
			// 得到编码后的图片对象
			BufferedImage image = decoder.decodeAsBufferedImage();
			// 得到输出的编码器
			OutputStream out = res.getOutputStream();
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);
			// 对图片进行输出编码
			imageIn.close();
			// 关闭文件流
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writePNGImage(InputStream imageIn, HttpServletResponse res, File file) {
//		res.addHeader("content-length",String.valueOf(file.length()));
		res.setContentType(PNG);
		getOutInfo(imageIn, res);
	}

	private void writeGIFImage(InputStream imageIn, HttpServletResponse res, File file) {
//		res.addHeader("content-length",String.valueOf(file.length()));
		res.setContentType(GIF);
		getOutInfo(imageIn, res);
	}

	private void getOutInfo(InputStream imageIn, HttpServletResponse res) {
		try {
			OutputStream out = res.getOutputStream();
			BufferedInputStream bis = new BufferedInputStream(imageIn);
			// 输入缓冲流
			BufferedOutputStream bos = new BufferedOutputStream(out);
			// 输出缓冲流
			byte data[] = new byte[4096];
			// 缓冲字节数
			int size = 0;
			size = bis.read(data);
			while (size != -1) {
				bos.write(data, 0, size);
				size = bis.read(data);
			}
			bis.close();
			bos.flush();
			// 清空输出缓冲流
			bos.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	@Override
	public void getAssetFile(String relativePath, HttpServletResponse res) {
		try {
			//默认
			File defaultImg = new File(imagePath + File.separator + "default.png");
			InputStream defaultIn = new FileInputStream(defaultImg);
			
			String type = relativePath.substring(relativePath.lastIndexOf(".") + 1);
			File file = new File(imagePath+File.separator +relativePath);
			
			if (file.exists()) {
				InputStream imageIn = new FileInputStream(file);
				if(type.toLowerCase().equals("swf")){
					writeSWF(imageIn,res,file);
				}else if (type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg")) {
					writeJPGImage(imageIn, res, file);
				} else if (type.toLowerCase().equals("png")) {
					writePNGImage(imageIn, res, file);
				} else if (type.toLowerCase().equals("gif")) {
					writeGIFImage(imageIn, res, file);
				} else {
					writePNGImage(defaultIn, res, file);
				}
			} else {
				writePNGImage(defaultIn, res, defaultImg);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private void writeSWF(InputStream imageIn, HttpServletResponse res, File file) {
//		res.addHeader("content-length",String.valueOf(file.length()));
		res.setContentType(SWF);
		getOutInfo(imageIn, res);
	}

	@Override
	public String yonkomaToWeibo(String type, String primaryId,
			String endingId, String userId, String content, String animId) {
		
				//根据type分两种方式：一种用系统定义的结局，则直接拼接图片;
				//另一种是自定义的结局，则根据动画id找到图片再拼接
				
				boolean flag = false;
				
				String appUrl = "http://apps.weibo.com/watuiup?id="+animId;
				
				User user = dbVisitor.getUserById(userId);
				String token = user.getAccessToken();
				
				//取主动画的长图片路径
				Yonkoma primary = dbVisitor.getYonkomaById(primaryId,"Primary");
				String primaryLong = primary.getLongImg();
				log.info("Primary image path : "+primaryLong);
				//结局动画的长图片路径
				String endingLong = "";
//				if("system".equals(type)){
					Yonkoma ending = dbVisitor.getYonkomaById(endingId,"Ending");
					endingLong = ending.getLongImg();
					log.info("System ending image path : "+endingLong);
					
//				}else if("custom".equals(type)){
//					//取出自定义结局的图片
//					Cartoon cartoon= dbVisitor.getAnimationById(endingId);
//					String thumbnailPath = cartoon.getThumbnail();
//					int position = thumbnailPath.lastIndexOf(".");
//					String extend = thumbnailPath.substring(position);
//					endingLong = thumbnailPath.substring(0,position)+"_Raw"+extend;	
//					log.info("Custom ending image path : "+endingLong);
//				}
				
				//拼图片，将主动画长图片和结局的图片拼接在一起，存在一个临时路径下，发微博成功后删除
				SpliceImage splice = new SpliceImage();
				String imgPath = splice.spliceImage(imagePath,primaryLong,endingLong);
				log.info("Splice image path is :"+imgPath);
				
				//发微博
				String weiboId = this.publishWeibo(token, imgPath,  content, appUrl);
				
//				//删除临时拼成的文件
//				File file = new File(imgPath);
//				boolean fileDel = false;
//				if(file.exists()){
//					fileDel=file.delete();
//				}
				
				//发送微博成功，
				if(!"".equals(weiboId)){
					//向数据库中插入一条数据
					Weibostat stat = this.generateWeibostat(weiboId,primaryId,endingId,type,userId,"sina");
					int rows = dbVisitor.createWeibostat(stat);
					if(rows > 0){
						flag = true;
					}
				}else{
					log.info("Weibo exception, return status is null");
				}
				
				return String.valueOf(flag);
	}

	private Weibostat generateWeibostat(String weiboId, String primaryId,
			String endingId, String type, String userId, String pf) {
		Weibostat stat = new Weibostat();
		stat.setId(weiboId);
		stat.setPrimary(primaryId);
		stat.setEnding(endingId);
		stat.setType(type);
		stat.setUser(userId);
		stat.setCreateTime(WatuiUtils.getFormatNowTime());
		stat.setPlatform(pf);
		return stat;
	}

	private String publishWeibo(String token, String imgPath, String content,
			String appUrl) {
		log.info("watui publish weibo parmas token :"+token);
		String weiboId = "";
		try{
			try{
				byte[] imgContent= readFileImage(imgPath);
				ImageItem pic=new ImageItem("pic",imgContent);
				
				String resultText =content+"  应用地址："+appUrl;
				
				String s=java.net.URLEncoder.encode(resultText,"utf-8");
				Timeline tl = new Timeline();
				tl.client.setToken(token);
				Status status=tl.UploadStatus(s, pic);
				
				//发送成功后返回微博id
				weiboId = status.getId();
				
				log.info("Successfully upload the status to ["
						+status.getText()+"].");
			}catch(Exception e1){
				e1.printStackTrace();
				log.info("WeiboException: invalid_access_token.");
			}
		}catch(Exception ioe){
			ioe.printStackTrace();
			log.info("Failed to read the system input.");
		}
		
		return weiboId;
	}

	
	private byte[] readFileImage(String filename) throws Exception {
		BufferedInputStream bufferedInputStream=new BufferedInputStream(
				new FileInputStream(filename));
		int len =bufferedInputStream.available();
		byte[] bytes=new byte[len];
		int r=bufferedInputStream.read(bytes);
		if(len !=r){
			bytes=null;
			throw new IOException("读取文件不正确");
		}
		bufferedInputStream.close();
		return bytes;
	}

	@Override
	public String operateWeiboUser(String userId, String accessToken) {
		boolean flag = false;
		
		weibo4j.model.User weiboUser = this.getUserByIdAndToken(userId, accessToken);
		String nickName = "佚名";
		if(weiboUser != null){
			nickName = weiboUser.getScreenName();
		}else{
			log.warn("weibo user is null");
		}
		//先判断用户是否存在
		int rows = dbVisitor.checkUserExist(userId);
		//存在即更新数据，不存在就插入新记录
		if(rows >0){
			log.info(userId+"----------"+accessToken);
			int udpRows = dbVisitor.updateUserById(userId,accessToken,nickName);
			if(udpRows > 0){
				flag = true;
			}
		}else{
			User user = this.generateUser(userId, accessToken, nickName,"sina");
			int crtRows = dbVisitor.createNewUser(user);
			if(crtRows > 0){
				flag = true;
			}
		}
		return String.valueOf(flag);
	}

	private User generateUser(String userId, String accessToken,String nickName,String platform) {
		User user = new User();
		user.setId(userId);
		user.setNickName(nickName);
		user.setCreateTime(WatuiUtils.getFormatNowTime());
		user.setAccessToken(accessToken);
		user.setPlatform(platform);
		user.setWealth(100);
		return user;
	}

	private weibo4j.model.User getUserByIdAndToken(String userId,
			String accessToken) {
		weibo4j.model.User wbUser = null;
		Users um = new Users();
		um.client.setToken(accessToken);
		try {
			wbUser = um.showUserById(userId);
		} catch (WeiboException e) {
			e.printStackTrace();
			log.info("catch WeiboException : "+ExceptionUtils.getStackTrace(e));
		}
		return wbUser;
	}

	@Override
	public UserDetail getUserInfo(String userId) {
		//（包括三步，首先从库里取token,wealth 再到去新浪取用户返回昵称和头像等 最后支付账户里去查到钱数累加）
		UserDetail userDetail = new UserDetail();
		User dbUser = new User();
		dbUser = dbVisitor.getUserById(userId);
		String accessToken = dbUser.getAccessToken();
		
		weibo4j.model.User weiboUser = this.getUserByIdAndToken(userId, accessToken);
		
		userDetail.setId(dbUser.getId());
		userDetail.setAccessToken(accessToken);
		if(weiboUser != null){
			userDetail.setAvatarLarge(weiboUser.getAvatarLarge());
			userDetail.setNickName(weiboUser.getScreenName());
			userDetail.setAvatarMini(weiboUser.getProfileImageUrl());
		}else{
			log.warn("weibo user is null");
		}
		
		userDetail.setWealth(dbUser.getWealth());

		return userDetail;
	}

	@Override
	public List<Cartoon> getAnimByPage(String pageNum, String pageSize) {
		List<Cartoon> list = dbVisitor.getAmimByPage(Integer.parseInt(pageNum),Integer.parseInt(pageSize));
		return list;
	}

	@Override
	public List<Cartoon> getAnimationsOf(String userId) {
		List<Cartoon> cartoonList = dbVisitor.getAnimationsOf(userId);
		return cartoonList;
	}

	@Override
	public int getYonkomaCount(String primary) {
		int rows = dbVisitor.getYonkomaCount(primary);
		return rows;
	}
	
	@Override
	public List<Yonkoma> getYonkomaByPage(String primary, String pageSize,
			String pageNum) {
		int size = Integer.parseInt(pageSize);
		int num = Integer.parseInt(pageNum);
		List<Yonkoma> list  = dbVisitor.getYonkomaByPage(primary,size,num);
		return list;
	}
	
	@Override
	public String createClipImage(FileItem shotData) {
		String fileName = shotData.getName();
		String filePath = imagePath + File.separator + fileName;
		File file = new File(filePath);
		try {
			shotData.write(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("upload video, save movieclip image path is "+filePath);
		return filePath;
	}

	@Override
	public String movieClipToWeibo(String userId, String clipId, String content, String type, String imgPath) {
		boolean flag = false;
		User user = dbVisitor.getUserById(userId);
		String token = user.getAccessToken();
		String appUrl = "http://apps.weibo.com/watuiup";
		String weiboId = this.publishWeibo(token, imgPath,  content, appUrl);
		//发送微博成功，
		if(!"".equals(weiboId)){
			//向数据库中插入一条数据
			Weibostat stat = this.generateWeibostat(weiboId,clipId,clipId,type,userId,"sina");
			int rows = dbVisitor.createWeibostat(stat);
			if(rows > 0){
				flag = true;
			}
		}else{
			log.info("Weibo exception, return status is null");
		}
		
		return String.valueOf(flag);
	}
	
	@Override
	public String operateTappUser(String openId, String nickName, String accessToken, String pf) {
		boolean flag = true;
		String userId = openId.substring(0,16);
		int count= dbVisitor.getUserByUserIdAndPlatform(userId,pf);
		if(count >0){
			int udpRows = dbVisitor.updateUserById(userId,accessToken,nickName);
			if(udpRows < 0){
				flag = false;
			}
		}else{
			User user = this.generateUser(userId, accessToken, nickName,pf);
			int crtRows = dbVisitor.createNewUser(user);
			if(crtRows < 0){
				flag = false;
			}
		}
		
		if(flag){
			return userId;
		}else{
			return String .valueOf(flag);
		}
	}

	@Override
	public String shareToTapp(String type, String primaryId, String endingId,
			String userId, String content, String openId, String openKey, String pf,String ip) {
		boolean flag = false;
//		User user = dbVisitor.getUserById(userId);
//		String token = user.getAccessToken();

		//取主动画的长图片路径
		Yonkoma primary = dbVisitor.getYonkomaById(primaryId,"Primary");
		String primaryLong = primary.getLongImg();
		log.info("Primary image path : "+primaryLong);
		//结局动画的长图片路径
		String endingLong = "";

		Yonkoma ending = dbVisitor.getYonkomaById(endingId,"Ending");
		endingLong = ending.getLongImg();
		log.info("System ending image path : "+endingLong);
		
		//拼图片，将主动画长图片和结局的图片拼接在一起，存在一个临时路径下
		SpliceImage splice = new SpliceImage();
		String imgPath = splice.spliceImage(imagePath,primaryLong,endingLong);
		log.info("Splice image path is :"+imgPath);
		
		if(new File(imgPath).exists()){
			weibo4j.org.json.JSONObject response = publishTencentWeibo(content,openId,openKey,ip,imgPath);
			//发微博成功
			try {
				if(response.getInt("ret") == 0){
					weibo4j.org.json.JSONObject weibo = response.getJSONObject("data");
					String weiboId = weibo.getString("id");
					Weibostat stat = this.generateWeibostat(weiboId,primaryId,endingId,type,userId,pf);
					int rows = dbVisitor.createWeibostat(stat);
					if(rows > 0){
						flag = true;
					}
				}else{
					log.info("Publish tencent weibo failed, error msg is "+response.getString("msg"));
				}
			} catch (weibo4j.org.json.JSONException e) {
				e.printStackTrace();
			}
		}
		
		return String.valueOf(flag);
	}
	
	private weibo4j.org.json.JSONObject publishTencentWeibo(String content,String openId, String openKey,String ip,String imgPath) {
		weibo4j.org.json.JSONObject response = new weibo4j.org.json.JSONObject();
		String url = "http://open.t.qq.com/api/t/add_pic_url";
		HttpClient client = new HttpClient();
		client.setToken("123");
		
		String appKey = systemConfigurer.getProperty("appKey");
		String appSecret = systemConfigurer.getProperty("appSecret");
		
		
		int position = imgPath.lastIndexOf("uploadFile");
		String relativePath = imgPath.substring(position+11);
		
		String picUrl = "http://diy.produ.cn/watui/watuiapi?method=getThumbnail&relativePath="+relativePath;
		System.out.println(">>>>>>>>>>>>>"+picUrl);
		
		String appContent = " 应用地址："+systemConfigurer.getProperty("appUrl");
	
		
		PostParameter appid = new PostParameter("appid",appKey);
		PostParameter openid = new PostParameter("openid",openId);
		PostParameter openkey = new PostParameter("openkey",openKey);
		PostParameter wbversion = new PostParameter("wbversion","1");
		PostParameter format = new PostParameter("format","json");
		PostParameter contentParam = new PostParameter("content",content+appContent);
		PostParameter clientip = new PostParameter("clientip",ip);
		PostParameter pic = new PostParameter("pic_url",picUrl);
		
		
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("appid",appKey);
		map.put("openid",openId);
		map.put("openkey",openKey);
		map.put("wbversion","1");
		map.put("reqtime",String.valueOf(new Date().getTime()));
		
		PostParameter sigParam = null;
		
		try {
			String sig = SnsSigCheck.makeSig("post","t/add_pic_url", map, appSecret);
			sigParam = new PostParameter("sig",sig);
		} catch (OpensnsException e) {
			e.printStackTrace();
		}
		
		PostParameter[] params = new PostParameter[]{appid,openid,openkey,wbversion,format,contentParam,clientip,pic,sigParam};
		
		try {
			response = client.post(url, params).asJSONObject();
			System.out.println("Response："+response.toString());

		} catch (WeiboException e) {
			e.printStackTrace();
		}
		
		return response;
	}

	@Override
	public String movieClipToTapp(String userId, String clipId, String content,
			String type, String filePath, String pf, String openId,
			String openKey, String ip) {
		boolean flag = false;
		weibo4j.org.json.JSONObject response = this.publishTencentWeibo(content, openId, openKey, ip, filePath);
		//发微博成功
		try {
			if(response.getInt("ret") == 0){
				weibo4j.org.json.JSONObject weibo = response.getJSONObject("data");
				String weiboId = weibo.getString("id");
				Weibostat stat = this.generateWeibostat(weiboId,clipId,clipId,type,userId,pf);
				int rows = dbVisitor.createWeibostat(stat);
				if(rows > 0){
					flag = true;
				}
			}else{
				log.info("Publish tencent weibo failed, error msg is "+response.getString("msg"));
			}
		} catch (weibo4j.org.json.JSONException e) {
			e.printStackTrace();
		}
		
		return String.valueOf(flag);
	}

	@Override
	public UserDetail getTappUser(String openId, String openKey, String pf) {
		UserDetail userDetail = new UserDetail();
		
		log.info("openId="+openId+"-----------openKey="+openKey);
		
		String appKey = systemConfigurer.getProperty("appKey");
		String appSecret = systemConfigurer.getProperty("appSecret");
		
		String serverName =  systemConfigurer.getProperty("serverName");
		
		OpenApiV3 sdk = new OpenApiV3(appKey, appSecret);
	    sdk.setServerName(serverName);
	    
	    String scriptName = "/v3/user/get_info";

        // 指定HTTP请求协议类型
        String protocol = "http";

        // 填充URL请求参数
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("openid", openId);
        params.put("openkey", openKey);
        params.put("pf", pf);
        
        String nickName = "";
        String avatarMini = "";
        String avatarLarge = "";
        try
        {
            String resp = sdk.api(scriptName, params, protocol);
            JSONObject json = JSONObject.fromObject(resp);
            if(json.getInt("ret") == 0){
            	nickName = json.getString("nickname");
            	String figureurl = json.getString("figureurl");
            	int position = figureurl.lastIndexOf("/")+1;
        		String relativePath = figureurl.substring(0,position);
            	avatarMini = relativePath+"50";
            	avatarLarge = relativePath + "100";
            }else{
            	log.info("/v3/user/get_info faild, error msg is "+json.getString("msg"));
            }
            System.out.println(resp);
            
        }catch (OpensnsException e){
            System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
            e.printStackTrace();
        }
		
		userDetail.setId("");
		userDetail.setAccessToken("");
		userDetail.setAvatarLarge(avatarLarge);
		userDetail.setNickName(nickName);
		userDetail.setAvatarMini(avatarMini);
		userDetail.setWealth(100);

		return userDetail;
	}

	@Override
	public String movieClipVideoToWeibo(String userId, String movieId,
			String content, String type, String url, String imgPath) {
		boolean flag = false;
		User user = dbVisitor.getUserById(userId);
		String token = user.getAccessToken();
		String appUrl = "http://apps.weibo.com/watuiup";
		String weiboId = this.publishVideoWeibo(token, url,  content, appUrl,imgPath);
		//发送微博成功，
		if(!"".equals(weiboId)){
			//向数据库中插入一条数据
			Weibostat stat = this.generateWeibostat(weiboId,movieId,movieId,type,userId,"sina");
			int rows = dbVisitor.createWeibostat(stat);
			if(rows > 0){
				flag = true;
			}
		}else{
			log.info("Weibo exception, return status is null");
		}
		
		return String.valueOf(flag);
	}

	private String publishVideoWeibo(String token, String url, String content,
			String appUrl, String imgPath) {
		log.info("watui publish weibo parmas token :"+token);
		String weiboId = "";
		try{
			try{
				String resultText =content+"  视频地址："+url+"    应用地址："+appUrl;
				byte[] imgContent= readFileImage(imgPath);
				ImageItem pic=new ImageItem("pic",imgContent);
				String s=java.net.URLEncoder.encode(resultText,"utf-8");
				Timeline tl = new Timeline();
				tl.client.setToken(token);
				Status status=tl.UploadStatus(s, pic);
				
				//发送成功后返回微博id
				weiboId = status.getId();
				
				log.info("Successfully upload the status to ["
						+status.getText()+"].");
			}catch(Exception e1){
				e1.printStackTrace();
				log.info("WeiboException: invalid_access_token.");
			}
		}catch(Exception ioe){
			ioe.printStackTrace();
			log.info("Failed to read the system input.");
		}
		
		return weiboId;
	}
	
	@Override
	public String movieClipToVideoToTapp(String userId, String movieId,
			String content, String type, String url, String openId,
			String openKey, String pf, String ip) {
		boolean flag = false;
		weibo4j.org.json.JSONObject response = this.publishVideoTencent(content, openId, openKey, ip, url);
		//发微博成功
		try {
			if(response.getInt("ret") == 0){
				weibo4j.org.json.JSONObject weibo = response.getJSONObject("data");
				String weiboId = weibo.getString("id");
				Weibostat stat = this.generateWeibostat(weiboId,movieId,movieId,type,userId,pf);
				int rows = dbVisitor.createWeibostat(stat);
				if(rows > 0){
					flag = true;
				}
			}else{
				log.info("Publish tencent weibo failed, error msg is "+response.getString("msg"));
			}
		} catch (weibo4j.org.json.JSONException e) {
			e.printStackTrace();
		}
		
		return String.valueOf(flag);
	}

	private weibo4j.org.json.JSONObject publishVideoTencent(String content,
			String openId, String openKey, String ip, String url) {
		weibo4j.org.json.JSONObject response = new weibo4j.org.json.JSONObject();
		String askUrl = "http://open.t.qq.com/api/t/add_video";
		HttpClient client = new HttpClient();
		client.setToken("123");
		
		String appKey = systemConfigurer.getProperty("appKey");
		String appSecret = systemConfigurer.getProperty("appSecret");
		
		String appContent = " 应用地址："+systemConfigurer.getProperty("appUrl");
		
		PostParameter appid = new PostParameter("appid",appKey);
		PostParameter openid = new PostParameter("openid",openId);
		PostParameter openkey = new PostParameter("openkey",openKey);
		PostParameter wbversion = new PostParameter("wbversion","1");
		PostParameter format = new PostParameter("format","json");
		PostParameter contentParam = new PostParameter("content",content+appContent);
		PostParameter clientip = new PostParameter("clientip",ip);
		PostParameter pic = new PostParameter("video_url",url);
		
		
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("appid",appKey);
		map.put("openid",openId);
		map.put("openkey",openKey);
		map.put("wbversion","1");
		map.put("reqtime",String.valueOf(new Date().getTime()));
		
		PostParameter sigParam = null;
		
		try {
			String sig = SnsSigCheck.makeSig("post","t/add_video", map, appSecret);
			sigParam = new PostParameter("sig",sig);
		} catch (OpensnsException e) {
			e.printStackTrace();
		}
		
		PostParameter[] params = new PostParameter[]{appid,openid,openkey,wbversion,format,contentParam,clientip,pic,sigParam};
		
		try {
			response = client.post(askUrl, params).asJSONObject();
			System.out.println("Response："+response.toString());

		} catch (WeiboException e) {
			e.printStackTrace();
		}
		
		return response;
	}
}
