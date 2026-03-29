<template>
    <el-main>
        <div class="container">
            <div class="title">账号登录</div>
            <el-input placeholder="请输入内容" v-model="account">
                <template slot="prepend">账号:</template>
            </el-input>
            <el-input placeholder="请输入内容" type="password" v-model="password">
                <template slot="prepend">密码:</template>
            </el-input>
            <el-row>
<!--                <el-col :span="12"><div class="font">注册账号</div></el-col>-->
<!--                <el-col :span="12"><div class="font">忘记密码</div></el-col>-->
            </el-row>
            <el-button type="primary" size="medium" @click="submit">登录</el-button>
        </div>
    </el-main>
</template>

<script>
import {login} from "../../api/user";

export default {
    // eslint-disable-next-line vue/multi-word-component-names
    name: "login",
    data(){
        return{
            account:'',
            password:''
        }
    },
    methods:{
        async submit(){
            await login(this.account,this.password).then(res=>{
                console.log(res);
                if (res.code === 200){
                    if(res.data.authority != 0){
                        this.$message.error('您没有权限登录!');
                        return;
                    }
                    console.log('登录成功');
                    localStorage.setItem('token',res.data.token);
                    this.$router.push('/home');
                } else {
                    this.$message.error('账号或密码错误!');
                }
            })
        }
    },
    mounted() {
        let token = localStorage.getItem('token');
        if(token){
            this.$router.push('/home');
        }
    }
}
</script>

<style scoped>
.el-main{
    max-width: 1450px;
}
.container{
    position: absolute;
    top: 30%;
    /* margin-top: -248px; */
    right: 160px;
    border: 1px solid #e3e3e3;
    width: 500px;
    padding: 30px;
    background: #fff;
    border-radius: 10px;
    box-shadow: 2px 2px 4px 3px rgba(50, 50, 50, .2);

}
.container .title{
    font-size: 25px;
    font-weight: bold;
    height: 25px;
    line-height: 25px;
    text-align: center;
}
.el-input{
    margin: 10px;
}
.font{
    margin-top: 10px;
    color: #4285f4;
    text-align: center;
}
.el-button{
    width: 300px;
    margin: 20px 70px;
}
</style>