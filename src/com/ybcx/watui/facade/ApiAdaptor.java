/**
 * 
 */
package com.ybcx.watui.facade;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.fileupload.FileItem;

import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;
import com.ybcx.watui.beans.Cartoon;
import com.ybcx.watui.beans.UserDetail;
import com.ybcx.watui.beans.Yonkoma;




/**
 * Servlet调用服务的参数转换器，用来封装客户端参数并实现服务调用；
 * 
 * @author lwz
 * 
 */

public class ApiAdaptor {
	
	// 由Spring注入
	private ComicServiceInterface comicService;

	public void setComicService(ComicServiceInterface comicService) {
		this.comicService = comicService;
	}
	public ApiAdaptor() {

	}

	// 由AppStarter调用
	public void setImagePath(String filePath) {
		this.comicService.saveImagePathToProcessor(filePath);
	}

	public String createThumbnail(List<FileItem> fileItems) {
		FileItem sourceData = null;
		for (int i = 0; i < fileItems.size(); i++) {
			FileItem item = fileItems.get(i);
			if (!item.isFormField()) {
				//图片数据
				sourceData = item;
			}
		}
		String imgPath = comicService.createAdImg(sourceData);
		return imgPath;
	}
	
	public String createAnimation(List<FileItem> fileItems) {
		FileItem shotData = null;
		String userId = "";
		String name = "";
		String content = "";
		
		for (int i = 0; i < fileItems.size(); i++) {
			FileItem item = fileItems.get(i);
			if (!item.isFormField()) {
				//图片数据
				shotData = item;
			}
			
			if (item.isFormField()) {
				
				if (item.getFieldName().equals("userId")) {
					userId = item.getString();
				}
				
				if (item.getFieldName().equals("name")) {
					try {
						name = item.getString("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
				if (item.getFieldName().equals("content")) {
					try {
						content = item.getString("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
			}
		}//取参数完成
	
		String result = comicService.createAnimation(shotData,userId,name,content);
		
		return result;
		
	}
	
	public void getThumbnailFile(String relativePath, HttpServletResponse res) {
		comicService.getThumbnailFile(relativePath,res);
	}
	
	public void getAssetFile(String relativePath, HttpServletResponse res) {
		comicService.getAssetFile(relativePath,res);
	}
	
	public String yonkomaToWeibo(String type, String primaryId,
			String endingId, String userId, String content) {
		String res = comicService.yonkomaToWeibo(type,primaryId,endingId,userId,content);
		return res;
	}
	
	public String operateWeiboUser(String userId, String accessToken) {
		String result = comicService.operateWeiboUser(userId,accessToken);
		return result;
	}
	
	public String getUserInfo(String userId) {
		UserDetail detail= comicService.getUserInfo(userId);
		return JSONArray.fromObject(detail).toString();
	}
	
	public String getAnimByPage(String pageNum) {
		List<Cartoon> list = comicService.getAmimByPage(pageNum);
		JSONArray jsonArray = JSONArray.fromCollection(list);
		processCartoon(jsonArray);
		return jsonArray.toString();
	}
	
	private void processCartoon(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			//DIY成品的缩略图
			String thumbnailPath = jsonArray.getJSONObject(i).get("thumbnail").toString();
			if(!"".equals(thumbnailPath)){
				//先从字符串中找到文件夹uploadFile的位置，再加上uploadFile的长度10，即可截取到下属文件路径
				int position = thumbnailPath.lastIndexOf("uploadFile");
				String relativePath = thumbnailPath.substring(position+11);
				jsonArray.getJSONObject(i).set("thumbnail", relativePath);
			}
		}
	}
	
	public String getAnimationsOf(String userId) {
		List<Cartoon> list = comicService.getAnimationsOf(userId);
		JSONArray jsonArray = JSONArray.fromCollection(list);
		processCartoon(jsonArray);
		return jsonArray.toString();
	}
	
	public String getYonkomaByPage(String primary, String pageSize, String pageNum) {
		List<Yonkoma> list = comicService.getYonkomaByPage(primary,pageSize,pageNum);
		JSONArray jsonArray = JSONArray.fromCollection(list);
		processYonkoma(jsonArray);
		return jsonArray.toString();
	}
	
	public int getYonkomaCount(String primary) {
		int res = comicService.getYonkomaCount(primary);
		return res;
	}
	
	private void processYonkoma(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			//缩略图
			String thumbnailPath = jsonArray.getJSONObject(i).get("thumbnail").toString();
			if(!"".equals(thumbnailPath)){
				//先从字符串中找到文件夹uploadFile的位置，再加上uploadFile的长度10，即可截取到下属文件路径
				int position = thumbnailPath.lastIndexOf("uploadFile");
				String relativePath = thumbnailPath.substring(position+11);
				jsonArray.getJSONObject(i).set("thumbnail", relativePath);
			}
			
			//DIY成品的缩略图
			String swfPath = jsonArray.getJSONObject(i).get("swf").toString();
			if(!"".equals(swfPath)){
				//先从字符串中找到文件夹uploadFile的位置，再加上uploadFile的长度10，即可截取到下属文件路径
				int position = swfPath.lastIndexOf("uploadFile");
				String relativePath = swfPath.substring(position+11);
				jsonArray.getJSONObject(i).set("swf", relativePath);
			}
			
			//DIY成品的缩略图
			String longImg = jsonArray.getJSONObject(i).get("longImg").toString();
			if(!"".equals(longImg)){
				//先从字符串中找到文件夹uploadFile的位置，再加上uploadFile的长度10，即可截取到下属文件路径
				int position = longImg.lastIndexOf("uploadFile");
				String relativePath = longImg.substring(position+11);
				jsonArray.getJSONObject(i).set("longImg", relativePath);
			}
		}
	}
	
	//FIXME 获取腾讯用户
	public String getTendentUser(String openId, String openKey, String pf,
			String pfKey) {
		String result = comicService.getTendentUser(openId,openKey,pf,pfKey);
		return result;
	}
	

} // end of class
