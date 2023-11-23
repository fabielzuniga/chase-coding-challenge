package com.fabiel.weather.core;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Dagger network module. */
@Module
public final class NetworkModule {

  /** Provides an instance of the {@link OpenWeatherMapNetworkRetrofitService} */
  @Singleton
  @Provides
  public OpenWeatherMapNetworkRetrofitService provideOpenWeatherMapNetworkRetrofitService() {
    return new Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenWeatherMapNetworkRetrofitService.class);
  }
}
