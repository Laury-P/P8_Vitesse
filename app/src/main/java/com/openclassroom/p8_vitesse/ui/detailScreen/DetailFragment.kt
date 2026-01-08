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
import com.openclassroom.p8_vitesse.ui.utils.BirthdayFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

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
        else findNavController().popBackStack() // Si une erreur survient on retourne à l'écran d'accueil
        setupBackNavigation()
        setupMenu()
        setupContactMenu()
    }

    /**
     * Récupération des infos candidat et affichage dans la vue
     *
     * @param id L'id du candidat
     */
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
                binding.TVBirthday.text = formatBirthdayDisplayString(candidate.dateOfBirth)
                candidate.expectedSalary?.let { salary ->
                    val formattedSalary = String.format("%.2f", salary)
                    binding.TVSalaryEuros.text = getString(R.string.salary_euros, formattedSalary)
                    binding.TVSalaryPounds.text = formatSalaryiInPounds(salary, viewModel.getRate())
                } ?: run {
                    binding.TVSalaryEuros.text = getString(R.string.salary_euros, "-")
                    binding.TVSalaryPounds.text = getString(R.string.salary_pounds, "-")
                }
                candidate.note?.let { note -> binding.TVNote.text = note }
            }

        }
    }

    /**
     *  Formate la date d'anniversaire d'un candidat et renvoie une chaîne prête à l'affichage
     *  incluant à la fois la date formatée selon la locale et l'âge calculé.
     *
     *  Cette fonction :
     *   - utilise `BirthdayFormatter.format(date)` pour obtenir la date au format local
     *   - calcule l'âge en années à partir de la date passée
     *   - retourne une chaîne combinant la date et l'âge, formatée via `R.string.age_format`
     *
     *   Exemple de résultat : "12/08/1990 (32 ans)" selon la locale.
     *
     *   @param date La date de naissance du candidat
     *   @return Une chaîne formatée affichant la date et l'âge du candidat
     */
    private fun formatBirthdayDisplayString(date: LocalDate): String {
        val birthday = BirthdayFormatter.format(date)

        val age = Period.between(date, LocalDate.now()).years

        return getString(R.string.age_format, birthday, age)
    }

    /**
     * Convertit le salaire attendu en livre sterling et retourne une chaîne prête à affiché
     *
     * Cette fonction :
     *   - multiplie le salaire par le taux de conversion fourni si le taux est différent de zéro
     *   - sinon retourne "-" pour indiqué une erreur de recupération du taux
     *   - format l'affichage avec 2 chiffres après la virgule
     *   - utilise `R.string.salary_pounds` pour préparer la chaîne d'affichage
     *
     *   @param euros Salaire en euros à convertir
     *   @param rate Taux de conversion euro → livre
     *   @return Chaîne affichable représentant le salaire en livres
     */
    private fun formatSalaryiInPounds(euros: Double, rate: Double): String {
        return if (rate != 0.0) {
            val pounds = euros * rate
            val formattedSalary = String.format("%.2f", pounds)
            getString(R.string.salary_pounds, formattedSalary)
        } else getString(R.string.salary_pounds, "-")
    }

    /**
     * Mise en place du bouton de retour
     */
    private fun setupBackNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Mise en place du menu
     *
     * Cette fonction:
     * - met en place les listeners pour les boutons du menu.
     * - affiche un dialog pour confirmer la suppression du candidat avant de l'effectuer si confirmé
     * - met en place la navigation vers l'écran d'édition du candidat avec l'id du candidat en argument
     */
    private fun setupMenu() {
        val menuFav = binding.toolbar.menu.findItem(R.id.menuFavorite)
        val menuEdit = binding.toolbar.menu.findItem(R.id.menuEdit)
        val menuDelete = binding.toolbar.menu.findItem(R.id.menuDelete)
        menuFav.setOnMenuItemClickListener {
            viewModel.setFavorite()
            return@setOnMenuItemClickListener true
        }
        menuEdit.setOnMenuItemClickListener {
            val id = viewModel.candidateFlow.value.candidate.id
                ?: return@setOnMenuItemClickListener false
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

    /**
     * Mise en place du menu de contact
     *
     * Cette fonction récupère le numéros de telephone et l'adresse mail du candidat et met en place
     * les boutons pour appeler, envoyer un sms ou envoyer un mail au candidat.
     */
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