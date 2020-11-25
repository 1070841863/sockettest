package com.mysocket.sockettest.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysocket.sockettest.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author study
 * @create 2020-11-22 14:27
 */
@Component
@ServerEndpoint("/websocket/{uname}")
public class WebSocket {

    private static final Logger logger=LoggerFactory.getLogger(WebSocket.class);

    private UserInfo userInfo;


    private static ConcurrentHashMap<String, UserInfo>  hashMap=new ConcurrentHashMap();


    /**
     * 开启一个连接
     * @param session
     */
    @OnOpen
    public void opOpen(@PathParam("uname") String uname,Session session) throws IOException {
        System.out.println(uname);
        //解码
        String decode = URLDecoder.decode(uname, "utf-8");
        decode=decode.replace("\"","").replace("\"","");
        UserInfo userInfos=new UserInfo(decode,session);
        logger.info(userInfos.getUname()+"已连接");
        this.userInfo=userInfos;
        //将数据存入hashMap中
        hashMap.put(decode,userInfos);
        JSONObject hashMapinfo=new JSONObject();
        hashMapinfo.put("type",1);
        hashMapinfo.put("uname",decode);
        System.out.println(hashMap);
        //登录之后，给所有人发送消息，我进入了。此时再前端获取到了之后就会在左部人员在线列表添加数据
        for(String key:hashMap.keySet()){
            //群发数据,但是不给自己发。因为自己登录的需要获取其他用户的数据。
            if(!hashMap.get(key).getUname().equals(userInfo.getUname())){
                hashMap.get(key).getSession().getBasicRemote().sendText(hashMapinfo.toString());
            }
        }

        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("type",-1);
        JSONArray jsonArray=new JSONArray();
        for(String key:hashMap.keySet()){
            jsonArray.add(hashMap.get(key).getUserInfo());
        }
        jsonObject1.put("onlineUsers",jsonArray);
        session.getBasicRemote().sendText(jsonObject1.toString());
    }

    /**
     * 关闭一个
     */
    @OnClose
    public void onClose() throws IOException {
        hashMap.remove(userInfo.getUname());
        logger.info(userInfo.getUname()+"断开连接");
        JSONObject hashMapinfo=new JSONObject();
        hashMapinfo.put("type",0);
        hashMapinfo.put("uname",userInfo.getUname());
        for(String key:hashMap.keySet()){
            //群发数据
            hashMap.get(key).getSession().getBasicRemote().sendText(hashMapinfo.toString());
        }
    }


    /**
     * 收到消息
     * @param msg
     */
    @OnMessage
    public void onMessage(String msg) throws IOException {
        logger.info(Instant.now() +"收到客户端发来的消息:"+msg);
        //收到群发消息
//        type为2就是当页面发送消息，就会进行给服务端发送消息，然后服务端群发消息
        JSONObject parse = JSONObject.parseObject(msg);
        System.out.println(msg);
        int flag= (int) parse.get("flag");
        if (flag==1){
            for (String key:hashMap.keySet())
            {
                hashMap.get(key).getSession().getBasicRemote().sendText(msg);
            }
        }else if (flag==2){
            for (String key:hashMap.keySet())
            {
                if (key.equals(parse.get("tousername"))){
                    hashMap.get(key).getSession().getBasicRemote().sendText(msg);
                }
            }
        }
    }


}
