import { GameObject } from "./GameObjict";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends GameObject {
    constructor(ctx, parent) {
        super();
        this.ctx = ctx;//画布
        this.parent = parent;//画布父元素，用来动态修改画布长宽
        this.L = 0;//一个单位的长度
        this.rows = 13;
        this.cols = 14;
        this.inner_walls_count = 30//内部障碍物数量
        this.walls = [];//墙数组
        this.Snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this),
        ];
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
                if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;//中心堆成画图
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;//判断使其不生成在左下角和右上角
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;//中心堆成画图
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

    add_listening_events() {
        this.ctx.canvas.focus();

        const [snake0, snake1] = this.Snakes;
        this.ctx.canvas.addEventListener("keydown", e => {//键盘控制移动
            if (e.key === "w") snake0.set_direction(0);
            else if (e.key === "d") snake0.set_direction(1);
            else if (e.key === "s") snake0.set_direction(2);
            else if (e.key === "a") snake0.set_direction(3);
            else if (e.key === "ArrowUp") snake1.set_direction(0);
            else if (e.key === "ArrowRight") snake1.set_direction(1);
            else if (e.key === "ArrowDown") snake1.set_direction(2);
            else if (e.key === "ArrowLeft") snake1.set_direction(3);
        });
    }
    start() {
        for (let i = 0; i < 1000; i++)//一直循环如果发现联通就break
        {
            if (this.create_wall()) break;
        }
        this.add_listening_events();

    }
    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }
    check_ready() {//判断两条蛇是否都准备好下一回合
        for (const snake of this.Snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }
    next_step() {//让两条蛇分别进入下一步
        for (const snake of this.Snakes) {
            snake.Snake_next_step();
        }
    }
    check_valid(cell) {//检测目标位置是否合法
        for (const wall of this.walls) {
            if (wall.c === cell.c && wall.r === cell.r) {
                return false;
            }
        }
        for (const snake of this.Snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing)//当蛇尾缩进近时，此时走到蛇尾是合法的
                k--;
            for (let i = 0; i < k; i++) {
                if (snake.cells[i].c === cell.c && snake.cells[i].r === cell.r)
                    return false;
            }
        }
        return true;
    }
    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();
    }
    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";//奇数偶数画不同颜色的小绿格子
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