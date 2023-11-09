<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container">
            <router-link class="navbar-brand" :to="{ name: 'home' }">King Of Bot</router-link>
            <div class="collapse navbar-collapse" id="navbarText">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <router-link :class="now_name == 'pk_idx' ? 'nav-link active' : 'nav-link'" aria-current="page"
                            :to="{ name: 'pk_idx' }">对战</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="now_name == 'record_idx' ? 'nav-link active' : 'nav-link'"
                            :to="{ name: 'record_idx' }">对局列表</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="now_name == 'ranklist_idx' ? 'nav-link active' : 'nav-link'"
                            :to="{ name: 'ranklist_idx' }">排行榜</router-link>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                            aria-expanded="false">
                            我的
                        </a>
                        <ul class="dropdown-menu">
                            <li><router-link class="dropdown-item" :to="{ name: 'bot_idx' }">我的Bot</router-link></li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li> <a class="dropdown-item" href="#"> 退出 </a> </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>
<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue';
export default {
    setup() {
        const rout = useRoute();
        let now_name = computed(() => rout.name);
        return {
            now_name
        }
    }
}
</script>
<style scoped></style>