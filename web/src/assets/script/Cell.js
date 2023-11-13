export class Cell {
    constructor(r, c) {//蛇的每一节
        this.r = r;
        this.c = c;
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}