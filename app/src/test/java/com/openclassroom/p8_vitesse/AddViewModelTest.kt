package com.openclassroom.p8_vitesse

import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.ui.addScreen.AddViewModel
import io.mockk.clearAllMocks
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit test pour le ViewModel du fragment Add.
 */
class AddViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AddViewModel
    private lateinit var repository: CandidateRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = AddViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test avec tout les champs obligatoire vide`() = runTest {
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
    fun `test avec un email non valide`() = runTest {
        viewModel.setFirstName("test")
        viewModel.setLastName("test")
        viewModel.setPhoneNumber("0123456")
        viewModel.setDateOfBirth(946736967) // Correspond au 01/01/2000
        viewModel.setEmail("test")
        viewModel.saveCandidate()
        val state = viewModel.candidateState.value

        assert(state.emailError == R.string.emailError)
        assertFalse(state.isCandidateCorrect)
    }

    @Test
    fun `Test avec un candidat valide`() = runTest {
        viewModel.setFirstName("test")
        viewModel.setLastName("test")
        viewModel.setPhoneNumber("0123456")
        viewModel.setDateOfBirth(946736967) // Correspond au 01/01/2000
        viewModel.setEmail("james.c.mcreynolds@example-pet-store.com")
        viewModel.saveCandidate()

        val state = viewModel.candidateState.value

        assert(state.firstnameError == null)
        assert(state.lastnameError == null)
        assert(state.phoneError == null)
        assert(state.emailError == null)
        assert(state.birthdayError == null)
        assert(state.isCandidateCorrect)
    }
}