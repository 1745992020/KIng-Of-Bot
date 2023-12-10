package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserUpdateController {
    @Autowired
    UserUpdateService userUpdateService;

    @PostMapping("/api/user/update/")
    public Map<String,String> update(@RequestParam Map<String,String> data){

        return userUpdateService.update(data);
    }
}
