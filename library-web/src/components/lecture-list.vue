<template>
    <div class="lecture-list">
        <div class="header">
            <h1>讲座列表</h1>
            <el-button type="primary" @click="dialogVisible = true">添加讲座</el-button>
        </div>
        
        <el-table :data="sortedLectureList" style="width: 100%" border>
            <el-table-column prop="title" label="讲座标题" min-width="200">
                <template slot-scope="scope">
                    <div class="title-cell">
                        <span>{{ scope.row.title }}</span>
                        <el-tag :type="getStatusType(scope.row)" size="small" class="status-tag">
                            {{ getStatusText(scope.row) }}
                        </el-tag>
                    </div>
                </template>
            </el-table-column>
            
            <el-table-column label="讲座图片" width="120">
                <template slot-scope="scope">
                    <el-image 
                        v-if="scope.row.imgurl"
                        style="width: 100px; height: 60px"
                        :src="scope.row.imgurl"
                        :preview-src-list="[scope.row.imgurl]"
                        fit="cover">
                    </el-image>
                    <span v-else>无图片</span>
                </template>
            </el-table-column>
            
            <el-table-column prop="date" label="开始时间" width="180" sortable>
                <template slot-scope="scope">
                    {{ formatDate(scope.row.date) }}
                </template>
            </el-table-column>
            <el-table-column prop="location" label="地点" width="180"></el-table-column>
            <el-table-column prop="address" label="详细地址" width="180"></el-table-column>
            
            <el-table-column prop="moderator" label="主讲人" width="120"></el-table-column>
            
            <el-table-column label="操作" width="150" fixed="right">
                <template slot-scope="scope">
                    <!-- <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button> -->
                    <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 添加讲座弹窗 -->
        <el-dialog title="添加讲座" :visible.sync="dialogVisible" width="50%" @close="resetForm">
            <el-form :model="lectureForm" :rules="rules" ref="lectureForm" label-width="100px">
                <el-form-item label="讲座标题" prop="title">
                    <el-input v-model="lectureForm.title" placeholder="请输入讲座标题"></el-input>
                </el-form-item>

                <el-form-item label="讲座内容" prop="content">
                    <el-input type="textarea" v-model="lectureForm.content" placeholder="请输入讲座内容"></el-input>
                </el-form-item>

                <el-form-item label="讲座图片" prop="imgurl">
                    <input
                        type="file"
                        accept="image/*"
                        @change="handleFileChange"
                        style="display: none"
                        ref="fileInput">
                    <el-button 
                        size="small" 
                        type="primary"
                        @click="$refs.fileInput.click()">
                        点击上传
                    </el-button>
                    <div class="el-upload__tip">只能上传jpg/png文件</div>
                    <el-image 
                        v-if="lectureForm.imgurl"
                        style="width: 200px; height: 120px; margin-top: 10px"
                        :src="lectureForm.imgurl"
                        fit="cover">
                    </el-image>
                </el-form-item>

                <el-form-item label="开始时间" prop="date">
                    <el-date-picker
                        v-model="lectureForm.date"
                        type="datetime"
                        placeholder="选择日期时间"
                        format="yyyy-MM-dd HH:mm"
                        value-format="yyyy-MM-dd HH:mm"
                        :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>

                <el-form-item label="讲座地点" prop="address">
                    <el-input v-model="lectureForm.address" placeholder="请输入讲座地点"></el-input>
                </el-form-item>

                <el-form-item label="主讲人" prop="moderator">
                    <el-input v-model="lectureForm.moderator" placeholder="请输入主讲人姓名"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getLectureList, deleteLecture, addLecture } from '../../api/lecture'
import { upload } from '../../api/upload'

