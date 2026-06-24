/**
 * QQ Music service — uses @sansenjian/qq-music-api SDK for search,
 * and direct HTTP calls for song URLs and lyrics.
 */
const axios = require('axios');

let qqSearch;
try {
  const mod = require('@sansenjian/qq-music-api/sdk');
  qqSearch = mod.search || mod.default?.search;
} catch {
  qqSearch = null;
}

/**
 * Search QQ Music.
 * Returns normalized SongResult[] with { id, name, artists, album, duration, fee }.
 */
async function searchQQ(keyword, limit = 30) {
  // Try SDK first
  if (qqSearch) {
    try {
      const res = await qqSearch({ key: keyword, limit, page: 1 });
      // Response format: { list: [...] } or { data: { list: [...] } }
      const list = res?.list || res?.data?.list || [];
      // Only trust SDK result if it actually returned data
      if (list.length > 0) {
        return list.map((song) => ({
          id: song.songmid || song.songid || song.id,
          name: song.songname || song.name || song.title,
          artists: (song.singer || []).map((s) => ({ id: s.id || 0, name: s.name || s.name_str || '' })),
          album: { id: song.albumid || 0, name: song.albumname || song.album || '' },
          duration: song.interval || song.duration || 0,
          fee: 8,
        }));
      }
      // SDK returned empty — fall through to HTTP
      console.warn('QQ SDK returned empty results, falling back to HTTP');
    } catch (e) {
      console.error('QQ SDK search failed, falling back to HTTP:', e.message);
      // Falls through to HTTP fallback
    }
  }

  // Fallback: direct HTTP call
  try {
    const resp = await axios.get('https://c.y.qq.com/soso/fcgi-bin/client_search_cp', {
      params: {
        w: keyword,
        n: limit,
        p: 1,
        format: 'json',
        outCharset: 'utf-8',
        t: 0,
      },
      headers: {
        Referer: 'https://y.qq.com/',
        'User-Agent': 'Mozilla/5.0',
      },
    });
    const data = resp.data?.data || {};
    const list = data.song?.list || [];
    return list.map((s) => ({
      id: s.songmid || s.songid || s.id,
      name: s.songname || s.name,
      artists: (s.singer || []).map((si) => ({ id: si.id || 0, name: si.name })),
      album: { id: s.albumid || 0, name: s.albumname || '' },
      duration: s.interval || 0,
      fee: s.pay?.payplay || s.fee || 8,
    }));
  } catch (e) {
    throw new Error(`QQ搜索失败: ${e.message}`);
  }
}

/**
 * Get QQ Music song play URLs.
 * Returns SongUrl[] with { id, url, type }.
 */
async function getQQSongUrls(songId, level) {
  // Try SDK first
  try {
    const sdkMod = require('@sansenjian/qq-music-api/sdk');
    const getPlay = sdkMod.getMusicPlay || sdkMod.default?.getMusicPlay;
    if (getPlay) {
      const qualityMap = { standard: '128', higher: '320', exhigh: 'flac' };
      const quality = qualityMap[level] || '320';
      const result = await getPlay({ songmid: String(songId), quality });
      const sdkUrl = result?.url || result?.data?.url || '';
      if (sdkUrl) {
        return [{ id: songId, url: sdkUrl, type: quality }];
      }
    }
  } catch (e) {
    console.error('QQ SDK getPlayUrl failed:', e.message);
  }

  // Fallback: direct API call
  try {
    const resp = await axios.get('https://u.y.qq.com/cgi-bin/musicu.fcg', {
      params: {
        format: 'json',
        data: JSON.stringify({
          req_0: {
            module: 'vkey.GetVkeyServer',
            method: 'CgiGetVkey',
            param: { guid: '0', songmid: [String(songId)], songtype: [0], uin: '0', loginflag: 0, platform: '20' },
          },
        }),
      },
    });
    const mids = resp.data?.req_0?.data?.midurlinfo || [];
    const sip = resp.data?.req_0?.data?.sip || [];
    const server = sip[0] || '';
    return mids.map((item) => ({
      id: item.songmid,
      url: item.purl ? server + item.purl : '',
      type: level,
    }));
  } catch (e) {
    return [];
  }
}

/**
 * Get QQ Music lyrics.
 * Returns { lyric: string }.
 */
async function getQQLyric(songId) {
  try {
    const sdkMod = require('@sansenjian/qq-music-api/sdk');
    const getLyric = sdkMod.lyric || sdkMod.getLyric || sdkMod.default?.getLyric;
    if (getLyric) {
      const result = await getLyric({ songmid: String(songId), isFormat: true });
      const lrc = result?.lyric || result?.data?.lyric || '';
      const tlyric = result?.tlyric || result?.data?.tlyric || '';
      return { lyric: lrc + '\n' + tlyric };
    }
  } catch (e) {
    console.error('QQ SDK lyric failed:', e.message);
  }
  return { lyric: '' };
}

module.exports = { searchQQ, getQQSongUrls, getQQLyric };
