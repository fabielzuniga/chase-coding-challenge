package com.fabiel.weather.core;

import com.fabiel.weather.model.WeatherGson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/** Retrofit network service definition for Open Weather Map. */
public interface OpenWeatherMapNetworkRetrofitService {

  /**
   * Gets the current weather information.
   *
   * @param latitude geographical coordinates (latitude, longitude)
   * @param longitude geographical coordinates (latitude, longitude)
   * @param units Units of measurement. standard, metric and imperial units are available.
   * @param apiKey API KEY
   * @return the weather call
   */
  @GET("data/2.5/weather")
  Call<WeatherGson> getCurrentWeatherInfo(
      @Query("lat") double latitude,
      @Query("lon") double longitude,
      @Query("units") String units,
      @Query("appid") String apiKey);
}
