package com.openclassroom.p8_vitesse.ui.addScreen

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.databinding.FragmentAddBinding
import com.openclassroom.p8_vitesse.ui.utils.BirthdayFormatter
import com.openclassroom.p8_vitesse.ui.utils.PhoneFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong("candidateId") ?: -1L // If null then become the default value
        if (id != -1L) {
            setupEditMode(id)
        }
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
        Glide.with(binding.profilPicture.context)
            .load(uri)
            .centerCrop()
            .into(binding.profilPicture)
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

        binding.ETBirthay.text?.let {
            val date = viewModel.candidateState.value.candidate.dateOfBirth
            datePickerBuilder.setSelection(
                date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
        }

        val datePicker = datePickerBuilder.build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            val longBirthday = datePicker.selection ?: return@addOnPositiveButtonClickListener
            val birthday = Instant.ofEpochMilli(longBirthday).atZone(ZoneId.systemDefault())
                .toLocalDate()
            binding.ETBirthay.setText(BirthdayFormatter.format(birthday))
            viewModel.setDateOfBirth(birthday)
        }
    }

    private fun setupFab() {
        binding.fabSave.setOnClickListener {
            viewModel.saveCandidate()
        }
    }

    private fun setupEditMode(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCandidate(id)
            val candidate = viewModel.candidateState.value.candidate
            binding.toolbar.title = getString(R.string.edit_candidate)
            binding.ETFirstname.setText(candidate.firstName)
            binding.ETLastname.setText(candidate.lastName)
            binding.ETPhone.setText(candidate.phoneNumber)
            binding.ETEmail.setText(candidate.email)
            candidate.note?.let { note -> binding.ETNote.setText(note) }
            candidate.expectedSalary?.let { salary -> binding.ETSalary.setText(salary.toString()) }
            if (candidate.dateOfBirth != LocalDate.now()) binding.ETBirthay.setText(BirthdayFormatter.format(candidate.dateOfBirth))
            Glide.with(binding.profilPicture.context)
                .load(candidate.photo)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .into(binding.profilPicture)
        }
    }

    private fun setupCandidate() {
        binding.ETFirstname.addTextChangedListener {
            viewModel.setFirstName(it.toString())
        }
        binding.ETLastname.addTextChangedListener {
            viewModel.setLastName(it.toString())
        }
        binding.ETPhone.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
                val formatted = PhoneFormatter.format(s.toString())

                if (formatted != current) {
                    current = formatted
                    binding.ETPhone.setText(formatted)
                    binding.ETPhone.setSelection(formatted.length)
                }

                viewModel.setPhoneNumber(current.filter { it.isDigit() })
            }
        })

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