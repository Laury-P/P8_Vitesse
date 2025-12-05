package com.openclassroom.p8_vitesse.ui.HomeScreen

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var candidateAdapter: CandidateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        tabsSelectedDisplay()
        observeAllCandidate()
    }

    private fun setupRecyclerView() {
        candidateAdapter = CandidateAdapter()
        binding.recyclerView.apply {
            adapter = candidateAdapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            //TO DO: Navigation vers l'ajout d'un candidat (avec un alert dialogue?)
        }
    }

    private fun observeAllCandidate() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.allCandidatesFlow, viewModel.isLoading) {list, isLoading ->
                list to isLoading
            }.collect { (list, isLoading) ->
                binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.noCandidate.visibility = if (list.isEmpty() && !isLoading) View.VISIBLE else View.GONE
                candidateAdapter.submitList(list)
            }
        }
    }

    private fun observeFavoriteCandidate() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.favoriteCandidatesFlow, viewModel.isLoading){list, isLoading ->
                list to isLoading
            }.collect { (list, isLoading) ->
                binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.noCandidate.visibility = if (list.isEmpty() && !isLoading) View.VISIBLE else View.GONE
                candidateAdapter.submitList(list)
            }
        }
    }

    private fun tabsSelectedDisplay() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> observeAllCandidate()
                    1 -> observeFavoriteCandidate()
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        }
        )
    }





}
