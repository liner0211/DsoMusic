package com.dirror.music

import android.util.Log
import com.dirror.music.cloudmusic.*
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.StorageUtil
import com.dirror.music.util.getCurrentTime
import com.dirror.music.util.toast
import com.google.gson.Gson

/**
 * 网易云 api
 * @author Moriafly
 * @since 2020年9月14日15:07:36
 */

object CloudMusic {
    // api 地址
    private const val MUSIC_API_URL = "http://musicapi.leanapp.cn"
    // https://musicapi.leanapp.cn
    // https://api.fczbl.vip/163/

    private fun timestamp(): String {
        return "&timestamp=${getCurrentTime()}"
    }


    fun loginByPhone(phone: String, password: String, callback: LoginCallback) {
        // ${System.currentTimeMillis()}
        val url = "$MUSIC_API_URL/login/cellphone?phone=$phone&password=$password"
        Log.e("url", url)
        MagicHttp.OkHttpManager().get(
            url,
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    Log.e("返回数据", response)
                    // 成功
                    // 解析 json
                    val loginData = Gson().fromJson(response, LoginData::class.java)
                    // 登录成功
                    MagicHttp.runOnMainThread {
                        when (loginData.code) {
                            200 -> {
                                toast("登录成功\n用户名：${loginData.profile.nickname}")
                                callback.success()
                                StorageUtil.putInt(StorageUtil.CLOUD_MUSIC_UID, loginData.profile.userId)
                            }
                            400 -> toast("用户不存在")
                                else -> toast("登录失败\n错误代码：${loginData.code}")
                        }

                    }
                }

                override fun failure(throwable: Throwable) {
                    Log.e("错误", throwable.message.toString())
                }
            })
    }

    interface LoginCallback {
        fun success()
    }

    fun loginByEmail(email: String, password: String) {

    }

    fun loginByUid(uid: Int, callback: LoginByUidCallback) {
        getUserDetail(uid, object : UserDetailCallback {
            override fun success(userDetailData: UserDetailData) {
                toast("登录成功")
                callback.success()
                StorageUtil.putInt(StorageUtil.CLOUD_MUSIC_UID, userDetailData.profile?.userId!!)
            }

            override fun failure() {
                toast("登录失败")
            }
        })
    }

    interface LoginByUidCallback {
        fun success()
    }

    /**
     * 用户歌单
     */
    fun getPlaylist(uid: Int, callback: PlaylistCallback) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/user/playlist?uid=$uid${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val userPlaylistData = Gson().fromJson(response, UserPlaylistData::class.java)
                    callback.success(userPlaylistData)
                }

                override fun failure(throwable: Throwable) {
                    Log.e("获取用户歌单错误", throwable.message.toString())
                }
            })
    }

    interface PlaylistCallback{
        fun success(userPlaylistData: UserPlaylistData)
    }

    fun getUserDetail(uid: Int, callback: UserDetailCallback) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/user/detail?uid=$uid",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val userDetailData = Gson().fromJson(response, UserDetailData::class.java)
                    if (userDetailData.code == 400 || userDetailData.code == 404) {
                        callback.failure()
                    } else {
                        Log.e("图片", userDetailData.profile?.avatarUrl.toString())
                        callback.success(userDetailData)
                    }
                }

                override fun failure(throwable: Throwable) {
                    Log.e("错误", throwable.message.toString())
                }
            })
    }

    interface UserDetailCallback {
        fun success(userDetailData: UserDetailData)
        fun failure()
    }

    /**
     * 获取歌单详情
     */
    fun getDetailPlaylist(id: Long, callback: DetailPlaylistCallback) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/playlist/detail?id=$id${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val detailPlaylistData = Gson().fromJson(response, DetailPlaylistData::class.java)
                    callback.success(detailPlaylistData)
                }

                override fun failure(throwable: Throwable) {
                    Log.e("获取歌单详情错误", throwable.message.toString())
                }
            })
    }

    interface DetailPlaylistCallback {
        fun success(detailPlaylistData: DetailPlaylistData)
    }

    /**
     * 获取歌曲详情
     */
    fun getSongDetail(id: Long, callback: SongCallback) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/song/detail?ids=$id${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val songData = Gson().fromJson(response, SongData::class.java)
                    callback.success(songData)
                }

                override fun failure(throwable: Throwable) {
                    Log.e("获取歌曲详情错误", throwable.message.toString())
                }
            })
    }

    interface SongCallback {
        fun success(songData: SongData)
    }

    /**
     * 获取歌曲评论
     */
    fun getMusicComment(id: Long, callback: MusicCommentCallback) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/comment/music?id=$id&limit=20&offset=0${timestamp()}",
            object : MagicHttp.MagicCallback {
                override fun success(response: String) {
                    val commentData = Gson().fromJson(response, CommentData::class.java)
                    callback.success(commentData)
                }

                override fun failure(throwable: Throwable) {
                    Log.e("获取歌曲详情错误", throwable.message.toString())
                }
            })
    }

    interface MusicCommentCallback {
        fun success(commentData: CommentData)
    }
}