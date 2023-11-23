<template>
    <CardtoAll>
        <div>
            <table class="table table-hover" style="text-align: center;">
                <thead>
                    <tr>
                        <th>玩家</th>
                        <th>对局场次</th>
                        <th>天梯积分</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="user in users" :key="user.id">
                        <td>
                            <img :src="user.photo" alt="头像" class="ranklist-user-photo" style="text-align: left;">
                            &nbsp;
                            <span class="ranklist-user-username">{{ user.username }}</span>
                        </td>
                        <td>{{ user.matchcount }}</td>
                        <td>{{ user.rating }}</td>
                    </tr>
                </tbody>
            </table>
            <nav aria-label="...">
                <ul class="pagination justify-content-end" style="margin-right: 5vw;">
                    <li class="page-item">
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

export default {
    components: {
        CardtoAll
    },
    setup() {
        const store = useStore();
        let users = ref([]);
        let users_count = 0;
        let current_page = 1;
        let pages = ref([]);

        const click_page = page => {
            if (page === -2) page = current_page - 1;
            if (page === -1) page = current_page + 1;
            let max_pages = parseInt(Math.ceil(users_count / 9));
            if (page >= 1 && page <= max_pages) {
                puul_page(page);
            }
        }

        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(users_count / 9));
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
                url: "http://127.0.0.1:3000/ranklist/getlist/",
                data: {
                    page,
                },
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    users.value = resp.users;
                    users_count = resp.users_count;
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                },
            })
        }

        puul_page(current_page);

        return {
            users,
            pages,
            click_page,
        }
    }
}


</script>
<style scoped>
.ranklist-user-photo {
    width: 6vh;
    border-radius: 50%;
}
</style>