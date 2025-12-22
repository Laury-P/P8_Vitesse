package com.openclassroom.p8_vitesse.ui.detailScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

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
        else findNavController().popBackStack() // If any error occur during intent or id is lost or invalid
        setupBackNavigation()
        setupMenu()
        setupContactMenu()
    }

    private fun setupCandidate(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCandidateById(id)
            viewModel.candidateFlow.collect {
                val candidate = it.candidate
                binding.toolbar.title = "${candidate.firstName} ${candidate.lastName.uppercase()}"
                binding.toolbar.menu.findItem(R.id.menuFavorite)
                    .setIcon(if (candidate.isFavorite) R.drawable.ic_is_favorite else R.drawable.ic_not_favorite)
                Glide.with(binding.profilPicture)
                    .load(candidate.photo)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(binding.profilPicture)
                binding.TVBirthday.text = birthdayFormatter(candidate.dateOfBirth)
                candidate.expectedSalary?.let { salary ->
                    val formattedSalary = String.format("%.2f", salary)
                    binding.TVSalaryEuros.text = getString(R.string.salary_euros, formattedSalary)
                    binding.TVSalaryPounds.text = salaryConverter(salary)
                } ?: run {
                    binding.TVSalaryEuros.text = getString(R.string.salary_euros, "-")
                    binding.TVSalaryPounds.text = getString(R.string.salary_pounds, "-")
                }
                candidate.note?.let { note -> binding.TVNote.text = note }
            }

        }
    }


    private fun birthdayFormatter(date: LocalDate): String {
        val local = Locale.getDefault()
        val pattern = when (local.language) {
            Locale.FRENCH.language -> "dd/MM/yyyy"
            else -> "MM/dd/yyyy"
        }
        val birthday = date.format(DateTimeFormatter.ofPattern(pattern))

        val age = Period.between(date, LocalDate.now()).years

        return getString(R.string.age_format, birthday, age)
    }

    private fun salaryConverter(euros: Double): String {
        val pounds = euros * 0.8782
        val formattedSalary = String.format("%.2f", pounds)
        return getString(R.string.salary_pounds, formattedSalary)
    }

    private fun setupBackNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupMenu() {
        val menuFav = binding.toolbar.menu.findItem(R.id.menuFavorite)
        val menuEdit = binding.toolbar.menu.findItem(R.id.menuEdit)
        val menuDelete = binding.toolbar.menu.findItem(R.id.menuDelete)
        menuFav.setOnMenuItemClickListener {
            viewModel.setFavorite()
            return@setOnMenuItemClickListener true
        }
        menuEdit.setOnMenuItemClickListener {
            val id = viewModel.candidateFlow.value.candidate.id ?: return@setOnMenuItemClickListener false
            val arguments = Bundle().apply { putLong("candidateId", id) }
            findNavController().navigate(R.id.action_detail_to_add, arguments)
            return@setOnMenuItemClickListener true
        }
        menuDelete.setOnMenuItemClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.delete_dialogue_title)
            builder.setMessage(R.string.delete_confirmation)
            builder.setPositiveButton(R.string.delete_confirm_button) { _, _ ->
                viewModel.deleteCandidate()
                findNavController().popBackStack()
            }
            builder.setNegativeButton(R.string.delete_denied_button, null)
            val dialog = builder.create()
            dialog.show()
            return@setOnMenuItemClickListener true
        }
    }

    private fun setupContactMenu() {
        binding.callButton.setOnClickListener {
            val number = viewModel.candidateFlow.value.candidate.phoneNumber
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:${number}"))
            startActivity(intent)
        }
        binding.smsButton.setOnClickListener {
            val number = viewModel.candidateFlow.value.candidate.phoneNumber
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("smsto:${number}"))
            startActivity(intent)
        }
        binding.emailButton.setOnClickListener {
            val mail = viewModel.candidateFlow.value.candidate.email
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:${mail}"))
            startActivity(intent)
        }
    }


}