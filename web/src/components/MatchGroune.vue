<template>
    <div class="matchground">
        <div class="row">
            <div class="col-5">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="头像">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-2">
                <div class="select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>亲自出马</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                            {{ bot.name }}
                        </option>
                    </select>
                </div>
            </div>
            <div class="col-5">
                <div class="opponent-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="对手头像">
                </div>
                <div class="opponent-username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center;padding-top: 15vh;">
                <button @click="click_match_btn" type="button" class="btn btn-outline-light btn-lg">{{ match_btn_info
                }}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import $ from "jquery"

export default {
    components: {
    },
    setup() {
        const store = useStore();
        let match_btn_info = ref("寻找对手");
        let bots = ref([]);
        let select_bot = ref("-1");

        const click_match_btn = () => {

            if (match_btn_info.value === "寻找对手") {
                match_btn_info.value = "取消";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    //bot_id:select_bot,
                }));
            } else {
                match_btn_info.value = "寻找对手";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
            }
        }

        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getList/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }
        refresh_bots();
        return {
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }


}

</script>

<style scoped>
div.matchground {
    width: 60vw;
    height: 70vh;
    margin: 5% auto;
    background-color: rgba(50, 50, 50, 0.4);
}

div.user-photo,
div.opponent-photo {
    text-align: center;
}

div.user-photo>img,
div.opponent-photo>img {
    height: 25vh;
    border-radius: 50%;
    margin-top: 10%;
}

div.user-username,
div.opponent-username {
    margin-top: 3%;
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    color: beige;
}

.select-bot {
    margin-top: 15vh;
}
</style>