export default {
    name: 'LectureList',
    data() {
        return {
            lectureList: [],
            dialogVisible: false,
            lectureForm: {
                title: '',
                content: '',
                imgurl: '',
                date: '',
                address: '',
                moderator: ''
            },
            rules: {
                title: [
                    { required: true, message: '请输入讲座标题', trigger: 'blur' },
                    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
                ],
                content: [
                    { required: true, message: '请输入讲座内容', trigger: 'blur' }
                ],
                date: [
                    { required: true, message: '请选择开始时间', trigger: 'change' }
                ],
                address: [
                    { required: true, message: '请输入讲座地点', trigger: 'blur' }
                ],
                moderator: [
                    { required: true, message: '请输入主讲人姓名', trigger: 'blur' }
                ]
            },
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() < Date.now() - 8.64e7; // 不能选择过去的日期
                }
            }
        }
    },
    computed: {
        sortedLectureList() {
            const now = new Date()
            const upcoming = this.lectureList.filter(lecture => {
                const lectureDate = new Date(lecture.date)
                return lectureDate > now
            }).sort((a, b) => new Date(a.date) - new Date(b.date))
            
            const past = this.lectureList.filter(lecture => {
                const lectureDate = new Date(lecture.date)
                return lectureDate <= now
            }).sort((a, b) => new Date(b.date) - new Date(a.date))
            
            return [...upcoming, ...past]
        }
    },
    created() {
        this.fetchLectureList()
    },
    methods: {
        async fetchLectureList() {
            try {
                const res = await getLectureList()
                if (res.code === 200) {
                    this.lectureList = res.data
                }
            } catch (error) {
                console.error('获取讲座列表失败:', error)
            }
        },
        handleAdd() {
            this.$router.push('/lecture/add')
        },
        handleEdit(lecture) {
            this.$router.push(`/lecture/edit/${lecture.id}`)
        },
        async handleDelete(lecture) {
            try {
                await this.$confirm('确定要删除该讲座吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                const res = await deleteLecture(lecture.id)
                if (res.code === 200) {
                    this.$message.success('删除成功')
                    this.fetchLectureList()
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除讲座失败:', error)
                }
            }
        },
        formatDate(date) {
            return new Date(date).toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            })
        },
        getStatusType(lecture) {
            const lectureDate = new Date(lecture.date)
            const now = new Date()
            return lectureDate > now ? 'success' : 'info'
        },
        getStatusText(lecture) {
            const lectureDate = new Date(lecture.date)
            const now = new Date()
            return lectureDate > now ? '未开始' : '已开始'
        },
        resetForm() {
            this.$refs.lectureForm?.resetFields()
            this.lectureForm = {
                title: '',
                content: '',
                imgurl: '',
                date: '',
                address: '',
                moderator: ''
            }
        },
        
        async handleFileChange(event) {
            const file = event.target.files[0]
            if (file) {
                const isImage = file.type.startsWith('image/')
                const isLt2M = true

                if (!isImage) {
                    this.$message.error('只能上传图片文件!')
                    return
                }
                if (!isLt2M) {
                    this.$message.error('图片大小不能超过 2MB!')
                    return
                }

                try {
                    const response = await upload(file)
                    if (response.code === 200) {
                        this.lectureForm.imgurl = response.data
                        this.$message.success('图片上传成功')
                    } else {
                        this.$message.error('图片上传失败')
                    }
                } catch (error) {
                    console.error('图片上传失败:', error)
                    this.$message.error('图片上传失败')
                }
            }
        },
        
        submitForm() {
            this.$refs.lectureForm.validate(async (valid) => {
                if (valid) {
                    try {
                        if(this.lectureForm.title === ''){
                            this.$message.error('请输入讲座标题')
                            return
                        }
                        if(this.lectureForm.content === ''){
                            this.$message.error('请输入讲座内容')
                            return
                        }
                        if(this.lectureForm.address === ''){
                            this.$message.error('请输入讲座地点')
                            return
                        }
                        if(this.lectureForm.moderator === ''){
                            this.$message.error('请输入主讲人')
                            return
                        }
                        if(this.lectureForm.date === ''){
                            this.$message.error('请选择开始时间')
                            return
                        }
                        const res = await addLecture(this.lectureForm)
                        if (res.code === 200) {
                            this.$message.success('添加成功')
                            this.dialogVisible = false
                            this.fetchLectureList()
                        }
                    } catch (error) {
                        console.error('添加讲座失败:', error)
                    }
                }
            })
        }
    }
}
</script>

<style scoped>
.lecture-list {
    padding: 20px;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.title-cell {
    display: flex;
    align-items: center;
    gap: 10px;
}

.status-tag {
    margin-left: 8px;
}

.el-image {
    cursor: pointer;
}

.upload-demo {
    margin-bottom: 10px;
}

.el-upload__tip {
    color: #909399;
    font-size: 12px;
    margin-top: 5px;
}
</style>