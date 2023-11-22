package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    BotMapper botMapper;
    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        int bot_id = Integer.parseInt(data.get("bot_id"));
        String name = data.get("name");
        String description = data.get("description");
        String content = data.get("content");
        Map<String,String> map = new HashMap<>();
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
        Bot bot = botMapper.selectById(bot_id);
        if(bot==null){
            map.put("error_massage","Bot不存在或已被删除");
            return map;
        }
        if(!bot.getUserId().equals(user.getId())){
            map.put("error_massage","别瞎JB删别人Bot");
            return map;
        }
        Bot new_bot = new Bot(
                bot.getId(),
                bot.getUserId(),
                name,
                description,
                content,
                bot.getRating(),
                bot.getCreatetime(),
                new Date()
        );
        botMapper.updateById(new_bot);
        map.put("error_massage","success");
        return map;
    }
}
