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
            return map;
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
            return map;
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
        String[] photos = new String[]{
                "https://p.qqan.com/up/2021-7/16255338463527509.png",
                "https://pic3.zhimg.com/50/v2-489247e55238dd4209d40d601da9d271_hd.jpg?source=1940ef5c",
                "https://tse3-mm.cn.bing.net/th/id/OIP-C.gTUjdrlN73h-cIMZ010I3AAAAA?rs=1&pid=ImgDetMain",
                "https://imgo.youxiniao.com/img2021/2/3/20/2021232045463286.jpg",
                "https://tse4-mm.cn.bing.net/th/id/OIP-C.csLASrVzsSjXJnAUx97hvwAAAA?rs=1&pid=ImgDetMain",
        };
        String photo = photos[(int)(Math.random()*photos.length)];

        User newuser = new User(null,username,encodedPassword,photo,1500,0);
        userMapper.insert(newuser);
        map.put("error_massage","success");
        return map;
    }
}
