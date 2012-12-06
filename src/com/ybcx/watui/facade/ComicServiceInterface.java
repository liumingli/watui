package com.ybcx.watui.facade;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.ybcx.watui.beans.Cartoon;
import com.ybcx.watui.beans.Yonkoma;
import com.ybcx.watui.beans.UserDetail;


public interface ComicServiceInterface {
	
	// 设置图片文件保存路径，由ApiAdaptor赋值
	public void saveImagePathToProcessor(String filePath);

	public String createAdImg(FileItem sourceData);
	
	public String createAnimation(FileItem shotData, String userId,
			String name, String content);

	public void getThumbnailFile(String relativePath, HttpServletResponse res);

	public void getAssetFile(String relativePath, HttpServletResponse res);

	public String yonkomaToWeibo(String type, String primaryId,
			String endingId, String userId, String content);

	public String operateWeiboUser(String userId, String accessToken);

	public UserDetail getUserInfo(String userId);

	public List<Cartoon> getAmimByPage(String pageNum);

	public List<Cartoon> getAnimationsOf(String userId);

	public int getYonkomaCount(String primary);

	public List<Yonkoma> getYonkomaByPage(String primary, String pageSize,
			String pageNum);



}