<template>
    <CardtoAll>
        <div>
            <table class="table table-hover">
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
                            <button type="button" class="btn btn-outline-dark">查看对局回放</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </CardtoAll>
</template>
<script>
import CardtoAll from "@/components/CardtoAll.vue"
import { useStore } from "vuex";
import { ref } from "vue";
import $ from "jquery"

export default {
    components: {
        CardtoAll
    },
    setup() {
        const store = useStore();
        let records = ref([]);
        let records_count = 0;
        let current_page = 1;

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
                },
                error(resp) {
                    console.log(resp);
                },
            })
        }
        puul_page(current_page);
        return {
            records,
            records_count,
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