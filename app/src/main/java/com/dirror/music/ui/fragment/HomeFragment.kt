package com.dirror.music.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.adapter.NewSongAdapter
import com.dirror.music.adapter.PlaylistRecommendAdapter
import com.dirror.music.databinding.FragmentHomeBinding
import com.dirror.music.foyou.sentence.Sentence
import com.dirror.music.music.netease.NewSong
import com.dirror.music.music.netease.PlaylistRecommend
import com.dirror.music.music.standard.data.SOURCE_DIRROR
import com.dirror.music.ui.activity.PlaylistActivity
import com.dirror.music.ui.activity.RecommendActivity
import com.dirror.music.util.AnimationUtil
import com.dirror.music.util.Config
import com.dirror.music.util.runOnMainThread

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val windowManager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = windowManager.defaultDisplay.width

        initView()
        initListener()
        update()
    }

    private fun initView() {
        if (!MyApplication.mmkv.decodeBool(Config.SENTENCE_RECOMMEND, true)) {
            binding.tvFoyou.visibility =View.GONE
            binding.includeFoyou.root.visibility = View.GONE
        }
    }

    /**
     * 刷新整个页面
     */
    private fun update() {
        // Banner
        // initBanner()
        // 推荐歌单
        refreshPlaylistRecommend()
        // 新歌速递
        updateNewSong()
        // 更改句子
        changeSentence()
    }

    private fun initListener() {
        binding.includeFoyou.root.setOnClickListener {
            changeSentence()
        }

        binding.clDaily.setOnClickListener {
            val intent = Intent(this.context, RecommendActivity::class.java)
            startActivity(intent)
        }

        binding.clDso.setOnClickListener {
            val intent = Intent(this.context, PlaylistActivity::class.java)
            intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_SOURCE, SOURCE_DIRROR)
            intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, 0)
            startActivity(intent)
        }
    }

    private fun changeSentence() {
        binding.includeFoyou.tvText.alpha = 0f
        binding.includeFoyou.tvAuthor.alpha = 0f
        binding.includeFoyou.tvSource.alpha = 0f
        Sentence.getSentence {
            runOnMainThread {
                binding.includeFoyou.tvText.text = it.text
                binding.includeFoyou.tvAuthor.text = it.author
                binding.includeFoyou.tvSource.text = it.source
                AnimationUtil.fadeIn(binding.includeFoyou.tvText, 1000, false)
                AnimationUtil.fadeIn(binding.includeFoyou.tvAuthor, 1000, false)
                AnimationUtil.fadeIn(binding.includeFoyou.tvSource, 1000, false)
            }
        }
    }

    private fun refreshPlaylistRecommend() {
        PlaylistRecommend.getPlaylistRecommend({
            runOnMainThread {
                binding.rvPlaylistRecommend.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                // binding.rvPlaylistRecommend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvPlaylistRecommend.adapter = PlaylistRecommendAdapter(it)

            }
        }, {

        })

    }

    private fun updateNewSong() {
        this.context?.let {
            NewSong.getNewSong(it) {
                runOnMainThread {
                    binding.rvNewSong.layoutManager = GridLayoutManager(this.context, 2)
                    binding.rvNewSong.adapter = NewSongAdapter(it)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // _binding = null
    }

}