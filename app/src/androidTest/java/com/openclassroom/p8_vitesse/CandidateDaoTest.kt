package com.openclassroom.p8_vitesse

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.p8_vitesse.data.VitesseDatabase
import com.openclassroom.p8_vitesse.data.dao.CandidateDao
import com.openclassroom.p8_vitesse.data.mapper.toDto
import com.openclassroom.p8_vitesse.domain.Candidate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CandidateDaoTest {
    private lateinit var database: VitesseDatabase
    private lateinit var candidateDao : CandidateDao

    @Before
    fun createDb() {
        database = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                VitesseDatabase::class.java
            ).allowMainThreadQueries()
            .build()

        candidateDao = database.candidateDao()
    }

    @After
    fun closeDb(){
        database.close()
    }


    @Test
    fun insertCandidateWithOnlyRequiredFields() = runBlocking {
        val candidate = Candidate(
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john.doe@exemple.com",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        candidateDao.upsertCandidate(candidate.toDto())


        val insertedCandidate = candidateDao.getAllCandidates().first()
        val expected = candidate.toDto().copy(id = 1)

        assertEquals(expected,insertedCandidate[0])

    }

    @Test
    fun insertCandidateWithOptionalFields() = runBlocking {
        val candidate = Candidate(
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john.doe@exemple.com",
            dateOfBirth = LocalDate.of(1990, 1, 1),
            note = "This is a note",
            isFavorite = true,
            expectedSalary = 50000.0
        )

        candidateDao.upsertCandidate(candidate.toDto())


        val insertedCandidate = candidateDao.getAllCandidates().first()
        val expected = candidate.toDto().copy(id = 1)

        assertEquals(expected,insertedCandidate[0])
    }

    @Test
    fun updateCandidate() = runBlocking {
        val candidate = Candidate(
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john.doe@exemple.com",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )
        candidateDao.upsertCandidate(candidate.toDto())

        val updatedCandidate = candidate.copy(
            id = 1,
            phoneNumber = "987654321",
            isFavorite = true,
            expectedSalary = 60000.0
        )
        candidateDao.upsertCandidate(updatedCandidate.toDto())

        val insertedCandidate = candidateDao.getAllCandidates().first()
        val expected = updatedCandidate.toDto()

        assertEquals(expected,insertedCandidate[0])
        assert(insertedCandidate.size == 1)
    }

    @Test
    fun deleteCandidate() = runBlocking {
        val candidate = Candidate(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john.doe@exemple.com",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )
        candidateDao.upsertCandidate(candidate.toDto())
        val insertedCandidate = candidateDao.getAllCandidates().first()
        assert(1 == insertedCandidate.size)

        candidateDao.deleteCandidate(candidate.toDto())
        val deletedCandidate = candidateDao.getAllCandidates().first()
        assert(deletedCandidate.isEmpty())
    }

    @Test
    fun getAllCandidates_Alphabetically() = runBlocking {
        val candidate1 = Candidate(
            firstName = "Jane",
            lastName = "Smith",
            phoneNumber = "321654",
            email = "jane.smith@exemple.com",
            dateOfBirth = LocalDate.of(1995,5,5)
        )

        val candidate2 = Candidate(
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john.doe@exemple.com",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        candidateDao.upsertCandidate(candidate1.toDto())
        candidateDao.upsertCandidate(candidate2.toDto())

        val insertedCandidates = candidateDao.getAllCandidates().first()
        assert(insertedCandidates.size == 2)
        assert("Smith" == insertedCandidates[1].lastName)
        assert("Doe" == insertedCandidates[0].lastName)
    }

    @Test
    fun getCandidateById() = runBlocking {
        val candidate1 = Candidate(
            firstName = "Jane",
            lastName = "Smith",
            phoneNumber = "321654",
            email = "jane.smith@exemple.com",
            dateOfBirth = LocalDate.of(1995,5,5)
        )

        val candidate2 = Candidate(
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john.doe@exemple.com",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        candidateDao.upsertCandidate(candidate1.toDto())
        candidateDao.upsertCandidate(candidate2.toDto())

        val insertedCandidate = candidateDao.getCandidateById(1)
        assert("Smith" == insertedCandidate.lastName)

        val otherCandidate = candidateDao.getCandidateById(2)
        assert("Doe" == otherCandidate.lastName)
    }

    @Test
    fun getFavoriteCandidates() = runBlocking {
        val candidate1 = Candidate(
            firstName = "Jane",
            lastName = "Smith",
            phoneNumber = "321654",
            email = "jane.smith@exemple.com",
            isFavorite = true,
            dateOfBirth = LocalDate.of(1995,5,5)
        )

        val candidate2 = Candidate(
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john.doe@exemple.com",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )
        candidateDao.upsertCandidate(candidate1.toDto())
        candidateDao.upsertCandidate(candidate2.toDto())

        val favoriteCandidate = candidateDao.getFavoriteCandidates().first()
        assert("Smith" == favoriteCandidate[0].lastName)
    }

    @Test
    fun getCandidateWhenEmpty() = runBlocking {
        val emptyList = candidateDao.getAllCandidates().first()
        assert(emptyList == emptyList<Candidate>())
    }

}