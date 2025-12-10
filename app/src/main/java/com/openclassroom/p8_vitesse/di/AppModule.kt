package com.openclassroom.p8_vitesse.di

import android.content.Context
import com.openclassroom.p8_vitesse.data.VitesseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): VitesseDatabase {
        return VitesseDatabase.getDatabase(context, scope)
    }

    @Provides
    @Singleton
    fun provideCandidateDao(database: VitesseDatabase) = database.candidateDao()


}