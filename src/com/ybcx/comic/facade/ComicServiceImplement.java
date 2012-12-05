package com.ybcx.comic.facade;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;

import com.ybcx.comic.beans.Assets;
import com.ybcx.comic.dao.DBAccessInterface;

public class ComicServiceImplement implements ComicServiceInterface {


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
		
		String path = imagePath + File.separator + "thumbnail" + File.separator
				+ newfileName + type;
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
	public List<Assets> getAllAssets() {
		List<Assets> assetsList = dbVisitor.getAllAssets();
		return assetsList;
	}

	@Override
	public Assets getAssetById(String assetId) {
		Assets asset = dbVisitor.getAssetById(assetId);
		return asset;
	}
	
	@Override
	public String deleteAssetById(String assetId) {
		boolean flag = true;
		int rows = dbVisitor.deleteAssetById(assetId);
		if(rows < 1){
			flag = false;
		}
		return String.valueOf(flag);
	}
	
	@Override
	public String updateAssetById(String assetId, String name, String price) {
		boolean flag = true;
		int rows = dbVisitor.updateAssetById(assetId,name,price);
		if(rows < 1){
			flag = false;
		}
		return String.valueOf(flag);
	}

	@Override
	public List<Assets> searchByLabel(String labels) {
		// TODO Auto-generated method stub
		return null;
	}


}
