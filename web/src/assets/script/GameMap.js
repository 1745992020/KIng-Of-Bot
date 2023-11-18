import { GameObject } from "./GameObjict";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends GameObject {
    constructor(ctx, parent, store) {
        super();
        this.store = store;
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

    create_wall() {
        const g = this.store.state.pk.gamemap;
        //根据布尔数组画墙
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
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
        this.create_wall();
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