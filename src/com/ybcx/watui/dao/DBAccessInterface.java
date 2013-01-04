package com.ybcx.watui.dao;


import java.util.List;

import com.ybcx.watui.beans.User;
import com.ybcx.watui.beans.Weibostat;
import com.ybcx.watui.beans.Yonkoma;
import com.ybcx.watui.beans.Cartoon;


public interface DBAccessInterface {

	public int saveAnimation(Cartoon cartoon);

	public User getUserById(String userId);

	public Yonkoma getYonkomaById(String primaryId, String string);

	public Cartoon getAnimationById(String endingId);

	public int createWeibostat(Weibostat stat);

	public int checkUserExist(String userId);

	public int updateUserById(String userId, String accessToken, String nickName);

	public int createNewUser(User user);

	public List<Cartoon> getAmimByPage(int pageNum, int pageSize);

	public List<Cartoon> getAnimationsOf(String userId);

	public int getYonkomaCount(String primary);

	public List<Yonkoma> getYonkomaByPage(String primary, int size, int num);

	public String getTendUserByOpenid(String openId);

	public int getUserByUserIdAndPlatform(String userId, String pf);



		
}
