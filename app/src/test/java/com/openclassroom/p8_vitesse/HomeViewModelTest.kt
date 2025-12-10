package com.openclassroom.p8_vitesse

import app.cash.turbine.test
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import com.openclassroom.p8_vitesse.ui.homeScreen.HomeViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Unit test pour le ViewModel du fragment Home.
 */
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: CandidateRepository

    private val allCandidateList = listOf(
        Candidate(id = null,photo = "https://images.unsplash.com/photo-1502685104226-ee32379fefbe", firstName = "Emma", lastName = "Durand", note = "Candidate très motivée, a démontré une excellente capacité d’adaptation durant ses projets précédents.", isFavorite = false, phoneNumber = "0601020304", email = "emma.durand@example.com", dateOfBirth = LocalDate.of(1995, 4, 12), expectedSalary = 38000.0),
        Candidate(id = null, photo = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e", firstName = "Lucas", lastName = "Martin", note = "Bon communicant, esprit d’équipe marqué. Devrait encore progresser sur la gestion de projet.", isFavorite = true, phoneNumber = "0611223344", email = "lucas.martin@example.com", dateOfBirth = LocalDate.of(1990, 9, 2), expectedSalary = 42000.0),
        Candidate(id = null, photo = null, firstName = "Sarah", lastName = "Leclerc", note = "Profil autodidacte avec beaucoup d’initiatives. Son absence de photo servira pour tester les cas sans visuel.", isFavorite = false, phoneNumber = "0677889900", email = "sarah.leclerc@example.com", dateOfBirth = LocalDate.of(1998, 1, 30), expectedSalary = null),
        Candidate(id = null, photo = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e", firstName = "Hugo", lastName = "Bernard", note = "Bonne maîtrise technique. Le candidat a exprimé un intérêt particulier pour le développement mobile.", isFavorite = false, phoneNumber = "0655443322", email = "hugo.bernard@example.com", dateOfBirth = LocalDate.of(1987, 7, 14), expectedSalary = 45000.0),
        Candidate(id = null, photo = "https://images.unsplash.com/photo-1520813792240-56fc4a3765a7", firstName = "Julie", lastName = "Moreau", note = "Présentation très soignée, excellente capacité d’analyse. Son sens critique peut être précieux dans une équipe.", isFavorite = true, phoneNumber = "0622334455", email = "julie.moreau@example.com", dateOfBirth = LocalDate.of(1993, 3, 21), expectedSalary = null),
        Candidate(id = null, photo = null, firstName = "Mehdi", lastName = "Karim", note = "Apporte une vision différente grâce à un parcours atypique. Son expérience variée enrichira les discussions techniques.", isFavorite = false, phoneNumber = "0644556677", email = "mehdi.karim@example.com", dateOfBirth = LocalDate.of(1985, 11, 8), expectedSalary = 39000.0),
        Candidate(id = null, photo = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91", firstName = "Anaïs", lastName = "Renault", note = "Très créative, propose souvent des idées innovantes. Quelques lacunes en architecture logicielle mais forte évolution.", isFavorite = false, phoneNumber = "0677001122", email = "anais.renault@example.com", dateOfBirth = LocalDate.of(1996, 6, 5), expectedSalary = 36000.0),
        Candidate(id = null, photo = null, firstName = "Thomas", lastName = "Lemoine", note = "Très autonome. Son profil sans photo permet de tester plusieurs cas limites dans ton UI.", isFavorite = true, phoneNumber = "0633557799", email = "thomas.lemoine@example.com", dateOfBirth = LocalDate.of(1992, 12, 17), expectedSalary = 41000.0)
    )

    private val favoriteCandidateList = listOf(
        Candidate(id = null, photo = null, firstName = "Thomas", lastName = "Lemoine", note = "Très autonome. Son profil sans photo permet de tester plusieurs cas limites dans ton UI.", isFavorite = true, phoneNumber = "0633557799", email = "thomas.lemoine@example.com", dateOfBirth = LocalDate.of(1992, 12, 17), expectedSalary = 41000.0),
        Candidate(id = null, photo = "https://images.unsplash.com/photo-1520813792240-56fc4a3765a7", firstName = "Julie", lastName = "Moreau", note = "Présentation très soignée, excellente capacité d’analyse. Son sens critique peut être précieux dans une équipe.", isFavorite = true, phoneNumber = "0622334455", email = "julie.moreau@example.com", dateOfBirth = LocalDate.of(1993, 3, 21), expectedSalary = null),
        Candidate(id = null, photo = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e", firstName = "Lucas", lastName = "Martin", note = "Bon communicant, esprit d’équipe marqué. Devrait encore progresser sur la gestion de projet.", isFavorite = true, phoneNumber = "0611223344", email = "lucas.martin@example.com", dateOfBirth = LocalDate.of(1990, 9, 2), expectedSalary = 42000.0)
    )
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }



    @Test
    fun `test chargement initial de tous les candidats`() = runTest {
        coEvery { repository.getAllCandidates() } returns flowOf(allCandidateList)
        coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateList)
        viewModel = HomeViewModel(repository)
        viewModel.setFilter("")
        viewModel.setFavoriteTabSelected(false)

        viewModel.candidateFlow.test{
            val result = awaitItem()
            assert(result == allCandidateList)

            cancelAndConsumeRemainingEvents()
        }


    }

    @Test
    fun `test filtrage des candidats`() = runTest {
        coEvery { repository.getAllCandidates() } returns flowOf(allCandidateList)
        coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateList)
        viewModel = HomeViewModel(repository)
        viewModel.setFilter("le")
        viewModel.setFavoriteTabSelected(false)

        viewModel.candidateFlow.test{
            val result = awaitItem()
            assert(result.size == 2)
            assert(result[0].lastName == "Leclerc")
            assert(result[1].lastName == "Lemoine")

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test affichage des favoris`() = runTest {
        coEvery { repository.getAllCandidates() } returns flowOf(allCandidateList)
        coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateList)
        viewModel = HomeViewModel(repository)
        viewModel.setFilter("")
        viewModel.setFavoriteTabSelected(true)

        viewModel.candidateFlow.test{
            val result = awaitItem()
            assert(result == favoriteCandidateList)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test affichage des favoris filtrer`() = runTest {
        coEvery { repository.getAllCandidates() } returns flowOf(allCandidateList)
        coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateList)
        viewModel = HomeViewModel(repository)
        viewModel.setFilter("le")
        viewModel.setFavoriteTabSelected(true)

        viewModel.candidateFlow.test{
            val result = awaitItem()
            assert(result.size == 1)
            assert(result[0].lastName == "Lemoine")

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test reception d'une liste vide`() = runTest {
        coEvery { repository.getAllCandidates() } returns flowOf(emptyList())
        coEvery { repository.getFavoriteCandidates() } returns flowOf(emptyList())
        viewModel = HomeViewModel(repository)
        viewModel.setFilter("")
        viewModel.setFavoriteTabSelected(false)

        viewModel.candidateFlow.test {
            val result = awaitItem()
            assert(result == emptyList<Candidate>())
        }

    }
}