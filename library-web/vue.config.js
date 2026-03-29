const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8443', // 这里填写您的后端服务器地址
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  css: {
    loaderOptions: {
      // 配置 SCSS 全局变量和混入
      scss: {
        additionalData: `
          @import "~@/assets/styles/variables.scss";
          @import "~@/assets/styles/mixins.scss";
        `
      }
    }
  }
})
