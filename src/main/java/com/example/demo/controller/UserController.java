package com.example.demo.controller;
import com.example.demo.entity.User;
import com.example.demo.entity.Card;
import com.example.demo.util.PasswordUtil;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
@Controller
public class UserController {

    @Resource
    private JdbcTemplate jdbcTemplate;
    //1.跳转到登录页
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    //2.处理登录请求
    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("id") String id, @RequestParam("password") String password, HttpSession session, Model model){ 
        try{
            User user = jdbcTemplate.queryForObject(
                    "SELECT id, name, password, sex FROM user WHERE id = ?",
                    new Object[]{id},
                    (rs, rowNum) -> {
                        User u = new User();
                        u.setId(rs.getString("id"));
                        u.setName(rs.getString("name"));
                        u.setPassword(rs.getString("password"));
                        u.setSex(rs.getInt("sex"));
                        return u;
                    }
            );
            if(PasswordUtil.verify(password, user.getPassword())){
                session.setAttribute("loginUser", user);
                return "redirect:/user/home";
            }else{
                model.addAttribute("error", "密码错误");
                return "login";
            }
        }catch(Exception e){
            model.addAttribute("error", "用户不存在");
            return "login";
        }
    }

//3.跳转到注册页
@GetMapping("/register")
public String toRegister(){
    return "register";
}
//4.处理注册请求
@PostMapping("/doRegister")
public String doRegister(User user,Model model){ 

    try{
        //检查用户名是否存在
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user WHERE id = ?",
                new Object[]{user.getId()},
                Integer.class
        );
        if(count != null && count > 0){
            model.addAttribute("error", "用户已存在");
            return "register";
        }
        //加密后存入数据库
        String encrypPwd = PasswordUtil.encrypt(user.getPassword());
        //插入用户数据
        jdbcTemplate.update(
                "INSERT INTO user (id, name, password, sex) VALUES (?, ?, ?, ?)",
                user.getId(), user.getName(), encrypPwd, user.getSex()
        );
        return "redirect:/login";  //注册成功，跳转登录页
    }catch(Exception e){
        model.addAttribute("msg", "注册失败");
        return "register";
    }
}
//5.跳转至用户主页(需登录)
@GetMapping("/user/home")
public String home(HttpSession session, Model model){ 
    User loginUser = (User)session.getAttribute("loginUser");
    if(loginUser == null){
        return "redirect:/login";//未登录，跳转登录页
    }
    //查当前用户的所有银行卡
    List<Card> bankCards = jdbcTemplate.query(
            "SELECT * FROM cards WHERE user_id = ?",
            new Object[]{loginUser.getId()},
            (rs, rowNum) -> {
                Card card = new Card();
                card.setCard_id(rs.getString("card_id"));
                card.setUser_id(rs.getString("user_id"));
                card.setMoney(rs.getDouble("money"));
                card.setState(rs.getInt("state"));
                card.setPassword(rs.getString("password"));
                return card;
            }
    );
    model.addAttribute("cardList", bankCards);//把银行卡列表传给前端
    model.addAttribute("user", loginUser);//把用户信息传给前端
    return "home";//跳转用户主页
}
//6.退出登录
@GetMapping("/logout")
public String logout(HttpSession session){
    session.invalidate();
    return "redirect:/login";
}
}