@echo off
echo ===== Starting Unified Music API (port 3100) =====
echo   网易云 → NeteaseCloudMusicApi 直连
echo   QQ音乐 / 酷狗音乐 / 汽水音乐 → 直连
cd /d "%~dp0music-api\multi-platform"
if not exist "node_modules" (
    echo Installing unified music-api dependencies...
    call npm install
)
start "unified-music-api" cmd /c "node app.js"

echo ===== Starting Blog Server (port 8080) =====
cd /d "%~dp0"
start "blog-server" cmd /c "mvn spring-boot:run"

echo.
echo All services are starting...
echo   unified-music-api    : http://localhost:3100
echo   blog-server          : http://localhost:8080
echo.
echo Close this window and the terminal windows to stop.
pause
