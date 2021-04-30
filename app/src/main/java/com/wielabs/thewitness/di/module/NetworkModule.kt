package com.wielabs.thewitness.di.module

import com.wielabs.thewitness.network.TheWitnessService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
/**
 * Hilt module for providing the instances of objects related to network
 */
@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://thewitness.wielabs.com/"

    /**
     * Provides a single instance of Retrofit object throughout app's lifecycle.
     * @return Retrofit
     */
    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides a single instance of BoutiqueService throughout the app's lifecycle.
     * @param retrofit: Retrofit instance obtained from [provideRetrofitInstance]
     * @return [TheWitnessService]
     */
    @Singleton
    @Provides
    fun provideTheWitnessService(retrofit: Retrofit): TheWitnessService {
        return retrofit.create(TheWitnessService::class.java)
    }
}