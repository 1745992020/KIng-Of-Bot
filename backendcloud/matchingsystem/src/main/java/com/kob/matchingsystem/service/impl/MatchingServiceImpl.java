package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {

    public final static MatchingPool matchingPool = new MatchingPool();
    @Override
    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("add_Player"+userId+" "+rating);
        matchingPool.addPlayer(userId,rating);
        return "add_success";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove_Player"+userId);
        matchingPool.removePlayer(userId);
        return "remove_success";
    }
}
