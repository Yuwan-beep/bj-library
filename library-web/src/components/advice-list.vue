<template>
  <div>
    <el-table :data="adviceList" style="width: 100%">
      <el-table-column prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column prop="title" label="标题" width="150"></el-table-column>
      <el-table-column prop="content" label="内容">
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.content" placement="top">
            <span>{{ scope.row.content.length > 20 ? scope.row.content.substring(0, 20) + '...' : scope.row.content }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="date" label="日期" width="180">
        <template slot-scope="scope">
          {{ formatDate(scope.row.date) }}
        </template>
      </el-table-column>
      <el-table-column prop="isReply" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isReply === 1 ? 'success' : 'warning'">
            {{ scope.row.isReply === 1 ? '已解决' : '未解决' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleReply(scope.row)">回复</el-button>
          <el-button 
            size="mini" 
            type="success" 
            @click="handleResolve(scope.row)"
            :disabled="scope.row.isReply === 1">
            标记解决
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 回复对话框 -->
    <el-dialog title="回复留言" :visible.sync="replyDialogVisible" width="50%">
      <div v-if="currentAdvice">
        <h3>留言内容：{{ currentAdvice.content }}</h3>
        <div v-if="currentAdvice.imgurl" style="margin: 10px 0;">
          <img :src="currentAdvice.imgurl" style="max-width: 200px; max-height: 200px;">
        </div>
        <h4>历史回复：</h4>
        <div v-for="reply in replyList" :key="reply.id" style="margin: 10px 0; padding: 10px; background: #f5f7fa;">
          <p><strong>{{ reply.replyPerson }}：</strong>{{ reply.content }}</p>
        </div>
        <el-input
          type="textarea"
          :rows="3"
          placeholder="请输入回复内容"
          v-model="requestComment.content">
        </el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="replyDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReply">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAdviceList, replyAdvice, resolveAdvice, getCommentList } from '../../api/advice';

export default {
  name: "advice-list",
  data() {
    return {
      adviceList: [],
      replyDialogVisible: false,
      currentAdvice: null,
      replyList: [],
      requestComment: {
        replyPerson: '管理员',
        adviceId: '',
        content: ''
      }
    }
  },
  methods: {
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
    async handleReply(row) {
      this.currentAdvice = row;
      this.replyDialogVisible = true;
      this.requestComment.content = '';
      // 获取历史回复
      try {
        const response = await getCommentList(row.id);
        if (response.code === 200) {
          this.replyList = response.data;
        }
      } catch (error) {
        this.$message.error('获取回复历史失败');
      }
    },
    async submitReply() {
      if (!this.requestComment.content.trim()) {
        this.$message.warning('请输入回复内容');
        return;
      }
      try {
        this.requestComment.adviceId = this.currentAdvice.id;
        const response = await replyAdvice(this.requestComment);
        if (response.code === 200) {
          this.$message.success('回复成功');
          this.replyDialogVisible = false;
          this.fetchAdviceList();
          this.requestComment.content = '';
        } else {
          this.$message.error('回复失败');
        }
      } catch (error) {
        this.$message.error('回复失败');
      }
    },
    async handleResolve(row) {
      try {
        const response = await resolveAdvice(row.id);
        if (response.code === 200) {
          this.$message.success('已标记为已解决');
          this.fetchAdviceList();
        }
      } catch (error) {
        this.$message.error('操作失败');
      }
    },
    async fetchAdviceList() {
      try {
        const response = await getAdviceList();
        if (response.code === 200) {
          // 对列表进行排序，未解决的排在前面
          this.adviceList = response.data.sort((a, b) => {
            // 如果a未解决，b已解决，a排在前面
            if (a.isReply === 0 && b.isReply === 1) return -1;
            // 如果a已解决，b未解决，b排在前面
            if (a.isReply === 1 && b.isReply === 0) return 1;
            // 如果状态相同，按日期降序排列
            return new Date(b.date) - new Date(a.date);
          });
        }
      } catch (error) {
        this.$message.error('获取留言列表失败');
      }
    }
  },
  created() {
    this.fetchAdviceList();
  }
}
</script>

<style scoped>
.el-table {
  margin-top: 20px;
}
</style>