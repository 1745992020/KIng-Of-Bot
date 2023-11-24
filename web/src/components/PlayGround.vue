<template>
    <div class="playground">
        <GameMap>游戏地图</GameMap>
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
    width: 70vw;
    height: 70vh;
    margin: 0% auto;
    margin-top: 5%;
    margin-bottom: 1%;
}
</style>