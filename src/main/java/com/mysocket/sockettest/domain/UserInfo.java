package com.mysocket.sockettest.domain;

import com.alibaba.fastjson.JSONObject;

import javax.websocket.Session;

/**
 * @author study
 * @create 2020-11-25 14:45
 */
public class UserInfo {

    private String uname;

    private Session session;

    public UserInfo(String uname, Session session) {
        this.uname = uname;
        this.session = session;
    }


    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    //获取用户信息
    public String getUserInfo(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("uname",uname);
        return jsonObject.toJSONString();
    }
}
