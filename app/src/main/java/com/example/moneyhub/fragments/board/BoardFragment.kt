package com.example.moneyhub.fragments.board

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.moneyhub.activity.postonboard.PostOnBoardActivity
import com.example.moneyhub.activity.viewonboard.ViewOnBoardActivity
import com.example.moneyhub.adapter.BoardRecyclerAdapter
import com.example.moneyhub.databinding.FragmentBoardBinding
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.PostSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BoardViewModel by viewModels()
    private lateinit var adapter: BoardRecyclerAdapter

    // ActivityResultLauncher for PostOnBoardActivity
    private val postActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.loadPosts() // 게시글 작성 후 새로고침
            }
        }

    // ActivityResultLauncher for ViewOnBoardActivity
    private val viewOnBoardActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.loadPosts() // 게시글 삭제 후 새로고침
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.fabPost.setOnClickListener {
            val intent = Intent(context, PostOnBoardActivity::class.java)
            postActivityLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadPosts() // 화면에 표시될 때마다 게시글 목록 새로고침
    }

    private fun setupRecyclerView() {
        adapter = BoardRecyclerAdapter(emptyList(),
            onItemClick = { post ->
                // ViewOnBoardActivity로 이동할 때 launcher 사용
                val intent = Intent(requireContext(), ViewOnBoardActivity::class.java)
                PostSession.setPost(post)
                viewOnBoardActivityLauncher.launch(intent)
            }
        )
        binding.recyclerViewBoard.adapter = adapter
        binding.recyclerViewBoard.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postList.collect { posts ->
                    adapter.updatePosts(posts)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when {
                        state.isLoading -> binding.progressBar.visibility = View.VISIBLE
                        else -> binding.progressBar.visibility = View.GONE
                    }

                    state.error?.let { error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
