<template>
    <CardtoAll>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username">
                    </div>
                    <div class="mb-3">
                        <label for="Password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="Password">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword">
                    </div>
                    <div class="error_massage">{{ error_massage }}</div>
                    <button type="submit" class="btn btn-primary">注册</button>
                </form>
            </div>
        </div>
    </CardtoAll>
</template>
<script>
import CardtoAll from "@/components/CardtoAll.vue"
import { ref } from "vue";
import router from "@/router";
import $ from "jquery"

export default {
    components: {
        CardtoAll
    },
    setup() {
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_massage = ref('');
        const register = () => {
            error_massage = '';
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/register/",
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
                },
                success(resp) {
                    if (resp.error_massage === "success") {
                        router.push({ name: 'user_account_login' });
                    }
                    else {
                        error_massage.value = resp.error_massage;
                    }
                },
            });
        };
        return {
            username,
            password,
            confirmedPassword,
            error_massage,
            register,
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
</style>