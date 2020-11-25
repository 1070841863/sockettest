package com.mysocket.sockettest.controller;

import com.mysocket.sockettest.service.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author study
 * @create 2020-11-22 14:18
 */
@Controller
public class MessageController {



    @Autowired
    private WebSocket webSocket;

    @ResponseBody
    @RequestMapping("/sendMessage")
    public String sendMessage(String id){
//        webSocket.sendMessage("你有新的订单，请注意查收",id);
        return "订单创建成功";
    }


    @RequestMapping("/")
    public String goPage(){
        System.out.println("index");
        return "/login";
    }

    @RequestMapping("/login")
    public String index(Model model, String uname){
        model.addAttribute("name",uname);
        return "/index";
    }
}
