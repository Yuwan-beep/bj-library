import Vue from 'vue'   //引入Vue
import vueRouter from 'vue-router'  //引入vue-router
import homePage from "@/views/home-page.vue";
import Login from "@/views/login-page.vue";
import bookList from "@/components/book-list.vue";
import adviceList from "@/components/advice-list.vue";
import userList from "@/components/user-list.vue";
import noticeList from "@/components/notice-list.vue";
import lectureList from "@/components/lecture-list.vue";
import accountList from "@/components/account-list.vue";
import aiChat from "@/components/ai-chat.vue";
import indexPage from '@/components/index-page.vue';
Vue.use(vueRouter)  //Vue全局使用Router
export default new vueRouter({
    mode: 'history',
    routes: [              //配置路由，这里是个数组
        {                    //每一个链接都是一个对象
            path: '/',         //链接路径
            name: 'login',     //路由名称，
            component: Login   //对应的组件模板
        },
        {                    //每一个链接都是一个对象
            path: '/home',         //链接路径
            name: 'home-page',     //路由名称，
            component: homePage,
            children: [
                {
                    path: '/', // 默认子路由
                    component: indexPage
                },
                {
                    path: 'book',
                    component: bookList
                },
                {
                    path: 'users',
                    component: userList
                },
                {
                    path: 'notices',
                    component: noticeList
                },
                {
                    path: 'advices',
                    component: adviceList
                },
                {
                    path: 'lectures',
                    component: lectureList
                },
                {
                    path: 'account',
                    component: accountList
                },
                {
                    path: 'ai',
                    component: aiChat
                }
            ]//对应的组件模板
        }
]
})