package com.openclassroom.p8_vitesse.ui.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
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
        setupAddFab() // Mise en place du bouton pour ajouter un candidat
        setupSearchWidget()// Mise en place du widget de recherche/filtrage de candidat par nom ou prénom
        observeSelectedTabs()// Mise en place des onglets pour afficher les candidats favoris ou non
        setupTabsListener()
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
    private fun setupAddFab() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_add)
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

        searchView.setOnCloseListener {
            searchView.visibility = View.INVISIBLE
            searchBar.visibility = View.VISIBLE
            false
        }
    }

    /**
     * Observe le flux de candidats provenant de la BDD via le ViewModel
     *
     * Cette fonction:
     * - Gère l'affichage en fonction de l'état du flux : le chargement, une liste vide,
     * une liste non vide transmisse à l'adapter du RecyclerView ou une erreur via un Toast
     */
    private fun observeCandidate() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.candidateFlow.collect { result ->
                binding.loadingBar.visibility =
                    if (result is CandidateResult.Loading) View.VISIBLE else View.GONE
                binding.noCandidate.visibility =
                    if (result is CandidateResult.Success && result.candidates.isEmpty()) View.VISIBLE else View.GONE

                when (result) {
                    is CandidateResult.Success -> candidateAdapter.submitList(result.candidates)
                    is CandidateResult.Error -> {
                        Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {}
                }
            }
        }

    }

    /**
     * Mise en place du listener pour la selection des onglets All et Favorite
     */
    private fun setupTabsListener() {
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

    /**
     * Mise en place des onglets pour afficher les candidats favoris ou non
     *
     * S'assure qu'au repositionnement de l'écran ou retour depuis un autre écran, le bon onglet est
     * toujours sélectionné
     */
    private fun observeSelectedTabs() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavoriteSelected.collect { isFavoriteSelected ->
                val expectedPosition = if (isFavoriteSelected) 1 else 0
                val currentPosition = binding.tabs.selectedTabPosition
                if (currentPosition != expectedPosition) binding.tabs.getTabAt(expectedPosition)
                    ?.select()
            }
        }


    }


}
