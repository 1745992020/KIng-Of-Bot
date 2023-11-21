package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String,String>map =new HashMap<>();

        QueryWrapper<User> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user =userMapper.selectOne(queryWrapper);
        if(user!=null){
            map.put("error_massage","用户名不能重复");
            return  map;
        }
        if(username==null){
            map.put("error_massage","用户名不能为空");
            return map;
        }
        if(password==null||confirmedPassword==null){
            map.put("error_massage","密码不能为空");
            return map;
        }
        username=username.trim();
        if(username.isEmpty()){
            map.put("error_massage","用户名不能为空");
            return  map;
        }
        if(password.isEmpty()||confirmedPassword.isEmpty()){
            map.put("error_massage","密码不能为空");
            return map;
        }
        if(username.length()>100||password.length()>100||confirmedPassword.length()>100){
            map.put("error_massage","用户名或密码长度不能大于100个字符");
            return map;
        }
        if(!password.equals(confirmedPassword)){
            map.put("error_massage","两次输入密码不一样");
            return map;
        }
        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://p.qqan.com/up/2021-7/16255338463527509.png";
        User newuser = new User(null,username,encodedPassword,photo,1500);
        userMapper.insert(newuser);
        map.put("error_massage","success");
        return  map;
    }
}
