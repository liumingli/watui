package com.ybcx.watui.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.ybcx.watui.beans.User;
import com.ybcx.watui.beans.Cartoon;
import com.ybcx.watui.beans.Weibostat;
import com.ybcx.watui.beans.Yonkoma;
import com.ybcx.watui.dao.DBAccessInterface;



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
	public int saveAnimation(final Cartoon cartoon) {
		String sql = "INSERT INTO t_cartoon "
				+ "(c_id, c_name, c_thumbnail, c_content,c_owner, c_createTime, c_enable, c_app, c_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
		
		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, cartoon.getId());
					ps.setString(2, cartoon.getName());
					ps.setString(3, cartoon.getThumbnail());
					ps.setString(4, cartoon.getContent());
					ps.setString(5, cartoon.getOwner());
					ps.setString(6, cartoon.getCreateTime());
					ps.setInt(7, cartoon.getEnable());
					ps.setString(8, "watui");
					ps.setString(9,"");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}

	@Override
	public User getUserById(String userId) {
		User user = new User();
		String sql = "select * from t_watuiuser where wa_id='"+userId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				user.setId(map.get("wa_id").toString());
				user.setAccessToken(map.get("wa_accessToken").toString());
				user.setCreateTime(map.get("wa_createTime").toString());
				user.setWealth(Integer.parseInt(map.get("wa_wealth").toString()));
			}
		}
		return user;
	}

	@Override
	public Yonkoma getYonkomaById(String id, String type) {
		Yonkoma yonkoma = new Yonkoma();
		String sql = "select * from t_yonkoma where y_id='"+id+"' and y_type='"+type+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				yonkoma.setId(map.get("y_id").toString());
				yonkoma.setName(map.get("y_name").toString());
				yonkoma.setSwf(map.get("y_swf").toString());
				yonkoma.setThumbnail(map.get("y_thumbnail").toString());
				yonkoma.setLongImg(map.get("y_longImg").toString());
				yonkoma.setCreateTime(map.get("y_createTime").toString());
				yonkoma.setParent(map.get("y_parent").toString());
				yonkoma.setFrame(Integer.parseInt(map.get("y_frame").toString()));
				yonkoma.setType(map.get("y_type").toString());
				yonkoma.setEnable(Integer.parseInt(map.get("y_enable").toString()));
				yonkoma.setAuthor(map.get("y_author").toString());
				yonkoma.setAd(Integer.parseInt(map.get("y_isad").toString()));
			}
		}
		return yonkoma;
	}

	@Override
	public Cartoon getAnimationById(String animId) {
		Cartoon cartoon = new Cartoon();
		String sql = "select * from t_cartoon where c_id='"+animId+"' and c_enable=1";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				cartoon.setId(map.get("c_id").toString());
				cartoon.setName(map.get("c_name").toString());
				cartoon.setOwner(map.get("c_owner").toString());
				cartoon.setContent(map.get("c_content").toString());
				cartoon.setCreateTime(map.get("c_createTime").toString());
				cartoon.setThumbnail(map.get("c_thumbnail").toString());
				cartoon.setEnable(Integer.parseInt(map.get("c_enable").toString()));
			}
		}
		return cartoon;
	}

	@Override
	public int createWeibostat(final Weibostat stat) {
		String sql = "INSERT INTO t_weibostat "
				+ "(w_id,w_primary,w_ending,w_type,w_user,w_createTime,w_memo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, stat.getId());
					ps.setString(2, stat.getPrimary());
					ps.setString(3, stat.getEnding());
					ps.setString(4, stat.getType());
					ps.setString(5, stat.getUser());
					ps.setString(6, stat.getCreateTime());
					ps.setString(7, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		return res;
	}

	@Override
	public int checkUserExist(String userId) {
		String sql = "select count(wa_id) from t_watuiuser where wa_id='"+userId+"'";
		int rows = jdbcTemplate.queryForInt(sql);
		return rows;
	}

	@Override
	public int updateUserById(String userId, String accessToken,String nickName) {
		String sql = "update t_watuiuser set wa_accessToken='"+accessToken+"',wa_nickName='"+nickName+"' " +
				"where wa_id='"+userId+"'";
		int rows = jdbcTemplate.update(sql);
		return rows;
	}

	@Override
	public int createNewUser(final User user) {
		String sql = "insert into t_watuiuser(wa_id,wa_nickName,wa_accessToken,wa_createTime,wa_wealth,wa_platform,wa_memo) values (?,?,?,?,?,?,?)";
		int res =jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) {
				try {
					ps.setString(1, user.getId());
					ps.setString(2, user.getNickName());
					ps.setString(3, user.getAccessToken());
					ps.setString(4, user.getCreateTime());
					ps.setInt(5, user.getWealth());
					ps.setString(6, user.getPlatform());
					ps.setString(7, "");

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		return res;
	}

	@Override
	public List<Cartoon> getAmimByPage(int pageNum, int pageSize) {
		List<Cartoon> list = new ArrayList<Cartoon>();
		int startLine = (pageNum -1)*pageSize;
		String sql = "select c.c_id,c.c_name,c.c_owner,c.c_content,c.c_createTime,c.c_thumbnail,c.c_enable,u.wa_nickName" +
				" from t_cartoon c, t_watuiuser u where c.c_enable=1 and c.c_owner = u.wa_id and c.c_app='watui'" +
				"order by c.c_createTime desc limit "+startLine+","+pageSize;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Cartoon cartoon = new Cartoon();
				cartoon.setId(map.get("c_id").toString());
				cartoon.setName(map.get("c_name").toString());
				cartoon.setOwner(map.get("c_owner").toString());
				cartoon.setAuthor(map.get("wa_nickName").toString());
				cartoon.setContent(map.get("c_content").toString());
				cartoon.setCreateTime(map.get("c_createTime").toString());
				cartoon.setThumbnail(map.get("c_thumbnail").toString());
				cartoon.setEnable(Integer.parseInt(map.get("c_enable").toString()));
				list.add(cartoon);
			}
		}
		return list;
	}

	@Override
	public List<Cartoon> getAnimationsOf(String userId) {
		List<Cartoon> list = new ArrayList<Cartoon>();
		String sql = "select * from t_cartoon where c_owner='"+userId+"' and c_app='watui' and c_enable=1 order by c_createTime desc";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Cartoon cartoon = new Cartoon();
				cartoon.setId(map.get("c_id").toString());
				cartoon.setName(map.get("c_name").toString());
				cartoon.setOwner(map.get("c_owner").toString());
				cartoon.setContent(map.get("c_content").toString());
				cartoon.setCreateTime(map.get("c_createTime").toString());
				cartoon.setThumbnail(map.get("c_thumbnail").toString());
				cartoon.setEnable(Integer.parseInt(map.get("c_enable").toString()));
				list.add(cartoon);
			}
		}
		return list;
	}

	@Override
	public int getYonkomaCount(String primary) {
		String sql = "select count(y_id) from t_yonkoma where y_parent='"+primary+"' and y_enable=1";
		int result = jdbcTemplate.queryForInt(sql);
		return result;
	}

	@Override
	public List<Yonkoma> getYonkomaByPage(String primary, int pageSize, int pageNum) {
		List<Yonkoma> list = new ArrayList<Yonkoma>();
		int startLine = (pageNum -1)*pageSize;
		String sql = "select * from t_yonkoma where y_parent='"+primary+"' and y_enable=1" +
				" order by y_createTime desc limit "+startLine+","+pageSize;
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rows.get(i);
				Yonkoma yonkoma = new Yonkoma();
				yonkoma.setId(map.get("y_id").toString());
				yonkoma.setName(map.get("y_name").toString());
				yonkoma.setSwf(map.get("y_swf").toString());
				yonkoma.setThumbnail(map.get("y_thumbnail").toString());
				yonkoma.setLongImg(map.get("y_longImg").toString());
				yonkoma.setCreateTime(map.get("y_createTime").toString());
				yonkoma.setParent(map.get("y_parent").toString());
				yonkoma.setFrame(Integer.parseInt(map.get("y_frame").toString()));
				yonkoma.setType(map.get("y_type").toString());
				yonkoma.setEnable(Integer.parseInt(map.get("y_enable").toString()));
				yonkoma.setAuthor(map.get("y_author").toString());
				yonkoma.setAd(Integer.parseInt(map.get("y_isad").toString()));
				list.add(yonkoma);
			}
		}
		return list;
	}

	@Override
	public String getTendUserByOpenid(String openId) {
		String userId = "";
		String sql = "select * from t_watuiuser where wa_accessToken='"+openId+"'";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null && rows.size() > 0) {
			Map<String, Object> map = (Map<String, Object>) rows.get(0);
			userId = map.get("wa_id").toString();
		}
		return userId;
	}

	@Override
	public int getUserByUserIdAndPlatform(String userId, String pf) {
		String sql = "select count(*) from t_watuiuser where wa_id='"+userId+"' and wa_platform='"+pf+"'";
		int rows = jdbcTemplate.queryForInt(sql);
		return rows;
	}
	
	
}
