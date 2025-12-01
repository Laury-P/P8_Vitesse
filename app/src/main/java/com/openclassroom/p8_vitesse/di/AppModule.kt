package com.openclassroom.p8_vitesse.di

import android.content.Context
import com.openclassroom.p8_vitesse.data.VitesseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

   @Provides
   @Singleton
   fun provideAppDatabase(@ApplicationContext context: Context): VitesseDatabase {
       return VitesseDatabase.getDatabase(context)
   }

    @Provides
    @Singleton
    fun provideCandidateDao(database: VitesseDatabase) = database.candidateDao()


}