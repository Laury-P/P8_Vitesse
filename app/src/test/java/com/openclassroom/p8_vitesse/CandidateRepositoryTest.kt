package com.openclassroom.p8_vitesse

import com.openclassroom.p8_vitesse.data.local.dao.CandidateDao
import com.openclassroom.p8_vitesse.data.remote.api.ApiService
import com.openclassroom.p8_vitesse.data.remote.response.EurToPoundsResponse
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class CandidateRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var candidateDao: CandidateDao
    private lateinit var apiService: ApiService
    private lateinit var repository: CandidateRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        candidateDao = mockk(relaxed = true)
        apiService = mockk()
        repository = CandidateRepository(candidateDao, apiService)

        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test getEurToPoundsRate() with correct rate`() = runTest {
        val fakeRate = 0.8
        coEvery { apiService.getEuroToPoundsRate() } returns EurToPoundsResponse(
            EurToPoundsResponse.EurRate(
                fakeRate
            )
        )
        val rate = repository.getEurToPoundsRate()

        assert(rate == fakeRate)

    }

    @Test
    fun `test getEurToPoundsRate() with error`() = runTest {
        coEvery { apiService.getEuroToPoundsRate() } throws Exception("API Failed")

        val rate = repository.getEurToPoundsRate()

        assert(rate == 0.0)

    }


}