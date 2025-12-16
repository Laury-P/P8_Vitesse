package com.openclassroom.p8_vitesse.ui.addScreen

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.databinding.FragmentAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        setupImagePicker()
        setupDatePicker()
        setupFab()
        setupCandidate()
        setupCandidateStateCollector()
    }

    private fun goBack() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Lancement de pickMedia pour que l'utilisateur puissent choisir la photo a utiliser pour le candidat
     */
    private fun setupImagePicker() {
        binding.profilPicture.setOnClickListener {
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

    private fun setupDatePicker() {
        binding.ETBirthay.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now()) //empêche l'utilisateur de saisir des dates future
            .build()

        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
        datePickerBuilder.setTitleText(R.string.hint_datePicker)

        val datePicker = datePickerBuilder.build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            binding.ETBirthay.setText(datePicker.headerText)
            datePicker.selection?.let { it -> viewModel.setDateOfBirth(it) }
        }
    }

    private fun setupFab() {
        binding.fabSave.setOnClickListener {
            viewModel.saveCandidate()
        }
    }

    private fun setupCandidate() {
        binding.ETFirstname.addTextChangedListener {
            viewModel.setFirstName(it.toString())
        }
        binding.ETLastname.addTextChangedListener {
            viewModel.setLastName(it.toString())
        }
        binding.ETPhone.addTextChangedListener {
            viewModel.setPhoneNumber(it.toString())
        }
        binding.ETEmail.addTextChangedListener {
            viewModel.setEmail(it.toString())
        }
        binding.ETNote.addTextChangedListener {
            viewModel.setNote(it.toString())
        }
        binding.ETSalary.addTextChangedListener {
            viewModel.setExpectedSalary(it.toString())
        }
    }

    private fun setupCandidateStateCollector() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.candidateState.collect {
                binding.firstnameOutline.error =
                    it.firstnameError?.let { error -> getString(error) }
                binding.lastnameOutline.error = it.lastnameError?.let { error -> getString(error) }
                binding.phoneOutline.error = it.phoneError?.let { error -> getString(error) }
                binding.birthdayOutline.error = it.birthdayError?.let { error -> getString(error) }
                binding.emailOutline.error = it.emailError?.let { error -> getString(error) }

                if (it.isCandidateCorrect) findNavController().popBackStack()
            }
        }

    }


}