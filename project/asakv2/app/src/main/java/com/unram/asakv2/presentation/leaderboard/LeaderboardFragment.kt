package com.unram.asakv2.presentation.leaderboard

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.unram.asakv2.data.remote.RankingItem
import com.unram.asakv2.databinding.FragmentLeaderboardBinding
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    // Note: In a real app, inject this via Dagger/Hilt or custom ViewModelFactory
    // For now, let's assume we can get it or we mock it if repository is not injected.
    // However, since we don't have Hilt setup, we will use a dummy factory or just wait for injection.
    // For this example, let's comment out the real viewModels() if it crashes, but let's try.
    // private val viewModel: LeaderboardViewModel by viewModels() 

    private var countdownTimer: CountDownTimer? = null
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        
        // MOCK DATA for now to show the beautiful UI!
        showMockData()
        
        // Contoh implementasi observeViewModel (akan dikombinasi dengan ViewBinding nantinya)
        /*
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LeaderboardUiState.Loading -> { } // showLoading(true)
                        is LeaderboardUiState.Success -> {
                            binding.tvLeagueName.text = state.leagueName
                            startCountdown(state.resetInSeconds)
                            adapter.submitList(state.rankings)
                            updateStickyRow(state.myRanking)
                        }
                        is LeaderboardUiState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        */
    }
    
    private fun setupRecyclerView() {
        adapter = LeaderboardAdapter()
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLeaderboard.adapter = adapter
    }

    private fun showMockData() {
        binding.tvLeagueName.text = "Gold League"
        startCountdown(225015) // Example seconds
        
        val mockRankings = listOf(
            RankingItem("u2", "Budi Sasak", "Gold League", 5000, 1),
            RankingItem("u3", "Siti Lombok", "Gold League", 4800, 2),
            RankingItem("u4", "Agus Rinjani", "Gold League", 4500, 3),
            RankingItem("u1", "16_ RIDHO'", "Gold League", 3200, 4),
            RankingItem("u5", "Dewi Mataram", "Gold League", 2100, 5)
        )
        adapter.submitList(mockRankings)
        
        // Sticky row
        val myRank = mockRankings[3]
        updateStickyRow(myRank)
    }

    private fun updateStickyRow(myRanking: RankingItem?) {
        if (myRanking != null) {
            binding.cardStickyUser.visibility = View.VISIBLE
            // Use include layout binding logic (findViewById on the included layout)
            val stickyView = binding.cardStickyUser
            val tvRank = stickyView.findViewById<android.widget.TextView>(com.unram.asakv2.R.id.tv_rank)
            val tvName = stickyView.findViewById<android.widget.TextView>(com.unram.asakv2.R.id.tv_username)
            val tvXp = stickyView.findViewById<android.widget.TextView>(com.unram.asakv2.R.id.tv_xp)
            
            tvRank?.text = myRanking.rank_position.toString()
            tvName?.text = myRanking.username
            tvXp?.text = "${myRanking.total_xp} XP"
            
            when (myRanking.rank_position) {
                1 -> tvRank?.setTextColor(android.graphics.Color.parseColor("#FFD700"))
                2 -> tvRank?.setTextColor(android.graphics.Color.parseColor("#C0C0C0"))
                3 -> tvRank?.setTextColor(android.graphics.Color.parseColor("#CD7F32"))
                else -> tvRank?.setTextColor(android.graphics.Color.WHITE)
            }
        } else {
            binding.cardStickyUser.visibility = View.GONE
        }
    }

    private fun startCountdown(resetInSeconds: Long) {
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(resetInSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val d = seconds / 86400
                val h = (seconds % 86400) / 3600
                val m = (seconds % 3600) / 60
                val s = seconds % 60
                
                binding.tvCountdown.text = String.format("%02dd %02dh %02dm %02ds", d, h, m, s)
            }

            override fun onFinish() {
                // Trigger refresh data from API
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        _binding = null
    }
}
