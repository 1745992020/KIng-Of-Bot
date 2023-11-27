import 

<template>
    <CardtoAll>
        <div class="row justify-content-md-center" v-if="!$store.state.user.is_pullinginfo">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username">
                    </div>
                    <div class="mb-3">
                        <label for="Password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="Password">
                    </div>
                    <div class="login-to-register">还没有账号?
                        <router-link :to="{ name: 'user_account_register' }">点此去注册</router-link>
                    </div>
                    <div class="error_massage">{{ error_massage }}</div>
                    <button type="submit" class="btn btn-primary">登录</button>
                </form>

            </div>
        </div>
    </CardtoAll>
</template>
<script>
import CardtoAll from "@/components/CardtoAll.vue"
import { useStore } from "vuex"
import { ref } from "vue";
import router from "@/router/index";

export default {
    components: {
        CardtoAll
    },
    setup() {

        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_massage = ref('');

        const login = () => {
            error_massage.value = '';
            store.dispatch("login", {
                username: username.value,
                password: password.value,
                success() {
                    store.dispatch("getinfo", {
                        success() {
                            router.push({ name: "home" });
                        }
                    })

                },
                error() {
                    error_massage.value = "用户名或密码错误";
                }
            })
        }

        return {
            username,
            password,
            error_massage,
            login,
        }
    }
}


</script>
<style scoped>
button {
    width: 100%;
}

div.error_massage {
    color: red;
}

div.login-to-register {
    color: rgb(127, 127, 255);
    font-size: 16px;
}
</style>