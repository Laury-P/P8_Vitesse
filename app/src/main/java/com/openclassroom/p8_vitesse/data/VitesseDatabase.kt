package com.openclassroom.p8_vitesse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassroom.p8_vitesse.data.converter.LocalDateConverter
import com.openclassroom.p8_vitesse.data.dao.CandidateDao
import com.openclassroom.p8_vitesse.data.entity.CandidateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [CandidateDto::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(LocalDateConverter::class)
abstract class VitesseDatabase : RoomDatabase() {
    abstract fun candidateDao(): CandidateDao

    private class AppDatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.candidateDao())
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: VitesseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VitesseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitesseDatabase::class.java,
                    "VitesseDatabase"
                ).addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateDatabase(candidateDao: CandidateDao) {
            candidateDao.upsertCandidate(
                CandidateDto(
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
                )
            )
            candidateDao.upsertCandidate(
                CandidateDto(
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
            candidateDao.upsertCandidate(
                CandidateDto(
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
                )
            )
            candidateDao.upsertCandidate(
                CandidateDto(
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
                )
            )
            candidateDao.upsertCandidate(
                CandidateDto(
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
                )
            )
            candidateDao.upsertCandidate(
                CandidateDto(
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
                )
            )
            candidateDao.upsertCandidate(
                CandidateDto(
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
                )
            )
            candidateDao.upsertCandidate(
                CandidateDto(
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
        }
    }

}