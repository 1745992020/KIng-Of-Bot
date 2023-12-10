package com.kob.backend.service.impl.user.account;

import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserUpdateServiceImpl implements UserUpdateService {

    @Autowired
    UserMapper userMapper;
    @Override
    public Map<String,String> update(Map<String, String> data) {
        String photo = data.get("photo_url");

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        System.out.println(photo);
        userMapper.updateById(
             new User(
                     user.getId(),
                     user.getUsername(),
                     user.getPassword(),
                     photo,
                     user.getRating(),
                     user.getMatchcount()
             )
        );
        Map<String,String> map = new HashMap<>();
        map.put("error_message","success");
        map.put("photo",photo);
        return map;
    }
}
