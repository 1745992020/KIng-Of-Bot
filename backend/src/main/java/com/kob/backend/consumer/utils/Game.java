package com.kob.backend.consumer.utils;

import java.util.Random;

public class Game {
    private Integer rows;
    private Integer cols;
    private Integer inner_walls_count;
    private final int[][] g;//开布尔数组判断是否有墙
    private static int[] dx = {-1,0,1,0},dy={0,1,0,-1};
    public Game(Integer rows,Integer cols,Integer inner_walls_count){
         this.rows=rows;
         this.cols=cols;
         this.inner_walls_count=inner_walls_count;
         this.g= new int[rows][cols];
    }
    public int[][] getGameMap(){
        return  g;
    }
    private boolean check_connectivity(int sx,int sy,int tx,int ty) {//判断生成的地图是否联通
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for (int i = 0; i < 4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x>=0 && x<this.rows && y>=0 && y<this.rows && g[x][y]==0 && check_connectivity(x,y,tx,tx)){
                g[sx][sy]=0;
                return true;
            }
        }
        g[sx][sy] = 0;
        return false;
    }
    private boolean DrawWalls(){
        //初始化为0
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < cols; j++) {
                g[i][j]=0;
            }
        }
        //给四周加上墙(1表示墙)
        for (int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols-1] = 1;
        }
        for (int c = 0; c < this.cols; c++) {
            g[0][c]=g[this.rows-1][c] = 1;
        }
        Random random =new Random();
        //随机内部障碍物
        for (int i = 0; i < this.inner_walls_count/2; i++) {
            for(int j = 0; j<1000;j++){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if (g[r][c] ==1 || g[this.rows - 1 - r][this.cols - 1 - c]==1) continue;//中心对称画图
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;//判断使其不生成在左下角和右上角
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;//中心对称画图
                break;
            }
        }
        return check_connectivity(this.rows-2,1,1,this.cols-2);
    }
    public void CreateMap(){//画1000次直到联通就停止
        for (int i = 0; i < 1000; i++) {
            if(DrawWalls())break;
        }
    }
}
