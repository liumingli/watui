package com.ybcx.comic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ybcx.comic.beans.Assets;
import com.ybcx.comic.dao.DBAccessInterface;



public class DBAccessImplement  implements DBAccessInterface {

	private JdbcTemplate jdbcTemplate;

	// Inject by Spring
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	// Constructor
	public DBAccessImplement() {

	}

	@Override
	public List<Assets> getAllAssets() {
		List<Assets> resList = new ArrayList<Assets>();
		String sql = "select * from t_assets where a_enable = 1 order by a_uploadTime desc";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Assets asset = new Assets();
				asset.setId(map.get("a_id").toString());
//				asset.setCategory(map.get("a_category").toString());
//				asset.setLabel(map.get("a_label").toString());
				asset.setHoliday(map.get("a_holiday").toString());
				asset.setName(map.get("a_name").toString());
				asset.setType(map.get("a_type").toString());
				asset.setThumbnail(map.get("a_thumbnail").toString());
				asset.setPath(map.get("a_path").toString());
				asset.setUploadTime(map.get("a_uploadTime").toString());
				asset.setPrice(Float.parseFloat(map.get("a_price").toString()));
				asset.setHeat(Integer.parseInt(map.get("a_heat").toString()));
				asset.setEnable(Integer.parseInt(map.get("a_enable").toString()));
				resList.add(asset);
			}
		}
		return resList;
	}

	@Override
	public int deleteAssetById(String assetId) {
		String sql = "update t_assets set a_enable = 0 where a_id ='"+assetId+"'";
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public Assets getAssetById(String assetId) {
		Assets asset = new Assets();
		String sql = "select * from t_assets where a_id='"+assetId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				asset.setId(map.get("a_id").toString());
//				asset.setCategory(map.get("a_category").toString());
//				asset.setLabel(map.get("a_label").toString());
				asset.setHoliday(map.get("a_holiday").toString());
				asset.setName(map.get("a_name").toString());
				asset.setType(map.get("a_type").toString());
				asset.setThumbnail(map.get("a_thumbnail").toString());
				asset.setPath(map.get("a_path").toString());
				asset.setUploadTime(map.get("a_uploadTime").toString());
				asset.setPrice(Float.parseFloat(map.get("a_price").toString()));
				asset.setHeat(Integer.parseInt(map.get("a_heat").toString()));
				asset.setEnable(Integer.parseInt(map.get("a_enable").toString()));
			}
		}
		return asset;
	}

	@Override
	public int updateAssetById(String assetId, String name, String price) {
		Float priceVal = Float.parseFloat(price);
		String sql = "update t_assets set a_name='"+name+"', a_price="+priceVal+" where a_id='"+assetId+"'";
		int rows = jdbcTemplate.update(sql);
		return rows;
	}
	
	
}
