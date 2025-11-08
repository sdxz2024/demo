package com.example.demo.controller;

import com.example.demo.entity.Card;
import com.example.demo.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import java.util.List;
public class BankCardController {
    @Resource
    private JdbcTemplate jdbcTemplate;;
    //1.跳转到管理银行卡页面
    @GetMapping("/card/add")
    public String toAddCard(HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null){
            return "redirect:/login";//未登录，跳转登录页
        }
        return "add";
    }
}
