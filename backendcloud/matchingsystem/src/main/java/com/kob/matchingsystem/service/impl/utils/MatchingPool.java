package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock =new ReentrantLock();
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/startGame/";
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate=restTemplate;
    }

    public void addPlayer(Integer userId,Integer rating){
        lock.lock();
        try {
            players.add(new Player(userId,rating,0));
        }finally {
            lock.unlock();
        }
    }
    public void removePlayer(Integer userId){
        lock.lock();
        try {
            List<Player> newPlayers =new ArrayList<>();
            for(Player player:players){
                if(!player.userId.equals(userId)){
                    newPlayers.add(player);
                }
            }
            players=newPlayers;
        }finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime(){//更新所有玩家等待时间加1
        for (Player player:players){
            player.waitingTime++;
        }
    }
    private boolean checkMatched(Player a,Player b){//判断两名玩家是否匹配
        int ratingDelta = Math.abs(a.rating-b.rating);
        int waitTime = Math.max(a.waitingTime,b.waitingTime);
        return ratingDelta <= waitTime*10;
    }
    private void sendResult(Player a,Player b){//返回a，b的匹配结果
        //System.out.println("匹配成功"+a+" "+b);
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("a_id",a.userId.toString());
        data.add("b_id",b.userId.toString());
        restTemplate.postForObject(startGameUrl,data,String.class);
    }
    private void matchPlayers(){//尝试匹配所有玩家
        //System.out.println("匹配所有玩家"+players.toString());
        if(players.size()==1){
            Player a = players.get(0);
            //System.out.println(a.toString());
            if(a.waitingTime>30){
                Player bot = new Player(2,1500,0);
                sendResult(a,bot);
                players.clear();
            }
        }
        else {
            boolean[] used = new boolean[players.size()];
            for (int i = 0; i < players.size(); i++) {
                if (used[i]) continue;
                for (int j = i + 1; j < players.size(); j++) {
                    if (used[j]) continue;
                    Player a = players.get(i), b = players.get(j);
                    if (checkMatched(a, b)) {
                        used[i] = used[j] = true;
                        sendResult(a, b);
                        break;
                    }
                }
            }
            List<Player> newPlayers = new ArrayList<>();
            for (int i = 0; i < players.size(); i++) {
                if (!used[i]) {
                    newPlayers.add(players.get(i));
                }
            }
            players = newPlayers;
        }
    }
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                }finally {
                    lock.unlock();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
