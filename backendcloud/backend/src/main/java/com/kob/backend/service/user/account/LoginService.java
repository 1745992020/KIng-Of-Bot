package com.kob.backend.service.user.account;

import java.util.Map;

public interface LoginService {
    Map<String,String> Login(String username, String password);
}
