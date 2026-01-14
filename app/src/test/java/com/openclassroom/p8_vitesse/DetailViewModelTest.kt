package com.openclassroom.p8_vitesse

import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import com.openclassroom.p8_vitesse.ui.detailScreen.DetailViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
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
 * Tests unitaires du [DetailViewModel]
 *
 * Ces tests vérifient :
 * - la récupération des informations du candidat par son identifiant
 * - la mise a jours du statut de favoris
 * - la suppression du candidat
 */
class DetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DetailViewModel
    private lateinit var repository: CandidateRepository

    private val fakeCandidate = Candidate(
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = DetailViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `given un id existant when getCandidateById then state est mis à jour avec les infos du candidat`() = runTest {
        coEvery { repository.getCandidateById(1) } returns fakeCandidate
        viewModel.getCandidateById(1)

        val state = viewModel.candidateFlow.value
        assert(state.candidate == fakeCandidate)
    }

    @Test
    fun `given isFavorite est true when setFavoris then state est mis à jour avec isFavorite = false`() = runTest {
        val candidate = fakeCandidate.copy(isFavorite = true)
        coEvery { repository.getCandidateById(any()) } returns candidate
        viewModel.getCandidateById(1)
        viewModel.setFavorite()
        val state = viewModel.candidateFlow.value
        assertFalse(state.candidate.isFavorite)
    }

    @Test
    fun `given isFavorite est false when setFavoris then state est mis à jour avec isFavorite = true`() = runTest {
        val candidate = fakeCandidate.copy(isFavorite = false)
        coEvery { repository.getCandidateById(any()) } returns candidate
        viewModel.getCandidateById(1)
        viewModel.setFavorite()
        val state = viewModel.candidateFlow.value
        assertTrue(state.candidate.isFavorite)
    }

    @Test
    fun `when deleteCandidate then repository deleteCandidate est appelé`() = runTest {
        coEvery { repository.getCandidateById(any()) } returns fakeCandidate
        viewModel.getCandidateById(1)
        val state = viewModel.candidateFlow.value
        assert(state.candidate == fakeCandidate)
        viewModel.deleteCandidate()
        advanceUntilIdle()
        coVerify { repository.deleteCandidate(fakeCandidate) }
    }
}