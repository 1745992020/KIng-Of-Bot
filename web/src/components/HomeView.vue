<template>
    <div class="container text-center">
        <div class="welcom-text">
            欢迎来到 Snake of Bot
            你可以点击
            <router-link :to="{ name: 'pk_idx' }" class="rule">对战</router-link>
            来进行对局~
        </div>
        <div class="row">
            <div class="col-8">
                <div class="card-color">
                    <div class="card-title">
                        <h2>网站介绍：</h2>
                        <p class="text-weit">本网站是一个对抗平台，可以创建属于自己的AI来可以根据已有的游戏规则进行比赛一决胜负。同时支持人机，人人对战</p>
                    </div>
                    <div class="card-body" style="text-align: left;">
                        <h3 class="text-center">使用方法：</h3>
                        <ol class="text-weit">
                            <li>注册 Snake of Bot 账户</li>
                            <li>点击右边游戏规则 查看Snake游戏规则,按照游戏说明写自己的 AI 程序(或者直接点击对战亲自出马)</li>
                            <li>点击右上角账户名，进入我的 Bot 页面，创建 Bot 并上传程序</li>
                            <li>回到对战页面，点击开始匹配，与他人或其他 Bot 开始对战</li>
                        </ol>
                        &nbsp;
                    </div>
                </div>
            </div>
            <div class="col-4 ">
                <div class="card rules">
                    <div class="card-body">
                        欲查规则？点击
                        <a class="rule"
                            href="https://www.yuque.com/u39296687/gepfr6/xlr0x13w7o9kwfo9?singleDoc# 《Snake Of Bot》"
                            target="_blank">游戏规则 </a>
                        进入一览。
                    </div>
                    <div></div>
                </div>
            </div>
        </div>
    </div>
    <div class="filings">
        <!-- <img src="../assets/images/filings.png" alt="国徽"> -->
        <span style="margin-right: 7vw;"> <a class="filing" href="https://beian.miit.gov.cn/#/Integrated/recordQuery">
                津ICP备2023008864号-1 </a> </span>
    </div>
</template>

<script>
export default {

}
</script>

<style scoped>
div.welcom-text {
    margin-top: 5%;
    height: 16px;
    color: rgb(0, 255, 55);
    font-weight: 600;
    font-size: 32px;
    margin-bottom: 5vh;
}

div.card-color {
    color: white;
    background-color: rgba(0, 0, 0, 0.2);
    margin-top: 5vh;
    margin-bottom: 10vh;
}

div.rules {
    color: white;
    margin: 20vh auto;
    font-size: 5vh;
    margin-left: 5vw;
    background-color: rgba(0, 0, 0, 0.0);
}

div.card-title {
    margin-bottom: 20vh;
}

.text-weit {
    font-size: 18px;
}

div.filings {
    text-align: right;
    color: white;
    /* background-color: rgba(0, 0, 0, 0.3); */
}

.rule {
    color: black;
}

.rule:hover {
    font-size: 110%;
}

.filing {
    color: white;
    text-decoration: none;
    font-size: 18px;
}

.filing:hover {
    text-decoration: underline;
}
</style>