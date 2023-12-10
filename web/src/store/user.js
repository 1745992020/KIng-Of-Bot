import router from "@/router";
import $ from "jquery"



export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        waitingtime: 0,
        is_matchsuccess: false,
        interval: null,//方便随时结束计时间循环函数
        is_pullinginfo: true//是否在获取信息，以便获取成功后不显示登陆页面和注册登录的闪屏过程
    },
    getters: {

    },
    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullinginfo(state, is_pullinginfo) {
            state.is_pullinginfo = is_pullinginfo;
        },
        updateWaitingTime(state, waitingtime) {
            state.waitingtime = waitingtime;
        },
        updateIntervalStart(state, interval) {
            state.interval = interval;
        },
        updateIntervalStop(state) {
            clearInterval(state.interval);
            state.interval = null;
        },
        updateIsmatchingSuccess(state, is_matchsuccess) {
            state.is_matchsuccess = is_matchsuccess;
        },
        updateUserPhoto(state, photo) {
            state.photo = photo;
        }
    },
    actions: {
        login(context, data) {
            $.ajax({
                url: "https://www.wangyesheng.online/api/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_massage === "success") {
                        localStorage.setItem("jwt_token", resp.token);//存到浏览器实现持久化登录
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    }
                    else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                },
            });
        },
        getinfo(context, data) {
            $.ajax({
                url: "https://www.wangyesheng.online/api/user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp) {
                    if (resp.error_massage === "success") {
                        context.commit("updateUser", {
                            ...resp,
                            is_login: true,
                        })
                        data.success(resp);
                    }
                    else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                },
            });
        },
        logout(context) {
            localStorage.removeItem("jwt_token");//从浏览器删除token
            context.commit("logout");
            router.push({ name: 'user_account_login' });
        }
    },
    modules: {

    },
}