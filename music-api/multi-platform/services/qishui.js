/**
 * Qishui (汽水) Music service.
 *
 * Qishui is ByteDance's music platform with no public API. Search is
 * attempted via known internal endpoints; results may be limited.
 */
const axios = require('axios');

const UA = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36';

/**
 * Search Qishui Music.
 * Returns normalized SongResult[] or empty array on failure.
 */
async function searchQishui(keyword, limit = 30) {
  try {
    // Try the internal Qishui search API
    const resp = await axios.get('https://qishui.douyin.com/api/v1/search', {
      params: {
        keyword,
        type: 'song',
        limit,
        offset: 0,
      },
      headers: {
        'User-Agent': UA,
        Referer: 'https://qishui.douyin.com/',
      },
      timeout: 8000,
    });

    const data = resp.data?.data || resp.data;
    const list = data?.songs || data?.list || data?.data || [];
    return list.map((song) => ({
      id: song.id || song.song_id,
      name: song.name || song.title || song.song_name,
      artists: (song.artists || song.singer || []).map((a) => ({
        id: a.id || 0,
        name: a.name || '',
      })),
      album: { id: song.album?.id || 0, name: song.album?.name || '' },
      duration: Math.round((song.duration || 0) / 1000),
      fee: song.fee_type || song.fee || 8,
    }));
  } catch (e) {
    console.error('Qishui search failed:', e.message);
    // Fallback: try alternative endpoint
    try {
      const resp = await axios.get('https://doushen.douyin.com/api/v1/music/search', {
        params: { keyword, count: limit },
        headers: { 'User-Agent': UA },
        timeout: 8000,
      });
      const list = resp.data?.musics || resp.data?.data || [];
      return list.map((song) => ({
        id: song.id || song.music_id,
        name: song.title || song.name,
        artists: [{ id: 0, name: song.author || song.artist || '' }],
        album: { id: 0, name: '' },
        duration: song.duration || 0,
        fee: 8,
      }));
    } catch (e2) {
      console.error('Qishui fallback also failed:', e2.message);
      return [];
    }
  }
}

module.exports = { searchQishui };
