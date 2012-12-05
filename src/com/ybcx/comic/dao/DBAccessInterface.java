package com.ybcx.comic.dao;

import java.util.List;

import com.ybcx.comic.beans.Assets;




public interface DBAccessInterface {

		public List<Assets> getAllAssets();

		public int deleteAssetById(String assetId);

		public Assets getAssetById(String assetId);

		public int updateAssetById(String assetId, String name, String price);

		
}
