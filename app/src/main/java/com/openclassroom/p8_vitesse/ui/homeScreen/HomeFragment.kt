package com.openclassroom.p8_vitesse.ui.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
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
        setupSearchWidget()
        tabsSelectedDisplay()
        observeCandidate()
    }

    private fun setupRecyclerView() {
        candidateAdapter = CandidateAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = candidateAdapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_to_addFragment)
        }
    }

    private fun setupSearchWidget() {
        val searchView = binding.searchView
        val searchBar = binding.searchBar
        searchBar.setOnClickListener {
            searchView.visibility = View.VISIBLE
            searchBar.visibility = View.INVISIBLE
            searchView.isIconified = false
            searchView.requestFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.setFilter(query ?: "")
                return true
            }
        })

        searchView.setOnCloseListener(){
            searchView.visibility = View.INVISIBLE
            searchBar.visibility = View.VISIBLE
            false
        }
    }

    private fun observeCandidate(){
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.candidateFlow, viewModel.isLoading) { list, isLoading ->
                list to isLoading
            }.collect { (list, isLoading) ->
                binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.noCandidate.visibility =
                    if (list.isEmpty() && !isLoading) View.VISIBLE else View.GONE
                candidateAdapter.submitList(list)
            }
        }
    }


    private fun tabsSelectedDisplay() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.setFavoriteTabSelected(false)
                    1 -> viewModel.setFavoriteTabSelected(true)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        }
        )
    }


}
