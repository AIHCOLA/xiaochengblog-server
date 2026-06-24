#!/bin/bash
# 统一启动脚本：启动统一音乐 API + Spring Boot 博客后端

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "===== 启动统一音乐 API (端口 3100) ====="
echo "  网易云 → NeteaseCloudMusicApi 直连"
echo "  QQ/酷狗/汽水 → 直连"
cd "$SCRIPT_DIR/music-api/multi-platform"

if [ ! -d "node_modules" ]; then
  echo "正在安装 unified music-api 依赖..."
  npm install
fi

node app.js &
MUSIC_PID=$!
echo "unified-music-api PID: $MUSIC_PID"

echo "===== 启动博客后端 (端口 8080) ====="
cd "$SCRIPT_DIR"
mvn spring-boot:run &
SERVER_PID=$!
echo "server PID: $SERVER_PID"

cleanup() {
  echo "正在停止所有服务..."
  kill $MUSIC_PID 2>/dev/null
  kill $SERVER_PID 2>/dev/null
  wait
  echo "所有服务已停止"
}

trap cleanup EXIT INT TERM
wait
