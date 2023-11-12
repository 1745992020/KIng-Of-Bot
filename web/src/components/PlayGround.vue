<template>
    <div class="playground">
        <GameMap>游戏区域</GameMap>
    </div>
</template>

<script>
import GameMap from "@/components/GameMap.vue"
export default {
    components: {
        GameMap,
    }
}

</script>

<style scoped>
div.playground {
    /* background-color: rgb(0, 46, 57); */
    width: 60vw;
    height: 70vh;
    margin: 5% auto;
}
</style>