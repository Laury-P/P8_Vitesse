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
        setupRecyclerView() // Mise en place du recycler view qui affiche la liste de candidat
        setupFab() // Mise en place du bouton pour ajouter un candidat
        setupSearchWidget() // Mise en place du widget de recherche/filtrage de candidat par nom ou prénom
        tabsSelectedDisplay() // Mise en place des onglets pour afficher les candidats favoris ou non
        observeCandidate() // Lancement de l'observateur des listes de candidats venant de la BDD
    }

    /**
     * Mise en place du recycler view qui affiche la liste de candidat
     */
    private fun setupRecyclerView() {
        candidateAdapter = CandidateAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = candidateAdapter
        }
    }

    /**
     * Mise en place du bouton pour ajouter un candidat et navigation vers le fragment d'ajout
     */
    private fun setupFab() {
        binding.fab.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_to_addFragment)
        }
    }

    /**
     * Mise en place du widget de recherche/filtrage de candidat par nom ou prénom
     * Un clic sur la searchBar permet d'afficher et activer le widget de recherche via la searchView
     * Une fois la recherche fini la SearchBar redevient visible et la recherche est désactivée
     */
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

        searchView.setOnCloseListener() {
            searchView.visibility = View.INVISIBLE
            searchBar.visibility = View.VISIBLE
            false
        }
    }

    /**
     * Lancement de l'observateur des listes de candidats venant de la BDD
     * Gère l'affichage du chargement, l'affichage indiquant une liste vide et affiche la mise à jours de la liste des candidats
     */
    private fun observeCandidate() {
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

    /**
     * Mise en place des onglets pour afficher les candidats favoris ou non
     */
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
