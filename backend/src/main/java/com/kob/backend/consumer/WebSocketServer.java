package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthotication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.soap.SOAPBinding;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    private static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();
    private User user;
    private Session session = null;

    private static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper=userMapper;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session=session;
        System.out.println("connected!");
        Integer userId = JwtAuthotication.getuserid_from_token(token);
        this.user=userMapper.selectById(userId);
        if(this.user!=null){
            users.put(userId,this);
            System.out.printf(this.user.toString());
        }
        else{
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user!=null){
            users.remove(this.user.getId());
            matchPool.remove(this.user);
        }
    }
    private void startMatching(){
        System.out.println("进入开始匹配函数");
        matchPool.add(this.user);
        System.out.println(matchPool.size());
        while (matchPool.size()>=2){
            Iterator<User> it = matchPool.iterator();
            User a = it.next(),b = it.next();
            matchPool.remove(a);
            matchPool.remove(b);

            Game game = new Game(13,14,20);
            game.CreateMap();
            JSONObject respA = new JSONObject();
            respA.put("error_message","match-success");
            respA.put("opponent_username",b.getUsername());
            respA.put("opponent_photo",b.getPhoto());
            respA.put("gamemap",game.getGameMap());
            users.get(a.getId()).setMassage(respA.toJSONString());
            System.out.println("resA返回");

            JSONObject respB = new JSONObject();
            respB.put("error_message","match-success");
            respB.put("opponent_username",a.getUsername());
            respB.put("opponent_photo",a.getPhoto());
            respB.put("gamemap",game.getGameMap());
            users.get(b.getId()).setMassage(respB.toJSONString());
            System.out.println("resB返回");
        }
    }
    private void stopMatch(){
        System.out.println("进入停止匹配函数");
        matchPool.remove(this.user);
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("receive massage!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatch();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();

    }

    public void setMassage(String massage){
        synchronized (this.session){//上锁
            try {
                this.session.getBasicRemote().sendText(massage);
            }catch (IOException e){
                e.printStackTrace();;
            }
        }
    }
}