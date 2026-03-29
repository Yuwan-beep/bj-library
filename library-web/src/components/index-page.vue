<template>
    <div class="container">
        <h1 class="title">图书馆统计</h1>
        <div v-if="loading" class="loading">
            <div class="loading-text">数据加载中...</div>
            <div class="loading-time" v-if="loadingTime">
                已用时：{{ loadingTime }} 秒
            </div>
        </div>
        <div v-else>
            <div class="charts-container">
                <div ref="totalBooks" class="chart total-chart"></div>
                <div ref="categoryPie" class="chart pie-chart"></div>
            </div>
            
            <div class="stats-container">
                <div class="ranking-section">
                    <h2 class="ranking-title">借阅排行榜 TOP 10</h2>
                    <div class="carousel-container">
                        <div class="scroll-list" ref="scrollList">
                            <div class="scroll-content" :style="{ transform: 'translateY(' + scrollOffset + 'px)' }">
                                <div v-for="book in topBooks" 
                                     :key="book.id" 
                                     class="book-item"
                                     :class="{'top-three': book.rank <= 3}">
                                    <div class="rank-number" :class="'rank-' + book.rank">
                                        {{ book.rank }}
                                    </div>
                                    <div class="book-info">
                                        <div class="book-title">《{{ book.name }}》</div>
                                        <div class="book-meta">
                                            作者：{{ book.author }}
                                        </div>
                                    </div>
                                    <div class="borrow-count">
                                        <span class="number">{{ book.cumulativeBorrow }}</span>
                                        <span class="text">次借阅</span>
                                    </div>
                                </div>
                                <div v-for="book in topBooks" 
                                     :key="'duplicate-' + book.id" 
                                     class="book-item"
                                     :class="{'top-three': book.rank <= 3}">
                                    <div class="rank-number" :class="'rank-' + book.rank">
                                        {{ book.rank }}
                                    </div>
                                    <div class="book-info">
                                        <div class="book-title">《{{ book.name }}》</div>
                                        <div class="book-meta">
                                            作者：{{ book.author }}
                                        </div>
                                    </div>
                                    <div class="borrow-count">
                                        <span class="number">{{ book.cumulativeBorrow }}</span>
                                        <span class="text">次借阅</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="category-stats-section">
                    <h2 class="stats-title">图书借阅分类统计</h2>
                    <div ref="borrowPie" class="chart borrow-pie-chart"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts'
import { getBookInfo } from '../../api/book'
import bookClassification from '../assets/bookClassification'

