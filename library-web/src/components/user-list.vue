<template>
<div class="table-container">
    <div class="filter-container">
        <el-button type="primary" @click="showAllNotReturned">
            {{ showNotReturnedOnly ? '返回全部' : '查看未归还图书' }}
        </el-button>
    </div>
    <el-table
        :data="filteredList"
        style="width: 100%"
        height="calc(100vh - 70px)"
        border>
        <el-table-column
            prop="username"
            label="用户名"
            min-width="180"
            fixed>
        </el-table-column>
        <el-table-column
            prop="phone"
            label="手机号"
            min-width="180">
        </el-table-column>
        <el-table-column
            prop="name"
            label="书名"
            min-width="200">
        </el-table-column>
        <el-table-column
            prop="lendDate"
            label="借出时间"
            min-width="180">
        </el-table-column>
        <el-table-column
            prop="backDate"
            label="归还时间"
            min-width="180">
        </el-table-column>
        <el-table-column
            label="状态"
            width="100"
            fixed="right">
            <template #default="scope">
                <el-tag :type="scope.row.backDate ? 'success' : 'warning'">
                    {{ scope.row.backDate ? '已归还' : '未归还' }}
                </el-tag>
            </template>
        </el-table-column>
    </el-table>
</div>
</template>

<script>
import moment from "moment";
import {getLendList} from "../../api/book";

export default {
    name: "user-list",
    data(){
        return{
            lendList:[],
            showNotReturnedOnly: false
        }
    },
    computed:{
        dataList(){
            // eslint-disable-next-line vue/no-side-effects-in-computed-properties
            return this.lendList.reverse();
        },
        filteredList() {
            if (this.showNotReturnedOnly) {
                return this.dataList.filter(item => !item.backDate);
            }
            return this.dataList;
        }
    },
    methods: {
        showAllNotReturned() {
            this.showNotReturnedOnly = !this.showNotReturnedOnly;
        }
    },
    mounted() {
        getLendList().then(res=>{
            this.lendList = res.data.map(item => {
                if(item.backDate!=null)
                    return {
                        ...item,
                        // 转换时间字段
                        lendDate: moment(new Date(item.lendDate)).format('YYYY-MM-DD HH:mm:ss'),
                        backDate: moment(new Date(item.backDate)).format('YYYY-MM-DD HH:mm:ss'),
                    };
                else {
                    return{
                        ...item,
                        // 转换时间字段
                        lendDate: moment(new Date(item.lendDate)).format('YYYY-MM-DD HH:mm:ss'),
                    }
                }
            })
        })
    }
}
</script>

<style scoped>
.table-container {
    height: 100vh;
    width: 100%;
    padding: 10px;
    box-sizing: border-box;
    background-color: #f5f7fa;
}

.filter-container {
    margin-bottom: 10px;
    display: flex;
    justify-content: flex-end;
}

* {
    text-align: center;
}

:deep(.el-table) {
    height: 100%;
}

:deep(.el-table__body-wrapper) {
    height: calc(100% - 40px);
}

:deep(.el-tag) {
    font-size: 14px;
    padding: 0 10px;
    height: 28px;
    line-height: 28px;
}

:deep(.el-table__body) {
    width: 100% !important;
}

:deep(.el-table__header) {
    width: 100% !important;
}
</style>