/**
 * KuGou Music service — uses direct HTTP calls to Kugou's mobile API.
 */
const axios = require('axios');

const UA = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36';

/**
 * Search KuGou Music.
 * Returns normalized SongResult[] with { id, name, artists, album, duration, fee }.
 */
async function searchKugou(keyword, limit = 30) {
  // Kugou search API v3
  const resp = await axios.get('http://mobilecdn.kugou.com/api/v3/search/song', {
    params: {
      format: 'json',
      keyword,
      page: 1,
      pagesize: limit,
      showtype: 1,
    },
    headers: { 'User-Agent': UA },
  });

  const data = resp.data?.data || {};
  const list = data.info || [];
  return list.map((song) => ({
    id: song.hash || song.song_hash,
    name: song.songname || song.song_name || song.filename?.split(' - ')[1] || '',
    artists: [{ id: 0, name: song.singername || song.author_name || '' }],
    album: { id: song.album_id || 0, name: song.album_name || '' },
    duration: song.duration || 0,
    fee: song.fee_type?.includes('vip') ? 1 : 0,
  }));
}

/**
 * Get KuGou Music song play URLs.
 * Returns SongUrl[] with { id, url, type }.
 */
async function getKugouSongUrls(songHash) {
  try {
    const resp = await axios.get('http://m.kugou.com/app/i/getSongInfo.php', {
      params: { cmd: 'playInfo', hash: String(songHash) },
      headers: { 'User-Agent': UA },
    });
    const d = resp.data || {};
    // url/backup_url may be empty string or empty object {} — only use string values
    let url = (typeof d.url === 'string' && d.url) ? d.url : '';
    if (!url && typeof d.backup_url === 'string' && d.backup_url) url = d.backup_url;
    // Fallback: build URL from extra hash info
    if (!url && d.extra) {
      const hash128 = d.extra['128hash'] || '';
      const size128 = d.extra['128filesize'] || 0;
      if (hash128 && size128) {
        url = `http://fs.w.kugou.com/${hash128}/${size128}/${hash128}.mp3`;
      }
    }
    return [{ id: songHash, url, type: '128' }];
  } catch (e) {
    return [];
  }
}

/**
 * Get KuGou Music lyrics.
 * Returns { lyric: string }.
 */
async function getKugouLyric(songHash) {
  try {
    const resp = await axios.get('http://krcs.kugou.com/search', {
      params: {
        hash: String(songHash),
        ver: 1,
        man: 'yes',
        client: 'mobi',
      },
      headers: { 'User-Agent': UA },
    });
    const candidates = resp.data?.candidates || [];
    if (candidates.length > 0) {
      const lyricResp = await axios.get(candidates[0].accesskey, {
        headers: { 'User-Agent': UA },
      });
      const raw = typeof lyricResp.data === 'string' ? lyricResp.data : JSON.stringify(lyricResp.data);
      return { lyric: raw };
    }
  } catch (e) {
    // lyrics not available
  }
  return { lyric: '' };
}

module.exports = { searchKugou, getKugouSongUrls, getKugouLyric };
