package com.openclassroom.p8_vitesse.ui.detailScreen

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.databinding.FragmentDetailBinding
import com.openclassroom.p8_vitesse.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel : DetailViewModel by viewModels()

    private val CALL_PERMISSION_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong("candidateId")
        if (id != null) setupCandidate(id)
        else findNavController().popBackStack() // Erreur lors de la reception de l'intent
        // TODO : add error
        setupBackNavigation()
        setupMenu()
        setupContactMenu()
    }

    private fun setupCandidate(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCandidateById(id)
            viewModel.candidateFlow.collect {
                binding.toolbar.title = "${it.firstName} ${it.lastName.uppercase()}"
                binding.toolbar.menu.findItem(R.id.menuFavorite).setIcon(if (it.isFavorite) R.drawable.ic_is_favorite else R.drawable.ic_not_favorite)
                Glide.with(binding.profilPicture)
                    .load(it.photo)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(binding.profilPicture)
            }
        }
    }

    private fun setupBackNavigation(){
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupMenu(){
        val menuFav = binding.toolbar.menu.findItem(R.id.menuFavorite)
        val menuEdit = binding.toolbar.menu.findItem(R.id.menuEdit)
        val menuDelete = binding.toolbar.menu.findItem(R.id.menuDelete)
        menuFav.setOnMenuItemClickListener {
            viewModel.setFavorite()
            return@setOnMenuItemClickListener true
        }
        menuEdit.setOnMenuItemClickListener {
            // TODO : add edit
            return@setOnMenuItemClickListener true
        }
        menuDelete.setOnMenuItemClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.delete_dialogue_title)
            builder.setMessage(R.string.delete_confirmation)
            builder.setPositiveButton(R.string.delete_confirm_button) {_, _ ->
                viewModel.deleteCandidate()
                findNavController().popBackStack()
            }
            builder.setNegativeButton(R.string.delete_denied_button, null)
            val dialog = builder.create()
            dialog.show()
            return@setOnMenuItemClickListener true
        }
    }

    private fun setupContactMenu(){
        binding.callButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:${viewModel.candidateFlow.value.phoneNumber}"))
            startActivity(intent)
        }
    }






}