package com.ybcx.watui.tools;

import java.util.*;

import net.sf.json.JSONObject;

import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;

/**
 * OpenAPI V3 SDK 示例代码
 *
 * @version 3.0.0
 * @since jdk1.5
 * @author open.qq.com
 * @copyright © 2012, Tencent Corporation. All rights reserved.
 * @History:
 *               3.0.0 | nemozhang | 2012-03-21 12:01:05 | initialization
 *
 */
 
public class TestOpenApiV3
{
    public static void main(String args[])
    {
        // 应用基本信息
        String appid = "801281774";
        String appkey = "bb01c18c95cb8bb7e0a86ef32b616c4e";

        // 用户的OpenID/OpenKey
        String openid = "14219AC2BF3FCDC2D80914235C042566";
        String openkey = "4A0CF12B48FC94D2C09F9C73B8ED1667";

        // OpenAPI的服务器IP 
        // 最新的API服务器地址请参考wiki文档: http://wiki.open.qq.com/wiki/API3.0%E6%96%87%E6%A1%A3 
        String serverName = "113.108.20.23";

        // 所要访问的平台, pf的其他取值参考wiki文档: http://wiki.open.qq.com/wiki/API3.0%E6%96%87%E6%A1%A3
        String pf = "tapp";

        OpenApiV3 sdk = new OpenApiV3(appid, appkey);
        sdk.setServerName(serverName);

        System.out.println("===========test GetUserInfo===========");
        testGetUserInfo(sdk, openid, openkey, pf);
    }

    /**
     * 测试调用UserInfo接口
     *
     */
    public static void testGetUserInfo(OpenApiV3 sdk, String openid, String openkey, String pf)
    {
        // 指定OpenApi Cgi名字 
        String scriptName = "/v3/user/get_info";

        // 指定HTTP请求协议类型
        String protocol = "http";

        // 填充URL请求参数
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("openid", openid);
        params.put("openkey", openkey);
        params.put("pf", pf);
        
        try
        {
            String resp = sdk.api(scriptName, params, protocol);
            JSONObject json = JSONObject.fromObject(resp);
            if(json.getInt("ret") == 0){
            	String nickName = json.getString("nickname");
            	String figureurl = json.getString("figureurl");
            	int position = figureurl.lastIndexOf("/")+1;
        		String relativePath = figureurl.substring(0,position);
            	String avatarMini = relativePath+"50";
            	System.out.println(nickName+avatarMini);
            }else{
            	System.out.println("/v3/user/get_info faild, error msg is "+json.getString("msg"));
            }

//        try
//        {
//            String resp = sdk.api(scriptName, params, protocol);
//            System.out.println(resp);
//        }
        } catch (OpensnsException e)
        {
            System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
            e.printStackTrace();
        }
    }
}
