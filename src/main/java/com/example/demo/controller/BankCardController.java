package com.example.demo.controller;

import com.example.demo.entity.Card;
import com.example.demo.entity.User;
import com.example.demo.util.PasswordUtil;

import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import java.util.List;
@Controller
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
        return "addCard";
    }
    //2.处理添加银行卡请求
    @PostMapping("/card/doAdd")
    public String doAddCard(Card card, @RequestParam String confirmPassword,HttpSession session, Model model){
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null){
            return "redirect:/login";//未登录，跳转登录页
        }
        if(!card.getPassword().equals(confirmPassword)){
            model.addAttribute("msg", "两次输入的密码不一致!");
            return "addCard";
        }
        try{
            String encryptCardPwd = PasswordUtil.encrypt(card.getPassword());
            card.setPassword(encryptCardPwd);
            jdbcTemplate.update(
                    "INSERT INTO cards (user_id, card_id, password, money, state) VALUES (?, ?, ?, ?, ?)",
                    loginUser.getId(), card.getCard_id(), card.getPassword(), card.getMoney(), card.getState()
            );
            return "redirect:/user/home";//添加成功，跳转用户主页(显示银行卡列表)
        }catch(Exception e){
            model.addAttribute("error", "添加银行卡失败");
            return "addCard";
        }
    }
    //3.跳转到修改银行卡页面
    @GetMapping("/card/edit")
    public String toEditCard(@RequestParam String cardId,HttpSession session, Model model){ 
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null){
            return "redirect:/login";//未登录，跳转登录页
        }
        try{
            Card card = jdbcTemplate.queryForObject(
                    "SELECT card_id, password, state FROM cards WHERE card_id = ? AND user_id = ?",
                    new Object[]{cardId, loginUser.getId()},
                    (rs, rowNum) -> {
                        Card c = new Card();
                        c.setCard_id(rs.getString("card_id"));
                        c.setState(rs.getInt("state"));
                        return c;
                    }
            );
            model.addAttribute("card", card);
            return "editCard";
        }catch(Exception e){
            return "redirect:/user/home";//查询失败，跳转用户主页
        }
    }
    //4.处理修改银行卡请求
    @PostMapping("/card/doEdit")
    public String doEditCard(Card card,HttpSession session, Model model){ 
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null){
            return "redirect:/login";//未登录，跳转登录页
        }
        
        try{
            //1.验证银行卡密码
            String dbPassword = jdbcTemplate.queryForObject(
                    "SELECT password FROM cards WHERE card_id = ? AND user_id = ?",
                    new Object[]{card.getCard_id(), loginUser.getId()},
                    String.class
            );
            if(!PasswordUtil.verify(card.getPassword(), dbPassword)){
                model.addAttribute("msg", "银行卡密码错误");
                model.addAttribute("card", card);
                return "editCard";
            }
            //2.验证输入是否合法
            List<Integer> validStates = List.of(-1, 0, 1, 2);
            if(!validStates.contains(card.getState())){
                model.addAttribute("msg", "无效的银行卡状态");
                model.addAttribute("card", card);
                return "editCard";
            }
            //3.更新银行卡状态
            jdbcTemplate.update(
                    "UPDATE cards SET state = ? WHERE card_id = ? AND user_id = ?",
                    card.getState(), card.getCard_id(), loginUser.getId()
            );
            return "redirect:/user/home";//修改成功，跳转用户主页
        } catch(Exception e){ 
            model.addAttribute("msg", "修改银行卡失败");
            model.addAttribute("card", card);
            return "editCard";
        }
    }
}
