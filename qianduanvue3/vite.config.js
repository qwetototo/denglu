import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '127.0.0.1',
    port: 18080,
    proxy: {
      // 前端请求 /api/xxx 会被代理到本地后端
      '/api': {
        target: 'http://127.0.0.1:18081',
        changeOrigin: true,
        // 如果后端接口本身没有 /api 前缀，取消下面这行注释
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
