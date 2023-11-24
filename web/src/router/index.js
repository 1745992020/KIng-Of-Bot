import { createRouter, createWebHistory } from 'vue-router'

import PKidex from "@/views/pk/PKidex"
import RecordIdex from "@/views/record/RecordIdex"
import RecordContentView from "@/views/record/RecordContentView"
import RankListidex from "@/views/ranklist/RankListidex"
import BotIdex from "@/views/user/bot/BotIdex"
import NotFound from "@/views/error/NotFound"
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView"
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView"
import HomeView from "@/components/HomeView"
import store from '@/store/index'

const routes = [
  {
    path: "/home/",
    name: "home",
    component: HomeView,
    //redirect: "/pk/",
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/pk/",
    name: "pk_idx",
    component: PKidex,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/record/",
    name: "record_idx",
    component: RecordIdex,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/record/:recordId/",
    name: "record_content",
    component: RecordContentView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_idx",
    component: RankListidex,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/bot/",
    name: "bot_idx",
    component: BotIdex,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/404/",
    name: "Not_idx",
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})


router.beforeEach((to, from, next) => {//在路由跳转之前会执行的函数 to 目标 from 由来 next 下一站
  const jwt_token = localStorage.getItem("jwt_token");
  if (jwt_token) {
    store.commit("updateToken", jwt_token);
    store.dispatch("getinfo", {
      success() {
        next();
        store.commit("updatePullinginfo", false);
      },
      error() {
        store.commit("updatePullinginfo", false);
        next({ name: 'user_account_login' });
      }
    })
  }
  else {
    store.commit("updatePullinginfo", false);
    if (to.meta.requestAuth && !store.state.user.is_login) {//需要授权且没登陆
      alert("请先登录！")
      next({ name: 'user_account_login' });//下一站到登录或注册页面
    }
    else {
      next();//到本来的下一站
    }
  }
})

export default router
