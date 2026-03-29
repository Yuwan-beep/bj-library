<template>
  <div class="page">
      <div class="header">
          <el-row class="banner">
              <el-col :span="8"><div class="banner-left">
                  <span>北江中学</span>
                  <span>|</span>
                  <span>图书馆管理系统</span>
              </div></el-col>
              <el-col :span="15">
                  <div class="banner-right">
                    <span><router-link to="/home/">首页</router-link></span>
                    <span>|</span>
                    <span><router-link to="/home/book">图书管理</router-link></span>
                    <span>|</span>
                    <span><router-link to="/home/users">借阅记录</router-link></span>
                    <span>|</span>
                    <span><router-link to="/home/notices">通知管理</router-link></span>
                    <span>|</span>
                    <span><router-link to="/home/advices">留言管理</router-link></span>
                    <span>|</span>
                    <span><router-link to="/home/lectures">讲座信息</router-link></span>
                    <span>|</span>
                    <span><router-link to="/home/account">账号管理</router-link></span>
                    <span>|</span>
                    <span><router-link to="/home/ai">询问AI</router-link></span>
                  </div>
              </el-col>
              <el-col :span="1">
                  <div class="banner-right">
                    <span @click="logout" class="logout">登出</span>
                  </div>
              </el-col>
          </el-row>
      </div>
      <RouterView/>
  </div>
</template>

<script>
export default {
    name: "home-page",
    methods: {
        logout() {
            localStorage.removeItem('token');
            this.$router.push('/');
        }
    }
}
</script>

<style scoped>
a{
    color: #fff;
    text-decoration: none;
}
.page{
    min-width: 1450px;
}
*{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}
.header{
    text-align: left;
    height: 70px;
    background: #2487c8;
    padding: 10px;
    img{
        width: 50px;
        height: 50px;
    }
    .el-row span{
        font-size: 27px;
        line-height: 50px;
        height: 50px;
        color: #fff;
        vertical-align: 15px;
        margin-left: 15px;
    }
    .banner-right span{
        font-size: 20px;
    }
    .logout{
        cursor: pointer;
    }
}
</style>