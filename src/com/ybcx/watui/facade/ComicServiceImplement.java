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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import com.qq.open.ErrorCode;
import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;
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

	private String saveAnimationRaw(FileItem imgData) {
		String fileName = imgData.getName();
		int position = fileName.lastIndexOf(".");
		String extend = fileName.substring(position);
		String newName = fileName.substring(0,position)+"_Raw"+extend;
		String filePath = imagePath + File.separator + newName;
		File file = new File(filePath);
		try {
			imgData.write(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	

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
					Weibostat stat = this.generateWeibostat(weiboId,primaryId,endingId,type,userId);
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
			String endingId, String type, String userId) {
		Weibostat stat = new Weibostat();
		stat.setId(weiboId);
		stat.setPrimary(primaryId);
		stat.setEnding(endingId);
		stat.setType(type);
		stat.setUser(userId);
		stat.setCreateTime(WatuiUtils.getFormatNowTime());
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
				
				String resultText =content+"  动画地址："+appUrl;
				
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
			User user = this.generateUser(userId, accessToken, nickName,"Sina");
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
	public List<Cartoon> getAmimByPage(String pageNum) {
		int pageSize = Integer.parseInt(systemConfigurer.getProperty("pageSize"));
		List<Cartoon> list = dbVisitor.getAmimByPage(Integer.parseInt(pageNum),pageSize);
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
	public String getTencentUser(String openId, String openKey, String pf,String pfKey) {
		String result = "";
		//从远程取用户
		String resp = this.askTencentUser(openId, openKey, pf);
		
		  // 解码JSON
        JSONObject jo = null;
        try {
            jo = new JSONObject(resp);
         } catch (JSONException e) {
            try {
				throw new OpensnsException(ErrorCode.RESPONSE_DATA_INVALID, e);
			} catch (OpensnsException e1) {
				e1.printStackTrace();
			} 
        } 

        // 检测ret值，为零时为正确返回
        int ret = jo.optInt("ret", 0);
        String nickName = jo.optString("nickname");
        if(ret == 0){//返回userId
        	result = this.operateTencentUser(openId,nickName);
        	try {
				jo.put("userId", result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        
		return jo.toString();
	}

	private String operateTencentUser(String openId,String nickName) {
		String res = "";
		//先判断用户是否存在
		String userId = dbVisitor.getTendUserByOpenid(openId);
		//存在即更新数据，不存在就插入新记录
		if(userId != null && !"".equals(userId)){
			res = userId;
		}else{
			String newId = WatuiUtils.generateUID();
			User user = this.generateUser(newId, openId, nickName,"Tencent");
			int crtRows = dbVisitor.createNewUser(user);
			if(crtRows > 0){
				res = newId;
			}
		}
		return res;
	}

	private String askTencentUser(String openId, String openKey, String pf) {
		String result = "";
		String appId = "801281774";
		String appKey = "bb01c18c95cb8bb7e0a86ef32b616c4e";	
		String scriptName = "/v3/user/get_info";
		String protocol = "http";
		String serverName = "119.147.19.43";
		
		OpenApiV3 open = new OpenApiV3(appId, appKey);
		open.setServerName(serverName);
		
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("openid", openId);
		params.put("openkey", openKey);
		params.put("pf", pf);
		try {
			result = open.api(scriptName, params, protocol);
		} catch (OpensnsException e) {
			e.printStackTrace();
		}
		return result;
	}
	


}
