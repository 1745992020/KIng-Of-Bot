<template>
    <CardtoAll>
        Bot
    </CardtoAll>
</template>
<script>
import CardtoAll from "@/components/CardtoAll.vue"
//import $ from "jquery"
//import { useStore } from "vuex";


export default {
    components: {
        CardtoAll
    },
    setup() {
        //const store = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "post",
        //     data: {
        //         name: "Bot_name",
        //         description: "Bot_描述",
        //         content: "bot代码",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     },
        // })
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "post",
        //     data: {
        //         bot_id: 4,
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     },
        // })
    }
}


</script>
<style scoped></style>