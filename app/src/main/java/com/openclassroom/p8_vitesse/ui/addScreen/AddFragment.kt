package com.openclassroom.p8_vitesse.ui.addScreen

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.databinding.FragmentAddBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddViewModel by viewModels()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                setProfilPicture(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goBack()
        setImagePicker()
    }

    private fun goBack() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Lancement de pickMedia pour que l'utilisateur puissent choisir la photo a utiliser pour le candidat
     */
    private fun setImagePicker(){
        binding.profilPicture.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    /**
     * Mise à jour de la photo de profil
     */
    private fun setProfilPicture(uri: Uri) {
        viewModel.setPhoto(uri.toString())
        binding.profilPicture.setImageURI(uri)
    }



}