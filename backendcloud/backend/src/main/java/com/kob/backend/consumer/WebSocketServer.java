package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthotication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    public static  ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer,Integer> bots = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;
    public Game game = null;
    public static Game gamebot =null;
    private final static String addPlayerUrl="http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl="http://127.0.0.1:3001/player/remove/";
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static RestTemplate restTemplate;
    private static BotMapper botMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper=userMapper;
    }
    @Autowired
    public void  setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper=recordMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate=restTemplate;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper){WebSocketServer.botMapper=botMapper;}
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session=session;
        //System.out.println("connected!");
        Integer userId = JwtAuthotication.getuserid_from_token(token);
        this.user=userMapper.selectById(userId);
        if(this.user!=null){
            users.put(userId,this);
        }
        else{
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        //System.out.println("disconnected!");
        if(this.user!=null){
            users.remove(this.user.getId());
            MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
            data.add("user_id",this.user.getId().toString());
            restTemplate.postForObject(removePlayerUrl,data,String.class);
        }
    }
    public static void startGame(Integer aId,Integer bId){
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Integer aBotId = bots.get(aId);
        Integer bBotId = 0;
        if(bId==2){
            bBotId = 1;
        }else{
            bBotId = bots.get(bId);
        }
        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);

        Game game = new Game(13,14,20,a.getId(),botA,b.getId(),botB);
        game.CreateMap();
        if(users.get(a.getId()) != null){
            users.get(a.getId()).game = game;
        }
        if(users.get(b.getId()) != null && bId!=2){
            users.get(b.getId()).game = game;
        } else if (bId==2) {
            gamebot = game;
        }

        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id",game.getPlayerA().getId());
        respGame.put("a_sx",game.getPlayerA().getSx());
        respGame.put("a_sy",game.getPlayerA().getSy());
        respGame.put("b_id",game.getPlayerB().getId());
        respGame.put("b_sx",game.getPlayerB().getSx());
        respGame.put("b_sy",game.getPlayerB().getSy());
        respGame.put("gamemap",game.getGameMap());

        JSONObject respA = new JSONObject();
        respA.put("event","success");
        respA.put("opponent_username",b.getUsername());
        respA.put("opponent_photo",b.getPhoto());
        respA.put("game",respGame);
        if(users.get(a.getId()) != null){
            users.get(a.getId()).setMassage(respA.toJSONString());
        }

        if(bId!=2) {
            JSONObject respB = new JSONObject();
            respB.put("event", "success");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("game", respGame);
            if (users.get(b.getId()) != null) {
                users.get(b.getId()).setMassage(respB.toJSONString());
            }
        }
    }
    private void startMatching(Integer botId){
        //System.out.println("进入开始匹配函数");
        bots.put(this.user.getId(),botId);//存下选手的botId
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl,data,String.class);
    }
    private void stopMatch(){
        //System.out.println("进入停止匹配函数");
        bots.remove(this.user.getId());//删除选手的botId
        MultiValueMap<String,String> data =new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);
    }
    private void move(int direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1))//亲自出马
                game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            if(game.getPlayerB().getBotId().equals(-1))//亲自出马
                game.setNextStepB(direction);
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        //System.out.println("receive massage!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        }
        else if ("stop-matching".equals(event)) {
            stopMatch();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
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