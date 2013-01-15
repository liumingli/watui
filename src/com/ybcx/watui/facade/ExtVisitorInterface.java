/**
 * 
 */
package com.ybcx.watui.facade;

/**
 * 这里定义外部访问接口需要的常量字符串
 *
 */
public interface ExtVisitorInterface {
	
    //操作微博连接上的用户
	public static final String OPERATEWEIBOUSER = "operateWeiboUser";
	
	//根据id获取用户信息
	public static final String GETUSERINFO= "getUserInfo";
	
    //发四格漫画到微博，主要是文字及长图片
    public static final String YONKOMATOWEIBO = "yonkomaToWeibo";
    
    
    //获取所有主动画数量
    public static final String GETPRIMARYCOUNT = "getPrimaryCount";
    
    //分页获取主动画
    public static final String GETPRIMARYBYPAGE = "getPrimaryByPage";
    
    
    //获取某一主动画的所有结局数目
    public static final String GETENDINGCOUNTBYPRIMARY = "getEndingCountByPrimary";
    
    //分页获取结局
    public static final String GETENDINGBYPRIMARYANDPAGE = "getEndingByPrimaryAndPage";
    
	
	//某用户的所有diy动画
	public static final String GETANIMATIONSOF = "getAnimationsOf";
	
	//分页取动画
	public static final String GETANIMBYPAGE = "getAnimByPage";
	
	//分页取动画加一个参数callback(给网站用的)
	public static final String GETWEBANIM = "getWebAnim";
	
	//发送动画短篇到新浪微博
	public static final String MOVIECLIPTOWEIBO = "movieClipToWeibo";
	
	
	//-----------------以下三个没有用
	
	//动画保存
	public static final String SAVEANIM = "saveAnim";
	
	//获取素材
	public static final String GETASSETFILE ="getAssetFile";
	
	//获取素材缩略图
	public static final String GETTHUMBNAIL ="getThumbnail";
	
	
	
	//操作微博用户，更新或新建
	public static final String OPERATETAPPUSER = "operateTappUser";
	
	//腾讯微博
	public static final String SHARETOTAPP = "shareToTapp";
	
    //发送视频短片到腾讯微博
	public static final String MOVIECLIPTOTAPP = "movieClipToTapp";
	
	//获取微博用户头像
	public static final String GETTAPPUSER = "getTappUser";
    
}
