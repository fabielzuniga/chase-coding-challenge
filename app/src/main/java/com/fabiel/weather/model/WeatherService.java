package com.fabiel.weather.model;

import android.location.Location;

import com.fabiel.weather.common.CommonProtos.WeatherProto;
import com.fabiel.weather.core.OpenWeatherMapNetworkRetrofitService;

import java.util.function.Consumer;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Weather service. */
public final class WeatherService {
  private static final String OPEN_WEATHER_MAP_API_KEY = "9191fef7b36861edf8ebf70ab479f693";

  private final OpenWeatherMapNetworkRetrofitService networkService;

  @Inject
  WeatherService(OpenWeatherMapNetworkRetrofitService networkService) {
    this.networkService = networkService;
  }

  /**
   * Fetches the weather.
   *
   * @param location location to fetch the weather info at
   * @param unit unit
   * @param onSuccess callback method executed on success
   * @param onError callback method executed on error
   */
  public void fetchWeather(
      Location location,
      Unit unit,
      Consumer<WeatherProto> onSuccess,
      Consumer<Throwable> onError) {
    fetchWeather(location.getLatitude(), location.getLongitude(), unit, onSuccess, onError);
  }

  /**
   * Fetches the weather.
   *
   * @param latitude location latitude to fetch the weather info at
   * @param longitude location longitude to fetch the weather info at
   * @param unit unit
   * @param onSuccess callback method executed on success
   * @param onError callback method executed on error
   */
  public void fetchWeather(
      double latitude,
      double longitude,
      Unit unit,
      Consumer<WeatherProto> onSuccess,
      Consumer<Throwable> onError) {
    // Example:
    // https://api.openweathermap.org/data/2.5/weather?lat=37.422671&lon=-122.084987&units=metric&appid=9191fef7b36861edf8ebf70ab479f693
    this.networkService.getCurrentWeatherInfo(
            latitude,
            longitude,
            unit.getQueryParameterValue(),
            OPEN_WEATHER_MAP_API_KEY)
        .enqueue(new Callback<WeatherGson>() {
          @Override
          public void onResponse(Call<WeatherGson> call, Response<WeatherGson> response) {
            WeatherGson gson = response.body();
            onSuccess.accept(WeatherProto.newBuilder()
                 .setLatitude(gson.coord.lat)
                .setLongitude(gson.coord.lon)
                .setMain(gson.weather[0].main)
                .setDescription(gson.weather[0].description)
                // https://openweathermap.org/weather-conditions
                .setIconUrl(String.format(
                    "https://openweathermap.org/img/wn/%s@2x.png", gson.weather[0].icon))
                .setUnit(unit.toProto())
                .setTemperature(gson.main.temp)
                .setFeelsLike(gson.main.feels_like)
                .setTemperatureMin(gson.main.temp_min)
                .setTemperatureMax(gson.main.temp_max)
                .setPressure(gson.main.pressure)
                .setHumidity(gson.main.humidity)
                .setWindSpeed(gson.wind.speed)
                .build());
          }

          @Override
          public void onFailure(Call<WeatherGson> call, Throwable error) {
            call.cancel();
            onError.accept(error);
          }
        });
  }
}
