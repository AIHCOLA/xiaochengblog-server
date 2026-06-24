const express = require('express');
const ncm = require('NeteaseCloudMusicApi');
const { searchQQ, getQQSongUrls, getQQLyric } = require('./services/qq');
const { searchKugou, getKugouSongUrls, getKugouLyric } = require('./services/kugou');
const { searchQishui } = require('./services/qishui');

const app = express();
const PORT = process.env.PORT || 3100;

// ========== Search ==========
app.get('/search', async (req, res) => {
  const { keywords, limit = '30', platform = 'ncm' } = req.query;
  if (!keywords || !keywords.trim()) {
    return res.json({ code: 400, message: 'keywords required' });
  }
  try {
    let data;
    const lim = parseInt(limit, 10) || 30;
    switch (platform) {
      case 'qq':
        data = await searchQQ(keywords.trim(), lim);
        break;
      case 'kugou':
        data = await searchKugou(keywords.trim(), lim);
        break;
      case 'qishui':
        data = await searchQishui(keywords.trim(), lim);
        break;
      case 'ncm':
      default: {
        const r = await ncm.cloudsearch({ keywords: keywords.trim(), limit: lim, type: req.query.type || 1 });
        const raw = r.body?.result?.songs || [];
        data = raw.map((s) => ({
          id: s.id,
          name: s.name,
          artists: (s.ar || []).map((a) => ({ id: a.id, name: a.name })),
          album: { id: s.al?.id || 0, name: s.al?.name || '', picUrl: s.al?.picUrl },
          duration: Math.round((s.dt || 0) / 1000),
          fee: s.fee || 0,
        }));
        break;
      }
    }
    res.json({ code: 200, result: { songs: data } });
  } catch (e) {
    console.error(`[${platform}] search error:`, e.message);
    res.json({ code: 500, message: e.message || 'Search failed' });
  }
});

// ========== Song URL ==========
app.get('/song/url/v1', async (req, res) => {
  const { id, platform = 'ncm', level = 'standard' } = req.query;
  if (!id) return res.json({ code: 400, message: 'id required' });
  try {
    let data;
    switch (platform) {
      case 'qq':
        data = await getQQSongUrls(id, level);
        break;
      case 'kugou':
        data = await getKugouSongUrls(id);
        break;
      case 'ncm':
      default: {
        const r = await ncm.song_url_v1({ id, level });
        data = r.body?.data || [];
        break;
      }
    }
    res.json({ code: 200, data });
  } catch (e) {
    console.error(`[${platform}] url error:`, e.message);
    res.json({ code: 500, message: e.message || 'Get URL failed' });
  }
});

// ========== Lyric ==========
app.get('/lyric', async (req, res) => {
  const { id, platform = 'ncm' } = req.query;
  if (!id) return res.json({ code: 400, message: 'id required' });
  try {
    let data;
    switch (platform) {
      case 'qq':
        data = await getQQLyric(id);
        break;
      case 'kugou':
        data = await getKugouLyric(id);
        break;
      case 'ncm':
      default: {
        const r = await ncm.lyric({ id });
        data = r.body?.lrc || { lyric: '' };
        break;
      }
    }
    res.json({ code: 200, lrc: data });
  } catch (e) {
    console.error(`[${platform}] lyric error:`, e.message);
    res.json({ code: 500, message: e.message || 'Get lyric failed' });
  }
});

// ========== Catch-all for other NCM endpoints ==========
app.all('*', async (req, res) => {
  // Skip handled paths
  if (req.path === '/search' || req.path === '/song/url/v1' || req.path === '/lyric') return;
  try {
    const fnName = req.path.replace(/^\//, '').replace(/\//g, '_').replace(/[^a-zA-Z0-9_]/g, '');
    const fn = ncm[fnName];
    if (typeof fn === 'function') {
      const r = await fn(req.query);
      res.json(r.body || r);
    } else {
      res.json({ code: 404, message: `Unknown endpoint: ${req.path}` });
    }
  } catch (e) {
    console.error(`[ncm] ${req.path} error:`, e.message);
    res.json({ code: 500, message: e.message || 'NCM error' });
  }
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`[unified-music-api] 统一音乐服务已启动，端口: ${PORT}`);
});
