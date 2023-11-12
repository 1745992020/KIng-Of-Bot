const GAME_OBJECTS = []

export class GameObject {
    constructor() {
        GAME_OBJECTS.push(this);
        this.timedelta = 0;//时间间隔
        this.has_start = false;//是否执行过start
    }

    start() { //只执行一次 

    }
    update() { //每帧执行一次

    }
    on_destroy() { //删除之前执行

    }
    destroy() {//删除对象
        this.on_destroy();
        for (let i in GAME_OBJECTS) {
            const obj = GAME_OBJECTS[i];
            if (obj == this) {
                GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}
let last_timestamp; //上次执行时间
const step = (timestamp) => {
    for (let obj of GAME_OBJECTS) {
        if (!obj.has_start) {
            obj.has_start = true;
            obj.start();
        }
        else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp;
    requestAnimationFrame(step);
}
requestAnimationFrame(step);