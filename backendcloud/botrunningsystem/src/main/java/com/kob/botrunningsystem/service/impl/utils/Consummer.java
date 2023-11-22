package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterfaceService;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class Consummer extends Thread {
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consummer.restTemplate=restTemplate;
    }
    public void startTimeout(long timeout,Bot bot){
        this.bot=bot;
        this.start();

        try {
            this.join(timeout);//最多等待timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt();//中断进程
        }
    }

    private String addUidToCode(String code,String uid){
        int k =code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterfaceService");
        return code.substring(0,k)+uid+code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid =UUID.randomUUID();
        String uid = uuid.toString().substring(0,8);

        BotInterfaceService botInterfaceService = Reflect.compile(
                "com.kob.botrunningsystem.utils.BOt"+uid,
                addUidToCode(bot.getBotCode(),uid)
        ).create().get();

        Integer direction = botInterfaceService.nextMove(bot.getInput());
        MultiValueMap<String,String> data= new LinkedMultiValueMap<>();
        data.add("user_id",bot.userId.toString());
        data.add("direction",direction.toString());
        restTemplate.postForObject(receiveBotMoveUrl,data,String.class);
    }
}