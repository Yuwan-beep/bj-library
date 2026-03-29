<template>
    <div class="account-management">
        <el-card>
            <div slot="header">
                <h1>账号管理</h1>
            </div>
            
            <!-- 管理员账号管理 -->
            <el-card class="admin-section">
                <div slot="header">
                    <span>管理员账号</span>
                    <el-button type="primary" size="small" style="float: right" @click="showAddAdminModal = true">
                        新建管理员
                    </el-button>
                </div>
                
                <el-input
                    v-model="adminSearch"
                    placeholder="搜索管理员用户名"
                    prefix-icon="el-icon-search"
                    clearable
                    style="width: 200px; margin-bottom: 15px"
                ></el-input>
                
                <el-table :data="displayAdmins" border style="width: 100%">
                    <el-table-column prop="username" label="用户名"></el-table-column>
                    <el-table-column label="权限" width="200">
                        <template slot-scope="scope">
                            <el-select v-model="scope.row.authority" @change="updateAdminAuthority(scope.row)">
                                <el-option :value="0" label="超级管理员"></el-option>
                                <el-option :value="1" label="普通管理员"></el-option>
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150">
                        <template slot-scope="scope">
                            <el-button type="text" @click="openChangePasswordModal(scope.row, 'admin')">修改密码</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="table-footer" v-if="filteredAdmins.length > 10">
                    <el-button type="text" @click="toggleAdminExpand">
                        {{ isAdminExpanded ? '收起' : '展开更多' }}
                        <i :class="isAdminExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                    </el-button>
                </div>
            </el-card>

            <!-- 用户账号管理 -->
            <el-card class="user-section">
                <div slot="header">
                    <span>用户账号</span>
                </div>
                
                <el-input
                    v-model="userSearch"
                    placeholder="搜索用户手机号"
                    prefix-icon="el-icon-search"
                    clearable
                    style="width: 200px; margin-bottom: 15px"
                ></el-input>
                
                <el-table :data="displayUsers" border style="width: 100%">
                    <el-table-column prop="username" label="用户名"></el-table-column>
                    <el-table-column prop="phone" label="手机号"></el-table-column>
                    <el-table-column label="状态" width="100">
                        <template slot-scope="scope">
                            <el-tag :type="scope.row.ban === 0 ? 'success' : 'danger'">
                                {{ scope.row.ban === 0 ? '正常' : '已禁用' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="250">
                        <template slot-scope="scope">
                            <el-button type="text" @click="openChangePasswordModal(scope.row, 'user')">修改密码</el-button>
                            <el-button type="text" @click="changeUserPhone(scope.row)">更换手机号</el-button>
                            <!-- <el-button 
                                type="text" 
                                :class="scope.row.ban === 0 ? 'el-button--danger' : 'el-button--success'"
                                @click="toggleUserBan(scope.row)"
                            >
                                {{ scope.row.ban === 0 ? '禁用' : '启用' }}
                            </el-button> -->
                        </template>
                    </el-table-column>
                </el-table>
                <div class="table-footer" v-if="filteredUsers.length > 10">
                    <el-button type="text" @click="toggleUserExpand">
                        {{ isUserExpanded ? '收起' : '展开更多' }}
                        <i :class="isUserExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                    </el-button>
                </div>
            </el-card>
        </el-card>

        <!-- 新建管理员对话框 -->
        <el-dialog
            title="新建管理员"
            :visible.sync="showAddAdminModal"
            width="30%"
        >
            <el-form :model="newAdmin" label-width="80px">
                <el-form-item label="用户名">
                    <el-input v-model="newAdmin.username"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="newAdmin.password" type="password"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="showAddAdminModal = false">取 消</el-button>
                <el-button type="primary" @click="createAdmin">确 定</el-button>
            </span>
        </el-dialog>

        <!-- 更换手机号对话框 -->
        <el-dialog
            title="更换手机号"
            :visible.sync="showChangePhoneModal"
            width="30%"
        >
            <el-form :model="phoneForm" label-width="80px">
                <el-form-item label="新手机号">
                    <el-input v-model="phoneForm.newPhone"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="showChangePhoneModal = false">取 消</el-button>
                <el-button type="primary" @click="confirmChangePhone">确 定</el-button>
            </span>
        </el-dialog>

        <!-- 修改密码对话框 -->
        <el-dialog
            :title="passwordForm.type === 'admin' ? '修改管理员密码' : '修改用户密码'"
            :visible.sync="showChangePasswordModal"
            width="30%"
        >
            <el-form :model="passwordForm" label-width="80px">
                <el-form-item label="新密码">
                    <el-input v-model="passwordForm.newPassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="确认密码">
                    <el-input v-model="passwordForm.confirmPassword" type="password"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="showChangePasswordModal = false">取 消</el-button>
                <el-button type="primary" @click="confirmChangePassword">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { getAdmins, getUsers, createAdmin, updateAdmin, updateUser, updateAuthority, updatePhone } from '../../api/account'
export default {
    name: 'account-list',
    data() {
        return {
            admins: [],
            users: [],
            adminSearch: '',
            userSearch: '',
            showAddAdminModal: false,
            showChangePhoneModal: false,
            showChangePasswordModal: false,
            isAdminExpanded: false,
            isUserExpanded: false,
            newAdmin: {
                username: '',
                password: '',
                authority: 1
            },
            phoneForm: {
                userId: null,
                newPhone: ''
            },
            passwordForm: {
                id: null,
                type: '', // 'admin' 或 'user'
                newPassword: '',
                confirmPassword: ''
            }
        }
    },
    computed: {
        filteredAdmins() {
            if (!this.adminSearch) {
                return this.admins;
            }
            const searchText = this.adminSearch.toLowerCase();
            return this.admins.filter(admin => 
                admin.username && admin.username.toLowerCase().includes(searchText)
            );
        },
        filteredUsers() {
            if (!this.userSearch) {
                return this.users;
            }
            const searchText = this.userSearch.trim();
            return this.users.filter(user => 
                user.phone && user.phone.includes(searchText)
            );
        },
        displayAdmins() {
            return this.isAdminExpanded ? this.filteredAdmins : this.filteredAdmins.slice(0, 10);
        },
        displayUsers() {
            return this.isUserExpanded ? this.filteredUsers : this.filteredUsers.slice(0, 10);
        }
    },
    methods: {
        toggleAdminExpand() {
            this.isAdminExpanded = !this.isAdminExpanded;
        },
        toggleUserExpand() {
            this.isUserExpanded = !this.isUserExpanded;
        },
        updateAdminAuthority(admin) {
            // 禁止对admin账号进行权限修改
            if (admin.username === 'admin') {
                this.$message.warning('禁止修改超级管理员权限');
                admin.authority = admin.originalAuthority;
                return;
            }
            
            // 如果选择的权限与当前权限相同，不执行任何操作
            if (admin.authority === admin.originalAuthority) {
                return;
            }
            
            this.$confirm('确认修改该管理员的权限吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                updateAuthority(admin.authority,admin.id).then(res => { 
                    if(res.code === 200){
                        admin.originalAuthority = admin.authority;
                        this.$message.success('权限修改成功');
                    } else {
                        this.$message.error(res.message)
                    }
                })
            }).catch(() => {
                // 取消时恢复原来的权限
                admin.authority = admin.originalAuthority;
                this.$message.info('已取消修改');
            });
        },
        openChangePasswordModal(row, type) {
            // 禁止对admin账号进行密码修改
            if (type === 'admin' && row.username === 'admin') {
                this.$message.warning('禁止修改admin管理员密码');
                return;
            }
            
            this.passwordForm = {
                id: row.id,
                username: row.username,
                authority: row.authority,
                phone: row.phone,
                ban: row.ban,
                type: type,
                newPassword: '',
                confirmPassword: ''
            };
            if(type === 'user'){
                this.passwordForm.id = row.readerId
            }
            this.showChangePasswordModal = true;
        },
        confirmChangePassword() {
            if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
                this.$message.error('两次输入的密码不一致');
                return;
            }
            if (this.passwordForm.newPassword.length < 6) {
                this.$message.error('密码长度不能小于6位');
                return;
            }
            if(this.passwordForm.type === 'admin'){
                let admin = {
                    id: this.passwordForm.id,
                    username: this.passwordForm.username,
                    password: this.passwordForm.newPassword,
                    authority: this.passwordForm.authority
                }
                updateAdmin(admin).then(res => {
                    if(res.code === 200){
                        this.$message.success('密码修改成功')
                        this.showChangePasswordModal = false
                    } else {
                        this.$message.error(res.message)
                    }
                })
            } else {
                let user = {
                    readerId: this.passwordForm.id,
                    username: this.passwordForm.username,
                    password: this.passwordForm.newPassword,
                    phone: this.passwordForm.phone,
                    ban: this.passwordForm.ban
                }
                updateUser(user).then(res => {
                    if(res.code === 200){
                        this.$message.success('密码修改成功')
                        this.showChangePasswordModal = false
                    } else {
                        this.$message.error(res.message)
                    }
                })
            }
        },
        resetUserPassword(user) {
            console.log(user);
            this.$confirm('确认重置该用户的密码吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实现重置用户密码逻辑
                this.$message.success('密码重置成功')
            }).catch(() => {
                this.$message.info('已取消重置')
            })
        },
        changeUserPhone(user) {
            this.phoneForm.userId = user.readerId
            this.phoneForm.newPhone = ''
            this.showChangePhoneModal = true
        },
        confirmChangePhone() {
            const phoneReg = /^1[3-9]\d{9}$/;
            if (!phoneReg.test(this.phoneForm.newPhone)) {
                this.$message.error('请输入正确的手机号')
                return
            }
            
            // 实现更换手机号逻辑
            updatePhone(this.phoneForm.newPhone,this.phoneForm.userId).then(res => {
                if(res.code === 200){
                    this.$message.success('手机号更换成功')
                    this.showChangePhoneModal = false
                    this.getUsers()
                } else {
                    this.$message.error(res.message)
                }
            })
        },
        toggleUserBan(user) {
            const action = user.ban === 0 ? '禁用' : '启用'
            this.$confirm(`确认${action}该用户吗？`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实现切换用户禁用状态逻辑
                this.$message.success(`${action}成功`)
            }).catch(() => {
                this.$message.info('已取消操作')
            })
        },
        createAdmin() {
            if(this.newAdmin.username === '' || this.newAdmin.password === ''){
                this.$message.error('请输入完整信息')
                return
            }
            if(this.newAdmin.password.length < 6){
                this.$message.error('密码长度不能小于6位')
                return
            }
            createAdmin(this.newAdmin).then(res => {
                if(res.code === 200){
                    this.$message.success('管理员创建成功')
                    this.showAddAdminModal = false
                    this.getAdmins()
                } else {
                    this.$message.error(res.message)
                }
            })
        },
        getAdmins() {
            getAdmins().then(res => {
                if(res.code === 200){
                    // 为每个管理员添加原始权限记录
                    this.admins = res.data.map(admin => ({
                        ...admin,
                        originalAuthority: admin.authority
                    }));
                } else {
                    this.$message.error(res.message || '获取管理员列表失败');
                }
            }).catch(error => {
                console.error('获取管理员列表失败:', error);
                this.$message.error('获取管理员列表失败，请检查网络连接');
            });
        },
        getUsers() {
            getUsers().then(res => {
                if(res.code === 200){
                    this.users = res.data;
                } else {
                    this.$message.error(res.message || '获取用户列表失败');
                }
            }).catch(error => {
                console.error('获取用户列表失败:', error);
                this.$message.error('获取用户列表失败，请检查网络连接');
            });
        },
    },
    mounted() {
        // 初始化时获取管理员和用户列表
        this.getAdmins();
        this.getUsers();
    }
}
</script>

<style scoped>
.account-management {
    padding: 20px;
}

.admin-section, .user-section {
    margin-bottom: 20px;
}

.el-card {
    margin-bottom: 20px;
}

.el-card__header {
    padding: 15px 20px;
}

.el-card__header h1 {
    margin: 0;
    font-size: 20px;
}

.el-table {
    margin-top: 15px;
}

.table-footer {
    text-align: center;
    margin-top: 10px;
    padding: 10px 0;
}

.table-footer .el-button {
    font-size: 14px;
}

.table-footer i {
    margin-left: 5px;
}
</style>
