<template>
<div>
    <el-container>
        <el-aside width="320px">
            <el-menu
                @select="handleSelect">
                <el-menu-item v-for="(classification,index) in classifications" :index="classification" :key="index">
                    <span>{{classification.classNumber}}</span>
                    <span slot="title" style="margin-left: 5px">{{classification.className}}</span>
                </el-menu-item>
            </el-menu>

        </el-aside>
        <el-main>
            <el-table
                :data="bookList"
                style="width: 100%"
                height="636">
                <el-table-column
                    prop="name"
                    label="书名"
                    width="180"
                    fixed
                    border>
                </el-table-column>
                <el-table-column
                    prop="author"
                    label="作者"
                    width="150">
                </el-table-column>
                <el-table-column
                    prop="publish"
                    label="出版社"
                    width="140">
                </el-table-column>
                <el-table-column
                    prop="pubDate"
                    label="出版时间"
                    width="100">
                </el-table-column>
                <el-table-column
                    prop="isbn"
                    label="ISBN"
                    width="140">
                </el-table-column>
                <el-table-column
                    prop="allNumber"
                    label="总数量"
                    width="80">
                </el-table-column>
                <el-table-column
                    prop="remainNumber"
                    label="剩余数量"
                    width="80">
                </el-table-column>
                <el-table-column
                    prop="classification"
                    label="图书分类"
                    width="120">
                </el-table-column>
                <el-table-column
                    label="操作"
                    fixed="right"
                >
                    <template slot-scope="scope">
                        <div class="table-button">
                            <el-button @click="handleEdit(scope)" type="text" size="small">编辑</el-button>
<!--                            <el-popconfirm-->
<!--                                title="确定删除吗？"-->
<!--                                icon="none"-->
<!--                                @confirm="saveDel(scope)"-->
<!--                            >-->
<!--                                <el-button slot="reference" type="text" size="small" >删除</el-button>-->
<!--                            </el-popconfirm>-->
                        </div>
                    </template>
                </el-table-column>

            </el-table>
        </el-main>
    </el-container>
    <el-dialog
        :visible.sync="dialogVisible"
        title="编辑图书信息"
    >
        <template>
            <el-form :model="editForm">
                <el-form-item label="书名">
                    <el-input v-model="editForm.name"></el-input>
                </el-form-item>
                <el-form-item label="作者">
                    <el-input v-model="editForm.author"></el-input>
                </el-form-item>
                <el-form-item label="出版社">
                    <el-input v-model="editForm.publish"></el-input>
                </el-form-item>
                <el-form-item label="出版时间">
                    <el-input v-model="editForm.pubDate"></el-input>
                </el-form-item>
                <el-form-item label="ISBN">
                    <el-input v-model="editForm.isbn" disabled></el-input>
                </el-form-item>
                <el-form-item label="总数量">
                    <el-input v-model="editForm.allNumber" disabled></el-input>
                </el-form-item>
                <el-form-item label="剩余数量">
                    <el-input v-model="editForm.remainNumber" disabled></el-input>
                </el-form-item>
                <el-form-item label="图书分类">
                    <el-input v-model="editForm.classification"></el-input>
                </el-form-item>
            </el-form>
        </template>
        <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEdit">保存</el-button>
        </span>
        </template>
    </el-dialog>
</div>
</template>

<script>
import bookClassification from '../assets/bookClassification';
import {deleteBook, getBookInfo, updateBook} from "../../api/book";

export default {
    name: "book-list",
    data(){
        return{
            classifications: bookClassification,
            bookList:[],
            visibleDel:false,
            editForm:{},
            dialogVisible:false,
            activeClassification:{}
        }
    },

    methods:{
        async handleSelect(classification){
            await getBookInfo(classification).then(res=>{
                this.activeClassification = classification;
                this.bookList = res.data;
                console.log(this.bookList)
            })
        },
        handleEdit(scope) {
            let row = scope.row;
            this.editForm = {...row};
            // console.log({...scope})
            this.dialogVisible = true;
        },
        async saveEdit(){
            if(this.editForm.name === ''){
                this.$message.error('请输入书名')
                return
            }
            if(this.editForm.author === ''){
                this.$message.error('请输入作者')
                return
            }
            const firstChar = this.editForm.classification.charAt(0);
            const invalidFirstLetters = ['L', 'M', 'W', 'Y'];
            const isValidClassification = /^[A-Z]/.test(this.editForm.classification) && 
                                       !invalidFirstLetters.includes(firstChar);
            
            if (!isValidClassification) {
                this.$message.error('分类编号首字母必须是大写英文字母（除L、M、W、Y外）');
                return;
            }

            if(this.editForm.classification)
            await updateBook(this.editForm).then(res=>{
                if(res.code === 200){
                    this.handleSelect(this.activeClassification).then(()=>{
                        this.$message.success('修改成功!');
                        this.dialogVisible = false;
                    })
                }
            })
        },
        async saveDel(scope){
            await deleteBook(scope.row.bookId).then(res=>{
                if(res.code === 200){
                    this.handleSelect(this.activeClassification).then(()=>{
                        this.$message.success('删除成功!');
                    })
                }
            })
        }
    },
    created(){
        this.handleSelect({ index: 0, classNumber: " ", className: "全部" });
    }
}
</script>

<style scoped>
.el-aside{
    height: 676px;
}
.el-main{
    .el-table{
        /*height: 636px;*/
    }
    height: 676px;
}
.table-button{
    display: flex;
    justify-content: space-around;
}
</style>