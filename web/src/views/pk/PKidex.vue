<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"></PlayGround>
    <MatchGround v-if="$store.state.pk.status === 'matching'"></MatchGround>
    <ResultBoard v-if="$store.state.pk.loser !== 'none'"></ResultBoard>
    <div class="user-color" v-if="$store.state.pk.status === 'playing' && $store.state.pk.a_id == $store.state.user.id">
        你是蓝色方
    </div>
    <div class="user-color" v-if="$store.state.pk.status === 'playing' && $store.state.pk.b_id == $store.state.user.id">
        你是红色方
    </div>
</template>
<script>
import PlayGround from "@/components/PlayGround.vue"
import MatchGround from "@/components/MatchGroune.vue"
import ResultBoard from "@/components/ResultBoard.vue"
import { onMounted, onUnmounted } from "vue";
import { useStore } from "vuex";

export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup() {
        const store = useStore();
        const socketUrl = `wss://www.wangyesheng.online/websocket/${store.state.user.token}`;
        store.commit("updateIsRecord", false);//改为不是录像

        let socket = null;
        onMounted(() => {
            store.commit("updateOpponent", {
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
                username: "未知对手"
            })
            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                //console.log("connected!");
                store.commit("updateSocket", socket);
            }
            socket.onmessage = masg => {
                const data = JSON.parse(masg.data);
                if (data.event === "success") {//匹配成功
                    //console.log("匹配成功")
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    })
                    store.commit("updateIsmatchingSuccess", true);
                    store.commit("updateIntervalStop");
                    store.commit("updateWaitingTime", 0);
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                        store.commit("updateIsmatchingSuccess", false);
                    }, 200)//0.2秒之后执行更新为对战页面
                    store.commit("updateGame", data.game);
                } else if (data.event === "move") {
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.Snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                } else if (data.event === "result") {
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.Snakes;
                    if (data.loser === "all" || data.loser === "A") {
                        snake0.status = "die";
                    }
                    if (data.loser === "all" || data.loser === "B") {
                        snake1.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }
            }
            socket.onclose = () => {
                //console.log("disconnected!");
            }
        })
        onUnmounted(() => {
            //console.log('取消挂载')
            socket.close();
            store.commit("updateLoser", "none");
            store.state.pk.socket = null;
            store.commit("updateStatus", "matching");
            store.commit("updateIntervalStop");
            store.commit("updateWaitingTime", 0);
        })
    }
}

</script>
<style scoped>
div.user-color {
    text-align: center;
    font-size: 30px;
    font-weight: 600;
    color: white;
}
</style>