export default {
    name: 'IndexPage',
    data() {
        return {
            totalBooks: 0,
            categoryData: [],
            loading: true,
            loadingTime: 0,
            timer: null,
            charts: {
                totalBooks: null,
                categoryPie: null,
                borrowPie: null
            },
            colorPalette: [
                '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
                '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#B22222',
                '#48D1CC', '#FF69B4', '#DDA0DD', '#9370DB', '#8A2BE2',
                '#4B0082', '#483D8B', '#008B8B', '#556B2F', '#8B4513',
                '#CD853F', '#DAA520', '#FF8C00', '#FF4500'
            ],
            topBooks: [],
            scrollOffset: 0,
            scrollTimer: null,
            categoryBorrowStats: []
        }
    },
    mounted() {
        this.initData()
        window.addEventListener('resize', this.handleResize)
    },
    beforeDestroy() {
        if (this.timer) {
            clearInterval(this.timer)
        }
        if (this.scrollTimer) {
            clearInterval(this.scrollTimer)
        }
        window.removeEventListener('resize', this.handleResize)
        Object.values(this.charts).forEach(chart => {
            if (chart) {
                chart.dispose()
            }
        })
    },
    methods: {
        handleResize() {
            Object.values(this.charts).forEach(chart => {
                if (chart) {
                    chart.resize()
                }
            })
        },
        startLoadingTimer() {
            this.loadingTime = 0
            this.timer = setInterval(() => {
                this.loadingTime++
            }, 1000)
        },
        stopLoadingTimer() {
            if (this.timer) {
                clearInterval(this.timer)
                this.timer = null
            }
        },
        getCategoryName(classNumber) {
            const category = bookClassification.find(c => c.classNumber === classNumber)
            return category ? category.className : '未分类'
        },
        startScroll() {
            const itemHeight = 60; // 每个项目的高度（包括间距）
            const totalHeight = this.topBooks.length * itemHeight;
            
            this.scrollTimer = setInterval(() => {
                this.scrollOffset -= 1; // 每次移动1像素
                if (Math.abs(this.scrollOffset) >= totalHeight) {
                    this.scrollOffset = 0; // 重置位置
                }
            }, 50); // 每50ms移动一次，实现平滑滚动
        },
        async initData() {
            this.loading = true
            this.startLoadingTimer()

            try {
                const promises = bookClassification
                    .map(category => getBookInfo(category))

                const responses = await Promise.all(promises)
                this.totalBooks = responses[0].data.length

                // 处理各分类数据
                this.categoryData = responses.slice(1).map((response, index) => ({
                    name: bookClassification[index + 1].className,
                    value: response.data.length
                })).filter(item => item.value > 0)

                // 处理借阅排行榜数据
                const allBooks = responses[0].data
                this.topBooks = allBooks
                    .sort((a, b) => b.cumulativeBorrow - a.cumulativeBorrow)
                    .slice(0, 10)
                    .map((book, index) => ({
                        ...book,
                        rank: index + 1
                    }))

                // 处理分类借阅统计
                this.categoryBorrowStats = responses.slice(1)
                    .map((response, index) => {
                        const books = response.data
                        const totalBorrow = books.reduce((sum, book) => sum + (book.cumulativeBorrow || 0), 0)
                        return {
                            name: bookClassification[index + 1].className,
                            bookCount: books.length,
                            borrowCount: totalBorrow,
                            avgBorrow: books.length ? totalBorrow / books.length : 0
                        }
                    })
                    .filter(item => item.bookCount > 0)
                    .sort((a, b) => b.borrowCount - a.borrowCount)
                    .map((item, index) => ({
                        ...item,
                        rank: index + 1
                    }))

                this.loading = false
                this.stopLoadingTimer()

                await this.$nextTick()
                this.initCharts()
                this.startScroll()
            } catch (error) {
                console.error('获取数据失败：', error)
                this.loading = false
                this.stopLoadingTimer()
            }
        },
        initCharts() {
            this.initTotalChart()
            this.initPieChart()
            this.initBorrowPieChart()
        },
        initTotalChart() {
            if (this.charts.totalBooks) {
                this.charts.totalBooks.dispose()
            }
            
            const chartDom = this.$refs.totalBooks
            if (!chartDom) return

            this.charts.totalBooks = echarts.init(chartDom)
            this.charts.totalBooks.setOption({
                title: {
                    text: '图书馆藏书总量',
                    left: 'center'
                },
                series: [{
                    type: 'gauge',
                    progress: {
                        show: true,
                        width: 18
                    },
                    axisLine: {
                        lineStyle: {
                            width: 18
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        length: 15,
                        lineStyle: {
                            width: 2,
                            color: '#999'
                        }
                    },
                    pointer: {
                        icon: 'path://M2090.36389,615.30999 L2090.36389,615.30999 C2091.48372,615.30999 2092.40383,616.23010 2092.40383,617.34993 L2092.40383,618.67956 C2092.40383,619.79939 2091.48372,620.71950 2090.36389,620.71950 L2090.36389,620.71950 C2089.24406,620.71950 2088.32395,619.79939 2088.32395,618.67956 L2088.32395,617.34993 C2088.32395,616.23010 2089.24406,615.30999 2090.36389,615.30999 Z',
                        length: '60%',
                        width: 6,
                        offsetCenter: [0, '5%']
                    },
                    detail: {
                        valueAnimation: true,
                        formatter: '{value}',
                        color: 'inherit'
                    },
                    data: [{
                        value: this.totalBooks
                    }]
                }]
            })
        },
        initPieChart() {
            if (this.charts.categoryPie) {
                this.charts.categoryPie.dispose()
            }

            const chartDom = this.$refs.categoryPie
            if (!chartDom) return

            this.charts.categoryPie = echarts.init(chartDom)
            this.charts.categoryPie.setOption({
                title: {
                    text: '图书分类统计',
                    left: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b}: {c} ({d}%)'
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    type: 'scroll',
                    textStyle: {
                        fontSize: 12
                    }
                },
                color: this.colorPalette,
                series: [
                    {
                        name: '图书分类',
                        type: 'pie',
                        radius: ['40%', '70%'],
                        avoidLabelOverlap: false,
                        itemStyle: {
                            borderRadius: 10,
                            borderColor: '#fff',
                            borderWidth: 2
                        },
                        label: {
                            show: false
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        },
                        data: this.categoryData
                    }
                ]
            })
        },
        initBorrowPieChart() {
            if (this.charts.borrowPie) {
                this.charts.borrowPie.dispose()
            }

            const chartDom = this.$refs.borrowPie
            if (!chartDom) return

            // 获取图书分类饼图的颜色映射
            const categoryColors = {}
            this.categoryData.forEach((item, index) => {
                categoryColors[item.name] = this.colorPalette[index]
            })

            this.charts.borrowPie = echarts.init(chartDom)
            this.charts.borrowPie.setOption({
                title: {
                    text: '借阅分类占比',
                    left: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: function(params) {
                        const data = params.data
                        return `${data.name}<br/>
                                借阅次数：${data.value}<br/>
                                占比：${params.percent}%<br/>
                                平均借阅：${data.avgBorrow.toFixed(1)}次/本`
                    }
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    type: 'scroll',
                    textStyle: {
                        fontSize: 12
                    }
                },
                series: [
                    {
                        name: '借阅分类',
                        type: 'pie',
                        radius: ['40%', '70%'],
                        avoidLabelOverlap: false,
                        itemStyle: {
                            borderRadius: 10,
                            borderColor: '#fff',
                            borderWidth: 2
                        },
                        label: {
                            show: false
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        },
                        data: this.categoryBorrowStats.map(item => ({
                            name: item.name,
                            value: item.borrowCount,
                            avgBorrow: item.avgBorrow,
                            itemStyle: {
                                color: categoryColors[item.name] || this.colorPalette[0]
                            }
                        }))
                    }
                ]
            })
        }
    }
}
</script>

