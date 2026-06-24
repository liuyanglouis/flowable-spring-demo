// Vue 3 主应用文件
const { createApp, ref, computed, onMounted, reactive } = Vue;
const { ElMessage, ElLoading } = ElementPlus;

// 创建Vue应用
const app = createApp({
    setup() {
        // 响应式数据
        const state = reactive({
            // 表单数据
            formData: {
                applicant: '',
                department: '技术部',
                leaveType: '年假',
                startDate: null,
                endDate: null,
                duration: 0,
                reason: ''
            },

            // 状态数据
            loading: false,
            submitting: false,
            showSuccess: false,
            errorMessage: '',

            // 统计信息
            stats: {
                totalApplications: 0,
                pendingApplications: 0,
                approvedApplications: 0,
                rejectedApplications: 0
            },

            // 最近流程列表
            recentProcesses: [],

            // 表单验证
            formErrors: {
                applicant: false,
                startDate: false,
                endDate: false,
                reason: false
            }
        });

        // 计算属性
        const isValidForm = computed(() => {
            return state.formData.applicant.trim() !== '' &&
                   state.formData.startDate &&
                   state.formData.endDate &&
                   state.formData.reason.trim() !== '';
        });

        const calculatedDuration = computed(() => {
            if (!state.formData.startDate || !state.formData.endDate) {
                return 0;
            }

            const start = dayjs(state.formData.startDate);
            const end = dayjs(state.formData.endDate);

            // 计算工作日天数（简化版）
            const diff = end.diff(start, 'day') + 1;
            return Math.max(0, diff);
        });

        // 方法
        const validateForm = () => {
            state.formErrors.applicant = !state.formData.applicant.trim();
            state.formErrors.startDate = !state.formData.startDate;
            state.formErrors.endDate = !state.formData.endDate;
            state.formErrors.reason = !state.formData.reason.trim();

            return !state.formErrors.applicant &&
                   !state.formErrors.startDate &&
                   !state.formErrors.endDate &&
                   !state.formErrors.reason;
        };

        const resetForm = () => {
            state.formData = {
                applicant: '',
                department: '技术部',
                leaveType: '年假',
                startDate: null,
                endDate: null,
                duration: 0,
                reason: ''
            };
            Object.keys(state.formErrors).forEach(key => {
                state.formErrors[key] = false;
            });
        };

        const submitForm = async () => {
            if (!validateForm()) {
                ElMessage.error('请填写完整的申请信息');
                return;
            }

            // 更新时长
            state.formData.duration = calculatedDuration.value;

            try {
                state.submitting = true;
                state.errorMessage = '';

                // 创建请假申请
                const applicationResponse = await axios.post('/api/leave', state.formData);
                const application = applicationResponse.data;

                ElMessage.success('请假申请已提交');

                // 启动流程
                const processResponse = await axios.post(`/api/leave/${application.id}/start-process`);
                const processData = processResponse.data;

                state.showSuccess = true;
                ElMessage.success('流程已成功启动！');

                // 重置表单
                resetForm();

                // 刷新数据
                await loadData();

                // 3秒后隐藏成功消息
                setTimeout(() => {
                    state.showSuccess = false;
                }, 3000);

            } catch (error) {
                console.error('提交失败:', error);
                state.errorMessage = error.response?.data?.message || '提交失败，请重试';
                ElMessage.error(state.errorMessage);
            } finally {
                state.submitting = false;
            }
        };

        const loadData = async () => {
            try {
                state.loading = true;

                // 获取统计信息
                const response = await axios.get('/api/leave');
                const applications = response.data;

                // 计算统计信息
                state.stats.totalApplications = applications.length;
                state.stats.pendingApplications = applications.filter(app => app.status === 'PENDING').length;
                state.stats.approvedApplications = applications.filter(app => app.status === 'APPROVED').length;
                state.stats.rejectedApplications = applications.filter(app => app.status === 'REJECTED').length;

                // 获取最近的流程（最新的5个）
                state.recentProcesses = applications
                    .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
                    .slice(0, 5)
                    .map(app => ({
                        id: app.id,
                        applicant: app.applicant,
                        leaveType: app.leaveType,
                        startDate: app.startDate,
                        endDate: app.endDate,
                        duration: app.duration,
                        status: app.status,
                        createTime: app.createTime
                    }));

            } catch (error) {
                console.error('加载数据失败:', error);
                ElMessage.error('加载数据失败');
            } finally {
                state.loading = false;
            }
        };

        const getStatusClass = (status) => {
            const classes = {
                'DRAFT': 'status-draft',
                'PENDING': 'status-pending',
                'APPROVED': 'status-approved',
                'REJECTED': 'status-rejected'
            };
            return classes[status] || 'status-draft';
        };

        const formatDate = (date) => {
            return dayjs(date).format('YYYY-MM-DD');
        };

        const formatDateTime = (datetime) => {
            return dayjs(datetime).format('YYYY-MM-DD HH:mm');
        };

        // 生命周期钩子
        onMounted(() => {
            loadData();
        });

        // 返回模板中需要的数据和方法
        return {
            state,
            isValidForm,
            calculatedDuration,
            submitForm,
            resetForm,
            getStatusClass,
            formatDate,
            formatDateTime
        };
    }
});

// 全局配置
app.use(ElementPlus);
app.mount('#app');