package com.example.moneyhub.fragments.board

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.moneyhub.activity.postonboard.PostOnBoardActivity
import com.example.moneyhub.adapter.BoardRecyclerAdapter
import com.example.moneyhub.databinding.FragmentBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardFragment : Fragment() {

    private lateinit var binding: FragmentBoardBinding
    private val viewModel: BoardFragmentViewModel by viewModels()
    private lateinit var adapter: BoardRecyclerAdapter

    override fun onCreate(savedInstnaceState: Bundle?) {
        super.onCreate(savedInstnaceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBoardBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        binding.fabPost.setOnClickListener{
            val intent = Intent(context, PostOnBoardActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = BoardRecyclerAdapter(emptyList())
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
    }
}