<style scoped>
.container {
    padding: 20px;
    width: 100%;
    box-sizing: border-box;
    min-height: 100vh;
    background-color: #f5f7fa;
}

.title {
    text-align: center;
    color: #303133;
    margin-bottom: 30px;
    font-size: 24px;
}

.charts-container {
    display: flex;
    justify-content: center;
    gap: 40px;
    margin-top: 20px;
    width: 100%;
}

.chart {
    width: 500px;
    height: 400px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
    padding: 20px;
    box-sizing: border-box;
}

.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 400px;
}

.loading-text {
    font-size: 24px;
    color: #409EFF;
    margin-bottom: 15px;
}

.loading-time {
    font-size: 18px;
    color: #909399;
}

.stats-container {
    display: flex;
    gap: 40px;
    margin-top: 40px;
    justify-content: center;
}

.ranking-section {
    width: 500px;
}

.category-stats-section {
    width: 500px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
    padding: 20px;
}

.stats-title {
    text-align: center;
    color: #303133;
    margin-bottom: 20px;
    font-size: 20px;
}

.borrow-pie-chart {
    width: 100%;
    height: 400px;
}

.ranking-title {
    text-align: center;
    color: #303133;
    margin-bottom: 20px;
    font-size: 20px;
}

.carousel-container {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
    padding: 20px;
    height: 400px;
    overflow: hidden;
}

.scroll-list {
    height: 100%;
    overflow: hidden;
    position: relative;
}

.scroll-content {
    transition: transform 0.05s linear;
    padding: 10px 0;
}

.book-item {
    display: flex;
    align-items: center;
    padding: 12px 15px;
    border-radius: 6px;
    background: #f5f7fa;
    margin-bottom: 10px;
    height: 60px;
}

.book-item:hover {
    background: #ecf5ff;
}

.rank-number {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    font-weight: bold;
    font-size: 16px;
    margin-right: 15px;
    background: #909399;
    color: #fff;
    flex-shrink: 0;
}

.rank-1 { background: #f56c6c; }
.rank-2 { background: #e6a23c; }
.rank-3 { background: #67c23a; }

.book-info {
    flex: 1;
    min-width: 0;
    margin-right: 15px;
}

.book-title {
    font-size: 15px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.book-meta {
    font-size: 13px;
    color: #606266;
}

.book-meta .author {
    color: #67c23a;
}

.book-meta .separator {
    margin: 0 8px;
    color: #dcdfe6;
}

.borrow-count {
    text-align: right;
    white-space: nowrap;
    flex-shrink: 0;
}

.borrow-count .number {
    font-size: 20px;
    font-weight: bold;
    color: #409EFF;
    margin-right: 5px;
}

.borrow-count .text {
    font-size: 12px;
    color: #909399;
}

@media screen and (max-width: 1400px) {
    .stats-container {
        flex-direction: column;
        align-items: center;
    }

    .ranking-section,
    .category-stats-section {
        width: 100%;
        max-width: 800px;
    }
}
</style>


