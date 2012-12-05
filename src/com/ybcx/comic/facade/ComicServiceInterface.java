package com.ybcx.comic.facade;

import java.util.List;

import org.apache.commons.fileupload.FileItem;

import com.ybcx.comic.beans.Assets;

public interface ComicServiceInterface {
	
	// 设置图片文件保存路径，由ApiAdaptor赋值
	public void saveImagePathToProcessor(String filePath);

	public String createAdImg(FileItem sourceData);

	public List<Assets> getAllAssets();

	public String deleteAssetById(String assetId);

	public List<Assets> searchByLabel(String labels);

	public Assets getAssetById(String assetId);

	public String updateAssetById(String assetId, String name, String price);


}
