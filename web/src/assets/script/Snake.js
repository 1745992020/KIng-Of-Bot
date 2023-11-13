import { GameObject } from "./GameObjict";
import { Cell } from "./Cell";
export class Snake extends GameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)];//存放蛇，cell[0]存放蛇头

        this.next_cell = null;//下一步的目的地
        this.speed = 5;//蛇的速度
        this.direction = -1;//-1表示没有指令0，1，2，3表示上右下左
        this.status = "idle";//idle表示静止，move表示移动，die表示死亡

        this.dr = [-1, 0, 1, 0];//四个方向行的偏移量
        this.dc = [0, 1, 0, -1];//四个方向列的偏移量

        this.step = 0;// 当前回合数
        this.eps = 1e-2;//允许的误差

        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2;//初始化左下角蛇眼睛向上右上角蛇眼睛向下

        this.eye_dx = [//眼睛的x方向偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [//眼睛的y方向偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ];
    }
    start() {

    }

    set_direction(d) {//获取输入下一步
        this.direction = d;
    }

    check_tail_increasing() {//检测当前回合蛇是否边长
        if (this.step <= 10) return true;//前十回合每回合边长
        if (this.step % 3 === 1) return true;//十回合以后每三回合边长一次
        return false;
    }

    Snake_next_step() {//更新蛇的状态为下一步
        const d = this.direction;
        this.eye_direction = d;//更新蛇眼睛方向
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);//下一步
        this.direction = -1;//清空
        this.status = "move";
        this.step++;

        const k = this.cells.length;
        for (let i = k; i > 0; i--) {//每个元素向后移动一位，相当于头部复制
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));//js赋值是引用，要转化为JSON在转化回来，来实现赋值
        }
        if (!this.gamemap.check_valid(this.next_cell)) {//如果下一步不合法，蛇嘎
            this.status = "die";
        }
    }

    update_move() {

        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps) {//走到目标点后
            this.cells[0] = this.next_cell;//将目标点作为头部
            this.next_cell = null;//将下一步清空
            this.status = "idle";//走完更新为静止

            if (!this.check_tail_increasing()) {//蛇不变长就砍掉蛇尾
                this.cells.pop();
            }

        }
        else {
            const move_distance = this.timedelta / 1000 * this.speed;//每两帧之间走过的距离
            this.cells[0].x += move_distance * dx / distance;//移动虚拟蛇头
            this.cells[0].y += move_distance * dy / distance;

            if (!this.check_tail_increasing()) {//如果蛇尾不需要变长那么就需要移动蛇尾，需要变长则不需要移动蛇尾
                const k = this.cells.length;
                const tail = this.cells[k - 1];//蛇尾
                const tail_target = this.cells[k - 2];//蛇尾前一个，即蛇尾移动的目标点
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;//移动蛇尾
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() {
        if (this.status === "move") {
            this.update_move();
        }

        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        ctx.fillStyle = this.color;
        if (this.status === "die") {//死亡变为白色
            ctx.fillStyle = "white";
        }
        for (const cell of this.cells) {//画蛇
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);//画圈
            ctx.fill();
        }
        for (let i = 1; i < this.cells.length; i++) {//在每两个相邻的圆之间画一个长方形使蛇看起来更好看
            const a = this.cells[i - 1], b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)//已经很接近就不画了
                continue;
            if (Math.abs(a.x - b.x) < this.eps) {
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);//根据canvas坐标系转化
            }
            else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        ctx.fillStyle = "black";//画眼睛
        for (let i = 0; i < 2; i++) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.20) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.20) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.07, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}