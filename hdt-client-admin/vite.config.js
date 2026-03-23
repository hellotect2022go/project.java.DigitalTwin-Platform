import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import path from 'path'
import vitePluginSvgr from 'vite-plugin-svgr'

// https://vite.dev/config/
export default defineConfig({
  base:'./',
  server: {
    open: '/admin/', // 브라우저를 열 때 이 주소로 열기
    host:true
  },
  plugins: [
    react(),
    vitePluginSvgr(),
    babel({ presets: [reactCompilerPreset()] })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'), // @를 src 폴더로 매핑
    },
  },
})
