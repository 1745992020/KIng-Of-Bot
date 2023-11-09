<template>
  <div>
    <div>名字：{{ body_name }}</div>
    <div>分数：{{ body_score }}</div>
  </div>
  <router-view />
</template>

<script>
import $ from "jquery";
import { ref } from "vue";
export default {
  name: "App",
  setup() {
    let body_name = ref("");
    let body_score = ref("");
    $.ajax({
      url: "http://127.0.0.1:3000/pk/info/",
      type: "get",
      success: resp => {
        body_name.value = resp.name;
        body_score.value = resp.score;
      }
    });
    return {
      body_name,
      body_score
    }
  }
}

</script>
<style>
body {
  background-image: url(@/assets/1_59b8f162fd-background.png);
  background-size: cover;
}
</style>
