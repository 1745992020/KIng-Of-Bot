<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 7%;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="头像" style="width: 100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 2%;">
                    <div class="card-header">
                        <span style="font-size: 150%;">我的Bot</span>
                        <!-- Button trigger modal -->
                        <button type="button" class="btn btn-outline-dark float-end" data-bs-toggle="modal"
                            data-bs-target="#add_bot_btn">添加Bot</button>
                    </div>

                    <!-- Modal -->
                    <div class="modal fade" id="add_bot_btn" tabindex="-1">
                        <div class="modal-dialog modal-xl">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h1 class="modal-title fs-5">添加Bot</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label for="name" class="form-label">名字</label>
                                        <input v-model="botadd.name" type="text" class="form-control" id="name"
                                            placeholder="请输入Bot名字">
                                    </div>
                                    <div class="mb-3">
                                        <label for="description" class="form-label">简介</label>
                                        <textarea v-model="botadd.description" type="text" class="form-control"
                                            id="description" rows="2" placeholder="请输入简介"></textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="content" class="form-label">代码</label>
                                        <VAceEditor v-model:value="botadd.content" @init="editorInit" lang="c_cpp"
                                            theme="textmate" style="height: 300px" :options="{
                                                enableBasicAutocompletion: true, //启用基本自动完成
                                                enableSnippets: true, // 启用代码段
                                                enableLiveAutocompletion: true, // 启用实时自动完成
                                                fontSize: 18, //设置字号
                                                tabSize: 4, // 标签大小
                                                showPrintMargin: false, //去除编辑器里的竖线
                                                highlightActiveLine: true,
                                            }" />
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <div class="error_massage">{{ botadd.error_massage }}</div>
                                    <button type="button" class="btn btn-primary" @click="add_bot">保存</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover" style="text-align: center;">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.name }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <!-- Button trigger modal -->
                                        <button type="button" class="btn btn-outline-info" data-bs-toggle="modal"
                                            :data-bs-target="'#update_bot_btn' + bot.id"
                                            style="margin-right: 5%;">修改</button>

                                        <!-- Modal -->
                                        <div class="modal fade" :id="'update_bot_btn' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h1 class="modal-title fs-5">{{ bot.name }}</h1>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                            aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body" style="text-align: initial;;">
                                                        <div class="mb-3">
                                                            <label for="name" class="form-label">名字</label>
                                                            <input v-model="bot.name" type="text" class="form-control"
                                                                id="name" placeholder="请输入Bot名字">
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="description" class="form-label">简介</label>
                                                            <textarea v-model="bot.description" type="text"
                                                                class="form-control" id="description" rows="2"
                                                                placeholder="请输入简介"></textarea>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="content" class="form-label">代码</label>
                                                            <VAceEditor v-model:value="bot.content" @init="editorInit"
                                                                lang="c_cpp" theme="textmate" style="height: 300px"
                                                                :options="{
                                                                    enableBasicAutocompletion: true, //启用基本自动完成
                                                                    enableSnippets: true, // 启用代码段
                                                                    enableLiveAutocompletion: true, // 启用实时自动完成
                                                                    fontSize: 18, //设置字号
                                                                    tabSize: 4, // 标签大小
                                                                    showPrintMargin: false, //去除编辑器里的竖线
                                                                    highlightActiveLine: true,
                                                                }" />
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <div class="error_massage">{{ bot.error_massage }}</div>
                                                        <button type="button" class="btn btn-primary"
                                                            @click="update_bot(bot)">修改保存</button>
                                                        <button type="button" class="btn btn-secondary"
                                                            data-bs-dismiss="modal">取消</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <button type="button" class="btn btn-outline-danger"
                                            @click="remove_bot(bot)">删除</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { ref, reactive } from 'vue';
import $ from "jquery";
import { useStore } from 'vuex';
import { Modal } from 'bootstrap/dist/js/bootstrap';
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';//代码高亮问题(4个)



export default {
    components: {
        VAceEditor,
    },
    setup() {
        ace.config.set(//ace编辑器配置
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" +
            require("ace-builds").version +
            "/src-noconflict/")
        const store = useStore();
        let bots = ref([]);
        let botadd = reactive({
            name: "",
            description: "",
            content: "",
            error_massage: "",
        })

        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getList/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }
        refresh_bots();
        const add_bot = () => {
            botadd.error_massage = "";
            console.log(botadd);
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/add/",
                type: "post",
                data: {
                    name: botadd.name,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_massage === "success") {
                        botadd.name = "";
                        botadd.description = "";
                        botadd.content = "";
                        Modal.getInstance("#add_bot_btn").hide();//关闭框
                        refresh_bots();
                    }
                    else {
                        botadd.error_massage = resp.error_massage;
                    }
                },
            })
        }
        const remove_bot = (bot) => {
            var choice = confirm("删除将永久删除此Bot");
            if (choice) {
                confirmedremove_bot(bot);
            }
        }
        const confirmedremove_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_massage === "success") {
                        refresh_bots();
                    }
                    else {
                        alert(resp.error_massage);
                    }
                }
            })
        };
        const update_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    name: bot.name,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_massage === "success") {
                        Modal.getInstance("#update_bot_btn" + bot.id).hide();
                        bot.error_massage = "";
                        refresh_bots();
                    }
                    else {
                        bot.error_massage = resp.error_massage;
                    }
                }
            })
        }
        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            confirmedremove_bot,
        }
    },

}


</script>
<style scoped>
.error_massage {
    color: red;
}
</style>