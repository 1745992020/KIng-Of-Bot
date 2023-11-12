import { GameObject } from "./GameObjict";
import { Wall } from "./Wall";

export class GameMap extends GameObject {
    constructor(ctx, parent) {
        super();
        this.ctx = ctx;//画布
        this.parent = parent;//画布父元素，用来动态修改画布长宽
        this.L = 0;//一个单位的长度
        this.rows = 13;
        this.cols = 13;
        this.inner_walls_count = 30//内部障碍物数量
        this.walls = [];//墙数组
    }
    check_connectivity(g, sx, sy, tx, ty) {//判断生成的地图是否联通
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = true;
        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i++) {
            let x = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }
        return false;
    }
    create_wall() {
        const g = [];//开布尔数组判断是否有墙
        for (let r = 0; r < this.rows; r++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c++) {
                g[r][c] = false;
            }
        }
        //给四周加上墙(true表示墙)
        for (let r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = true;
        }
        for (let c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = true;
        }
        //随机内部障碍物
        for (let i = 0; i < this.inner_walls_count / 2; i++) {
            for (let j = 0; j < 1000; j++) {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[c][r]) continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;
                g[r][c] = g[c][r] = true;
                break;
            }
        }
        const copy_g = JSON.parse(JSON.stringify(g));//复制一份地防止修改
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;//不连通返回false
        //根据布尔数组画墙
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        return true;//联通返回true
    }
    start() {
        for (let i = 0; i < 1000; i++)//一直循环如果发现联通就break
        {
            if (this.create_wall()) break;
        }

    }
    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }
    update() {
        this.update_size();
        this.render();
    }
    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                }
                else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }

        }
    }
}