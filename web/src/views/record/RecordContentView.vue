<template>
    <PlayGround></PlayGround>
    <div class="user-color" v-if="$store.state.record.is_record === true">
        <span style="margin-right: 10vw;">蓝色方: <img :src="$store.state.pk.a_photo" alt="蓝色方头像" class="user-photo"> <span>
                {{ $store.state.pk.a_username }}</span></span>
        <span>红色方: <img :src="$store.state.pk.b_photo" alt="红色方头像" class="user-photo"> <span>
                {{ $store.state.pk.b_username }}</span></span>
    </div>
</template>
<script>
import PlayGround from "@/components/PlayGround.vue"

export default {
    components: {
        PlayGround,
    },
    setup() {

    }
}

</script>
<style scoped>
div.user-color {
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    color: white;
}

.user-photo {
    width: 7vh;
    border-radius: 50%;
}

body {
    background-image: url(@/assets/images/background.png);
    background-size: cover;
}
</style>