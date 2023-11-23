<template>
    <CardtoAll>
        <div>
            <table class="table table-hover" style="text-align: center;">
                <thead>
                    <tr>
                        <th>玩家 A</th>
                        <th>玩家 B</th>
                        <th>对战结果</th>
                        <th>对战时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="record in records" :key="record.record.id">
                        <td>
                            <img :src="record.a_photo" alt="玩家A的头像" class="record-user-photo">
                            &nbsp;
                            <span class="record-user-username">{{ record.a_username }}</span>
                        </td>
                        <td>
                            <img :src="record.b_photo" alt="玩家B的头像" class="record-user-photo">
                            &nbsp;
                            <span class="record-user-username">{{ record.b_username }}</span>
                        </td>
                        <td>{{ record.result }}</td>
                        <td>{{ record.record.createtime }}</td>
                        <td>
                            <button @click="open_record_content(record.record.id)" type="button"
                                class="btn btn-outline-dark">查看对局回放</button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <nav aria-label="...">
                <ul class="pagination justify-content-end" style="margin-right: 5vw;">
                    <li class="page-item disabled">
                        <a class="page-link" @click="click_page(-2)" href="#">上一页</a>
                    </li>
                    <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number">
                        <a class="page-link" @click="click_page(page.number)" href="#">{{ page.number }}</a>
                    </li>
                    <li class="page-item">
                        <a class="page-link" @click="click_page(-1)" href="#">下一页</a>
                    </li>
                </ul>
            </nav>
        </div>
    </CardtoAll>
</template>
<script>
import CardtoAll from "@/components/CardtoAll.vue"
import { useStore } from "vuex";
import { ref } from "vue";
import $ from "jquery"
import router from "@/router/index";

export default {
    components: {
        CardtoAll
    },
    setup() {
        const store = useStore();
        let records = ref([]);
        let records_count = 0;
        let current_page = 1;
        let pages = ref([]);

        const click_page = page => {
            if (page === -2) page = current_page - 1;
            if (page === -1) page = current_page + 1;
            let max_pages = parseInt(Math.ceil(records_count / 10));
            if (page >= 1 && page <= max_pages) {
                puul_page(page);
            }
        }

        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(records_count / 10));
            let new_pages = [];
            for (let i = current_page - 2; i <= current_page + 2; i++) {
                if (i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: current_page === i ? "active" : "",
                    })
                }
            }
            pages.value = new_pages;
        }

        const puul_page = page => {
            current_page = page;
            $.ajax({
                url: "http://127.0.0.1:3000/record/getlist/",
                data: {
                    page,
                },
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    records.value = resp.records;
                    records_count = resp.records_count;
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                },
            })
        }
        puul_page(current_page);

        const StringTo2D = (map) => {
            let g = [];
            for (let i = 0, k = 0; i < 13; i++) {
                let line = [];
                for (let j = 0; j < 14; j++, k++) {
                    if (map[k] === '0') line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }

        const open_record_content = recordId => {
            for (const record of records.value) {
                if (record.record.id === recordId) {
                    store.commit("updateIsRecord", true);
                    store.commit("updateGame", {
                        gamemap: StringTo2D(record.record.map),
                        a_id: record.record.aid,
                        a_sx: record.record.asx,
                        a_sy: record.record.asy,
                        b_id: record.record.bid,
                        b_sx: record.record.bsx,
                        b_sy: record.record.bsy,

                    })
                    store.commit("updateSteps", {
                        a_steps: record.record.asteps,
                        b_steps: record.record.bsteps,
                    })
                    store.commit("updateRecordLoser", record.record.loser);
                    router.push({
                        name: "record_content",
                        params: {
                            recordId
                        }
                    })
                    break;
                }
            }
        }

        return {
            records,
            open_record_content,
            pages,
            click_page,
        }
    }
}


</script>
<style scoped>
.record-user-photo {
    width: 5vh;
    border-radius: 50%;
}
</style>