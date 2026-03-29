<template>
  <div class="notice-container">
    <div class="header">
      <h2>通知管理</h2>
      <el-button type="primary" @click="handleAdd">发布通知</el-button>
    </div>

    <el-table :data="sortedNoticeList" style="width: 100%">
      <el-table-column prop="title" label="标题" width="180">
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.title" placement="top">
            <span>{{ scope.row.title.length > 20 ? scope.row.title.substring(0, 20) + '...' : scope.row.title }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="date" label="发布时间" width="180">
        <template slot-scope="scope">
          {{ formatDate(scope.row.date) }}
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容预览">
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.content" placement="top">
            <span>{{ scope.row.content.length > 50 ? scope.row.content.substring(0, 50) + '...' : scope.row.content }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <!-- <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button> -->
          <el-button type="text" class="delete-btn" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%">
      <el-form :model="currentNotice" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="currentNotice.title"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="currentNotice.content" :rows="6"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <div class="image-uploader">
            <el-image 
              v-if="currentNotice.imgurl" 
              :src="currentNotice.imgurl" 
              fit="cover"
              class="preview-image"
              @click="triggerUpload"
            ></el-image>
            <div v-else class="upload-placeholder" @click="triggerUpload">
              <i class="el-icon-plus"></i>
            </div>
            <input 
              type="file" 
              ref="fileInput" 
              style="display: none" 
              accept="image/*"
              @change="handleFileChange"
            >
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSave">{{ isEdit ? '保存' : '发布' }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getNoticeList, updateNotice, deleteNotice, createNotice } from '../../api/notices';
import { upload } from '../../api/upload';

export default {
  name: "notice-list",
  data() {
    return {
      noticeList: [],
      dialogVisible: false,
      isEdit: false,
      currentNotice: {
        id: null,
        title: '',
        content: '',
        imgurl: ''
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑通知' : '发布通知';
    },
    sortedNoticeList() {
      return [...this.noticeList].sort((a, b) => {
        return new Date(b.date) - new Date(a.date);
      });
    }
  },
  created() {
    this.fetchNotices();
  },
  methods: {
    async fetchNotices() {
      try {
        const response = await getNoticeList();
        if (response.code === 200) {
          this.noticeList = response.data;
        }
      } catch (error) {
        this.$message.error('获取通知列表失败');
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    handleAdd() {
      this.isEdit = false;
      this.currentNotice = {
        id: null,
        title: '',
        content: '',
        imgurl: ''
      };
      this.dialogVisible = true;
    },
    handleEdit(notice) {
      this.isEdit = true;
      this.currentNotice = { ...notice };
      this.dialogVisible = true;
    },
    async handleSave() {
      try {
        let response;
        if (this.isEdit) {
          response = await updateNotice(this.currentNotice);
        } else {
          if(this.currentNotice.title === ''){
            this.$message.error('请输入标题')
            return
          }
          response = await createNotice(this.currentNotice);
        }
        
        if (response.code === 200) {
          this.$message.success(this.isEdit ? '保存成功' : '发布成功');
          this.dialogVisible = false;
          this.fetchNotices();
        }
      } catch (error) {
        this.$message.error(this.isEdit ? '保存失败' : '发布失败');
      }
    },
    async handleDelete(notice) {
      try {
        await this.$confirm('确定要删除这条通知吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        const response = await deleteNotice(notice.id);
        if (response.code === 200) {
          this.$message.success('删除成功');
          this.fetchNotices();
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败');
        }
      }
    },
    triggerUpload() {
      this.$refs.fileInput.click();
    },
    async handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        const response = await upload(file);
        if (response.code === 200) {
          this.currentNotice.imgurl = response.data;
        }
      }
    }
  }
}
</script>

<style scoped>
.notice-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.image-uploader {
  width: 200px;
  height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #8c939d;
  font-size: 28px;
}

.delete-btn {
  color: #F56C6C;
}

.delete-btn:hover {
  color: #f78989;
}
</style>