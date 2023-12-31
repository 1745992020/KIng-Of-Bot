package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.generator.ITemplate;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;//开布尔数组判断是否有墙
    private static final int[] dx = {-1, 0, 1, 0};
    private static final int[] dy = {0, 1, 0, -1};
    @Getter
    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private final ReentrantLock lock = new ReentrantLock();

    private String status = "playing";//表示游戏状态“playing”或“finished”
    private String loser = "";//all平局,A输,B输
    private final String addBotUrl = "http://127.0.0.1:3002/bot/add/";


    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Bot botA, Integer idB,Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        Integer botIdA = -1 , botIdB = -1;
        String botCodeA = "" , botCodeB = "";
        if(botA!=null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB!=null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA,botIdA,botCodeA,rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB,botIdB,botCodeB,1, cols - 2, new ArrayList<>());
    }

    public int[][] getGameMap() {
        return g;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }

    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }

    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    private boolean DrawWalls() {
        //初始化为0
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                g[i][j] = 0;
            }
        }
        //给四周加上墙(1表示墙)
        for (int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }
        Random random = new Random();
        //随机内部障碍物
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;//中心对称画图
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;//判断使其不生成在左下角和右上角
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;//中心对称画图
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void CreateMap() {//画1000次直到联通就停止
        for (int i = 0; i < 1000; i++) {
            if (DrawWalls()) break;
        }
    }

    private String getInput(Player player){//编码当前局面信息
        //地图#me_sx#me_sy#me_steps#you_dx#you_dy#you_steps#
        Player me,you;
        if(playerA.getId().equals(player.getId())){
            me = playerA;
            you = playerB;
        }else {
            me = playerB;
            you = playerA;
        }
        return getMapString() + "#" + me.getSx() + "#" + me.getSy() + "#(" + me.getstepsString() + ")#" + you.getSx() +
                "#" + you.getSy() + "#(" + you.getstepsString() + ")";
    }

    private void sendBotCode(Player player){
        if (player.getBotId().equals(-1)) return;//亲自出马
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",player.getId().toString());
        data.add("bot_code",player.getBotCode());
        data.add("input",getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl,data,String.class);
    }

    public boolean nextStep() {//获取两名玩家下一步操作
        try {
            Thread.sleep(200);//前端渲染时200ms画一次，防止多次的输入都被覆盖而只渲染最后一步
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean check_valid(List<Cell> SnackA,List<Cell> SnackB){//判断蛇头下一步操作是否合法
        int n=SnackA.size();
        Cell head = SnackA.get(n-1);
        if(g[head.x][head.y] == 1 )return false;
        for (int i = 0; i < n-1; i++) {
            if(SnackA.get(i).x == head.x && SnackA.get(i).y == head.y)return false;
        }
        for (int i = 0; i < n-1; i++) {
            if(SnackB.get(i).x == head.x && SnackB.get(i).y == head.y)return false;
        }
        return true;
    }


    private void judge() {  // 判断两名玩家下一步操作是否合法
        List<Cell> SnackA = playerA.getCells();
        List<Cell> SnackB = playerB.getCells();

        boolean validA = check_valid(SnackA, SnackB);
        boolean validB = check_valid(SnackB, SnackA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    public void sendAllMessage(String Message){//广播信息
        if(WebSocketServer.users.get(playerA.getId()) != null) {
            WebSocketServer.users.get(playerA.getId()).setMassage(Message);
        }
        if(WebSocketServer.users.get(playerB.getId()) != null || playerB.getId()!=2){
            WebSocketServer.users.get(playerB.getId()).setMassage(Message);
        }

    }
    public void sendMove(){//发送移动情况
        lock.lock();
        try {
            JSONObject res = new JSONObject();
            res.put("event","move");
            res.put("a_direction",nextStepA);
            res.put("b_direction",nextStepB);
            sendAllMessage(res.toJSONString());
            nextStepA=nextStepB=null;
        }finally {
            lock.unlock();
        }

    }
    private String getMapString(){
        StringBuilder res= new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player,Integer rating){
         User user = WebSocketServer.userMapper.selectById(player.getId());
         user.setRating(rating);
         WebSocketServer.userMapper.updateById(user);
    }
    private void updateMatchCount(Player player ,Integer matchcount){
        User user =WebSocketServer.userMapper.selectById(player.getId());
        user.setMatchcount(matchcount);
        WebSocketServer.userMapper.updateById(user);
    }
    public void saveRecord(){

        Integer matchcountA = WebSocketServer.userMapper.selectById(playerA.getId()).getMatchcount();
        Integer matchcountB = WebSocketServer.userMapper.selectById(playerB.getId()).getMatchcount();

        matchcountA++;
        matchcountB++;

        updateMatchCount(playerA,matchcountA);
        if(playerB.getId()!=2)
            updateMatchCount(playerB,matchcountB);

        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if("A".equals(loser)){
            ratingA-=2;
            ratingB+=5;
        } else if ("B".equals(loser)) {
            ratingA+=5;
            ratingB-=2;
        }
        updateUserRating(playerA,ratingA);
        if(playerB.getId()!=2)
            updateUserRating(playerB,ratingB);
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerA.getSx(),
                playerB.getSy(),
                playerA.getstepsString(),
                playerB.getstepsString(),
                getMapString(),
                loser,
                new Date()
        );
    WebSocketServer.recordMapper.insert(record);
    }
    public void sendResult(){//发送结果
        JSONObject res = new JSONObject();
        res.put("event","result");
        res.put("loser",loser);
        saveRecord();//保存对局记录
        sendAllMessage(res.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if(nextStep()){//是否获取到两个输入
                judge();
                if(status.equals("playing")){
                    sendMove();
                }else {
                    sendResult();
                    break;
                }
            }
            else{
                status  = "finished";
                lock.lock();
                try {
                    if(nextStepA==null && nextStepB==null){
                        loser="all";
                    }
                    else if(nextStepA==null){
                        loser="A";
                    } else if (nextStepB==null) {
                        loser="B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
