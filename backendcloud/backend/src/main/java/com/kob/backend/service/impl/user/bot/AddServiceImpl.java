package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    BotMapper botMapper;
    @Override
    public Map<String, String> Add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl)  authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        String name = data.get("name");
        String description = data.get("description");
        String content = data.get("content");
        Map<String,String> map = new HashMap<>();

        QueryWrapper<Bot> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        if(botMapper.selectCount(queryWrapper) >= 10){
            map.put("error_massage","每个用户最多创建10个Bot");
            return map;
        }
        if(name.isEmpty()){
            map.put("error_massage","Bot名字不能为空");
            return map;
        }
        if(name.length()>100){
            map.put("error_massage","Bot名字不能大于100");
            return map;
        }
        if(description.isEmpty()){
            description = "这个用户很懒，毛也没写~";
        }
        if(description.length()>300){
            map.put("error_massage","描述不能超过300字");
            return map;
        }
        if(content.isEmpty()||content.length()>10000){
            map.put("error_massage","代码不能为空或代码长度过长");
            return map;
        }
        Date now = new Date();
        Bot bot = new Bot(null,user.getId(),name,description,content,now,now);
        botMapper.insert(bot);
        map.put("error_massage","success");
        return map;
    }
}
