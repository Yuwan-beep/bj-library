<template>
    <div class="hello">
        <div class="header">
            <span>北江中学</span>
            <span>|</span>
            <span>图书馆管理系统</span>
        </div>
        <Login/>
    </div>
</template>

<script>
import Login from "@/components/login.vue";

export default {
    name: "indexPage",
    data() {
        return {}
    },
    components: {
        Login
    },
    methods: {}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.hello {
    min-height: 700px;
    min-width: 1450px;
}

.header {
    text-align: left;
    height: 70px;
    background: #2487c8;
    padding: 10px;

    img {
        width: 50px;
        height: 50px;
    }

    span {
        font-size: 27px;
        line-height: 50px;
        height: 50px;
        color: #fff;
        vertical-align: 15px;
        margin-left: 15px;
    }
}

.hello {
    width: 100%;
    height: 100%;
    background: url("../assets/background.png");
    background-size: cover;
}

.container {
    position: absolute;
    top: 50%;
    margin-top: -248px;
    right: 160px;
    border: 1px solid #e3e3e3;
    width: 500px;
    padding: 30px;
    background: #fff;
    border-radius: 10px;
    box-shadow: 2px 2px 4px 3px rgba(50, 50, 50, .2);
}

.container .title {
    font-size: 25px;
    font-weight: bold;
    height: 25px;
    line-height: 25px;
    text-align: center;
}

.el-input {
    margin: 10px;
}

.font {
    margin-top: 10px;
    color: #4285f4;
    text-align: center;
}

.el-button {
    width: 300px;
    margin: 20px 70px;
}
</style>
