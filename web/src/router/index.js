import { createRouter, createWebHistory } from 'vue-router'

import PKidex from "@/views/pk/PKidex"
import RecordIdex from "@/views/record/RecordIdex"
import RankListidex from "@/views/ranklist/RankListidex"
import BotIdex from "@/views/user/bot/BotIdex"
import NotFound from "@/views/error/NotFound"
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView"
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView"


const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
  },
  {
    path: "/pk/",
    name: "pk_idx",
    component: PKidex,
  },
  {
    path: "/record/",
    name: "record_idx",
    component: RecordIdex,
  },
  {
    path: "/ranklist/",
    name: "ranklist_idx",
    component: RankListidex,
  },
  {
    path: "/user/bot/",
    name: "bot_idx",
    component: BotIdex,
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
  },
  {
    path: "/404/",
    name: "Not_idx",
    component: NotFound,
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

export default router
