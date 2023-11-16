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
                <ul class="navbar-nav" v-if="$store.state.user.is_login">
                    <li class="nav-item dropdown">
                        <div> <img src="" alt="头像"></div>
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                            aria-expanded="false">
                            {{ $store.state.user.username }}
                        </a>
                        <ul class="dropdown-menu">
                            <li><router-link class="dropdown-item" :to="{ name: 'bot_idx' }">我的Bot</router-link></li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li> <a class="dropdown-item" @click="logout"> 退出 </a> </li>
                        </ul>
                    </li>
                </ul>
                <ul class="navbar-nav" v-else-if="!$store.state.user.is_pullinginfo">
                    <li class="nav-item">
                        <router-link class="nav-link" :to="{ name: 'user_account_login' }" role="button">
                            登录
                        </router-link>
                    </li>
                    <li class="nav-item">
                        <router-link class="nav-link" :to="{ name: 'user_account_register' }" role="button">
                            注册
                        </router-link>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>
<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue';
import { useStore } from 'vuex';

export default {
    setup() {
        const rout = useRoute();
        const store = useStore();
        let now_name = computed(() => rout.name);

        const logout = () => {
            store.dispatch("logout");
        }
        return {
            now_name,
            logout,
        }
    }
}
</script>
<style scoped></style>