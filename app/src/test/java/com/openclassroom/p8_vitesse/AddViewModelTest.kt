package com.openclassroom.p8_vitesse

import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import com.openclassroom.p8_vitesse.ui.addScreen.AddViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Tests unitaires du [AddViewModel]
 *
 * Ces tests vérifient :
 * - la récupération du candidat par son identifiant
 * - la validation des champs du formulaire: champs obligatoire et email invalide
 * - la sauvegarde d'un candidat valide
 *
 * Les dépendances sont simulées à l'aide de MockK
 * et les coroutines sont testées avec turbine et un dispatchr de test.
 */
class AddViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AddViewModel
    private lateinit var repository: CandidateRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = AddViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `given un id existant when getCandidate then state est mis à jour avec les informations du candidat`() =
        runTest {
            val fakeCandidate = Candidate(
                id = 1,
                photo = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                firstName = "Hugo",
                lastName = "Bernard",
                note = "Bonne maîtrise technique. Le candidat a exprimé un intérêt particulier pour le développement mobile.",
                isFavorite = false,
                phoneNumber = "0655443322",
                email = "hugo.bernard@example.com",
                dateOfBirth = LocalDate.of(1987, 7, 14),
                expectedSalary = 45000.0
            )
            coEvery { repository.getCandidateById(1) } returns fakeCandidate
            viewModel.getCandidate(1)

            val state = viewModel.candidateState.value.candidate
            assert(state == fakeCandidate)

        }

    @Test
    fun `given champs obligatoire vides when saveCandidate then state est mis a jour avec erreurs champs obligatoire`() =
        runTest {
            viewModel.saveCandidate()
            val state = viewModel.candidateState.value

            assert(state.firstnameError == R.string.mandatoryField)
            assert(state.lastnameError == R.string.mandatoryField)
            assert(state.phoneError == R.string.mandatoryField)
            assert(state.emailError == R.string.mandatoryField)
            assert(state.birthdayError == R.string.mandatoryField)
            assertFalse(state.isCandidateCorrect)
        }

    @Test
    fun `given un email invalide when saveCandidate then state est mis à jour avec erreur mail invalid`() =
        runTest {
            viewModel.setFirstName("test")
            viewModel.setLastName("test")
            viewModel.setPhoneNumber("0123456")
            viewModel.setDateOfBirth(LocalDate.of(2000, 1, 1)) // Correspond au 01/01/2000
            viewModel.setEmail("test")
            viewModel.saveCandidate()
            val state = viewModel.candidateState.value

            assert(state.emailError == R.string.emailError)
            assertFalse(state.isCandidateCorrect)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given un candidat valide when saveCandidate then state est mis à jour candidat correct et le candidat est ajouter à la BDD`() =
        runTest {
            viewModel.setFirstName("test")
            viewModel.setLastName("test")
            viewModel.setPhoneNumber("0123456")
            viewModel.setDateOfBirth(LocalDate.of(2000, 1, 1)) // Correspond au 01/01/2000
            viewModel.setEmail("james.c.mcreynolds@example-pet-store.com")
            viewModel.saveCandidate()
            advanceUntilIdle()

            val state = viewModel.candidateState.value

            assert(state.firstnameError == null)
            assert(state.lastnameError == null)
            assert(state.phoneError == null)
            assert(state.emailError == null)
            assert(state.birthdayError == null)
            assert(state.isCandidateCorrect)
            coVerify { repository.upsertCandidate(any()) }
        }
}