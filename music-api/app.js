const { serveNcmApi } = require('NeteaseCloudMusicApi');

async function start() {
  await serveNcmApi({
    port: 3000,
    host: '0.0.0.0',
    checkVersion: false,
  });
  console.log('[netease-music-api] 服务已启动，端口: 3000');
}

start().catch((err) => {
  console.error('[netease-music-api] 启动失败:', err.message);
  process.exit(1);
});
