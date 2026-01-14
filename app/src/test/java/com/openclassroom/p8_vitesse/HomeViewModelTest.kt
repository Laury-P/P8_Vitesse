package com.openclassroom.p8_vitesse

import app.cash.turbine.test
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import com.openclassroom.p8_vitesse.ui.homeScreen.CandidateResult
import com.openclassroom.p8_vitesse.ui.homeScreen.HomeViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Tests unitaires du [HomeViewModel]
 *
 * Ces tests vérifient :
 * - La transmission des listes de candidat en fonction de l'onglet selecionné (all ou favorite) et
 *   du filtre appliqué.
 * - La transmission d'une liste vide si aucun candidat n'existe en BDD.
 *
 * Les dépendances sont simulées via des Mocks.
 * Les coroutines sont testées via des TestDispatchers.
 */
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: CandidateRepository

    private val allCandidateFakeList = listOf(
        Candidate(
            id = null,
            photo = "https://images.unsplash.com/photo-1502685104226-ee32379fefbe",
            firstName = "Emma",
            lastName = "Durand",
            note = "Candidate très motivée, a démontré une excellente capacité d’adaptation durant ses projets précédents.",
            isFavorite = false,
            phoneNumber = "0601020304",
            email = "emma.durand@example.com",
            dateOfBirth = LocalDate.of(1995, 4, 12),
            expectedSalary = 38000.0
        ),
        Candidate(
            id = null,
            photo = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e",
            firstName = "Lucas",
            lastName = "Martin",
            note = "Bon communicant, esprit d’équipe marqué. Devrait encore progresser sur la gestion de projet.",
            isFavorite = true,
            phoneNumber = "0611223344",
            email = "lucas.martin@example.com",
            dateOfBirth = LocalDate.of(1990, 9, 2),
            expectedSalary = 42000.0
        ),
        Candidate(
            id = null,
            photo = null,
            firstName = "Sarah",
            lastName = "Leclerc",
            note = "Profil autodidacte avec beaucoup d’initiatives. Son absence de photo servira pour tester les cas sans visuel.",
            isFavorite = false,
            phoneNumber = "0677889900",
            email = "sarah.leclerc@example.com",
            dateOfBirth = LocalDate.of(1998, 1, 30),
            expectedSalary = null
        ),
        Candidate(
            id = null,
            photo = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
            firstName = "Hugo",
            lastName = "Bernard",
            note = "Bonne maîtrise technique. Le candidat a exprimé un intérêt particulier pour le développement mobile.",
            isFavorite = false,
            phoneNumber = "0655443322",
            email = "hugo.bernard@example.com",
            dateOfBirth = LocalDate.of(1987, 7, 14),
            expectedSalary = 45000.0
        ),
        Candidate(
            id = null,
            photo = "https://images.unsplash.com/photo-1520813792240-56fc4a3765a7",
            firstName = "Julie",
            lastName = "Moreau",
            note = "Présentation très soignée, excellente capacité d’analyse. Son sens critique peut être précieux dans une équipe.",
            isFavorite = true,
            phoneNumber = "0622334455",
            email = "julie.moreau@example.com",
            dateOfBirth = LocalDate.of(1993, 3, 21),
            expectedSalary = null
        ),
        Candidate(
            id = null,
            photo = null,
            firstName = "Mehdi",
            lastName = "Karim",
            note = "Apporte une vision différente grâce à un parcours atypique. Son expérience variée enrichira les discussions techniques.",
            isFavorite = false,
            phoneNumber = "0644556677",
            email = "mehdi.karim@example.com",
            dateOfBirth = LocalDate.of(1985, 11, 8),
            expectedSalary = 39000.0
        ),
        Candidate(
            id = null,
            photo = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91",
            firstName = "Anaïs",
            lastName = "Renault",
            note = "Très créative, propose souvent des idées innovantes. Quelques lacunes en architecture logicielle mais forte évolution.",
            isFavorite = false,
            phoneNumber = "0677001122",
            email = "anais.renault@example.com",
            dateOfBirth = LocalDate.of(1996, 6, 5),
            expectedSalary = 36000.0
        ),
        Candidate(
            id = null,
            photo = null,
            firstName = "Thomas",
            lastName = "Lemoine",
            note = "Très autonome. Son profil sans photo permet de tester plusieurs cas limites dans ton UI.",
            isFavorite = true,
            phoneNumber = "0633557799",
            email = "thomas.lemoine@example.com",
            dateOfBirth = LocalDate.of(1992, 12, 17),
            expectedSalary = 41000.0
        )
    )

    private val favoriteCandidateFakeList = listOf(
        Candidate(
            id = null,
            photo = null,
            firstName = "Thomas",
            lastName = "Lemoine",
            note = "Très autonome. Son profil sans photo permet de tester plusieurs cas limites dans ton UI.",
            isFavorite = true,
            phoneNumber = "0633557799",
            email = "thomas.lemoine@example.com",
            dateOfBirth = LocalDate.of(1992, 12, 17),
            expectedSalary = 41000.0
        ),
        Candidate(
            id = null,
            photo = "https://images.unsplash.com/photo-1520813792240-56fc4a3765a7",
            firstName = "Julie",
            lastName = "Moreau",
            note = "Présentation très soignée, excellente capacité d’analyse. Son sens critique peut être précieux dans une équipe.",
            isFavorite = true,
            phoneNumber = "0622334455",
            email = "julie.moreau@example.com",
            dateOfBirth = LocalDate.of(1993, 3, 21),
            expectedSalary = null
        ),
        Candidate(
            id = null,
            photo = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e",
            firstName = "Lucas",
            lastName = "Martin",
            note = "Bon communicant, esprit d’équipe marqué. Devrait encore progresser sur la gestion de projet.",
            isFavorite = true,
            phoneNumber = "0611223344",
            email = "lucas.martin@example.com",
            dateOfBirth = LocalDate.of(1990, 9, 2),
            expectedSalary = 42000.0
        )
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
    fun `given aucun filtre et onglet all selectionné then candidateFlow retourne allCandidateList`() =
        runTest {
            coEvery { repository.getAllCandidates() } returns flowOf(allCandidateFakeList)
            coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateFakeList)
            viewModel = HomeViewModel(repository)
            viewModel.setFilter("")
            viewModel.setFavoriteTabSelected(false)

            viewModel.candidateFlow.test {
                val loading = awaitItem()
                assert(loading is CandidateResult.Loading)
                val result = awaitItem()
                assert((result as CandidateResult.Success).candidates == allCandidateFakeList)

                cancelAndConsumeRemainingEvents()
            }


        }

    @Test
    fun `given un filtre et onglet all selectionné then candidateFlow retourne les candidats filtrés`() =
        runTest {
            coEvery { repository.getAllCandidates() } returns flowOf(allCandidateFakeList)
            coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateFakeList)
            viewModel = HomeViewModel(repository)
            viewModel.setFilter("le")
            viewModel.setFavoriteTabSelected(false)

            viewModel.candidateFlow.test {
                val loading = awaitItem()
                assert(loading is CandidateResult.Loading)
                val result = awaitItem()
                assert(result is CandidateResult.Success)
                assert((result as CandidateResult.Success).candidates.size == 2)
                assert(result.candidates[0].lastName == "Leclerc")
                assert(result.candidates[1].lastName == "Lemoine")

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `given aucun filtre et onglet favorite selectionné then candidateFlow retourne favoriteCandidateList`() =
        runTest {
            coEvery { repository.getAllCandidates() } returns flowOf(allCandidateFakeList)
            coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateFakeList)
            viewModel = HomeViewModel(repository)
            viewModel.setFilter("")
            viewModel.setFavoriteTabSelected(true)

            viewModel.candidateFlow.test {
                val loading = awaitItem()
                assert(loading is CandidateResult.Loading)
                val result = awaitItem()
                assert((result as CandidateResult.Success).candidates == favoriteCandidateFakeList)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `given un filtre et onglet favorite selectionné then candidateFlow retourne les candidats favoris filtrés`() =
        runTest {
            coEvery { repository.getAllCandidates() } returns flowOf(allCandidateFakeList)
            coEvery { repository.getFavoriteCandidates() } returns flowOf(favoriteCandidateFakeList)
            viewModel = HomeViewModel(repository)
            viewModel.setFilter("le")
            viewModel.setFavoriteTabSelected(true)

            viewModel.candidateFlow.test {
                val loading = awaitItem()
                assert(loading is CandidateResult.Loading)
                val result = awaitItem()
                assert((result as CandidateResult.Success).candidates.size == 1)
                assert(result.candidates[0].lastName == "Lemoine")

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `given aucun candidat then candidateFlow retourne une liste vide`() = runTest {
        coEvery { repository.getAllCandidates() } returns flowOf(emptyList())
        coEvery { repository.getFavoriteCandidates() } returns flowOf(emptyList())
        viewModel = HomeViewModel(repository)
        viewModel.setFilter("")
        viewModel.setFavoriteTabSelected(false)

        viewModel.candidateFlow.test {
            val loading = awaitItem()
            assert(loading is CandidateResult.Loading)
            val result = awaitItem()
            assert((result as CandidateResult.Success).candidates == emptyList<Candidate>())
        }

    }
}