
export default {
    state: {
        status: "matching",//"matching匹配界面，playing对战界面"
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,

    },
    getters: {

    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updaGamemap(state, gamemap) {
            state.gamemap = gamemap;
        },
    },
    actions: {

    },
    modules: {

    },
}