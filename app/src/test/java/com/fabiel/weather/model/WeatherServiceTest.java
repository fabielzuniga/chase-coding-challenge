package com.fabiel.weather.model;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.location.Location;

import com.fabiel.weather.common.CommonProtos.WeatherProto;
import com.fabiel.weather.core.OpenWeatherMapNetworkRetrofitService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Tests for {@link WeatherService } */
@RunWith(MockitoJUnitRunner.class)
public final class WeatherServiceTest {

  private static final double LATITUDE = 37.42267;
  private static final double LONGITUDE = -122.084987;

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private OpenWeatherMapNetworkRetrofitService networkServiceMock;
  @Mock
  private Call<WeatherGson> getCurrentWeatherInfoCallMock;
  @Mock
  private Location locationMock;
  @Mock
  private Consumer<Throwable> onErrorMock;
  @Mock
  private Consumer<WeatherProto> fetchWeatherOnSuccessMock;

  private WeatherService weatherService;

  @Before
  public void before() {
    this.weatherService = new WeatherService(networkServiceMock);
  }

  @Test
  public void fetchWeather_success() {
    WeatherGson weatherGson = new WeatherGson();
    weatherGson.coord = new WeatherGson.Coord();
    weatherGson.coord.lat = LATITUDE;
    weatherGson.coord.lon = LONGITUDE;
    weatherGson.weather = new WeatherGson.Weather[] {new WeatherGson.Weather()};
    weatherGson.weather[0].main = "Clear";
    weatherGson.weather[0].description = "clear sky";
    weatherGson.weather[0].icon = "10d";
    weatherGson.main = new WeatherGson.Main();
    weatherGson.wind = new WeatherGson.Wind();

    Unit unit = Unit.METRIC;

    when(locationMock.getLatitude()).thenReturn(LATITUDE);
    when(locationMock.getLongitude()).thenReturn(LONGITUDE);

    when(networkServiceMock.getCurrentWeatherInfo(
        eq(LATITUDE), eq(LONGITUDE), eq(unit.getQueryParameterValue()), anyString()))
        .thenReturn(getCurrentWeatherInfoCallMock);

    doAnswer(invocation -> {
      Callback<WeatherGson> callback = invocation.getArgument(0);
      callback.onResponse(getCurrentWeatherInfoCallMock, Response.success(weatherGson));
      return null;
    })
    .when(getCurrentWeatherInfoCallMock).enqueue(any(Callback.class));

    this.weatherService.fetchWeather(locationMock, unit, fetchWeatherOnSuccessMock, onErrorMock);

    WeatherProto expected = WeatherProto.newBuilder()
        .setLatitude(LATITUDE)
        .setLongitude(LONGITUDE)
        .setMain("Clear")
        .setDescription("clear sky")
        .setIconUrl("https://openweathermap.org/img/wn/10d@2x.png")
        .setUnit(unit.toProto())
        .build();

    verify(fetchWeatherOnSuccessMock).accept(expected);
    verify(onErrorMock, never()).accept(any(Throwable.class));
  }

  @Test
  public void fetchWeather_error() {
    Unit unit = Unit.METRIC;

    when(locationMock.getLatitude()).thenReturn(LATITUDE);
    when(locationMock.getLongitude()).thenReturn(LONGITUDE);

    when(networkServiceMock.getCurrentWeatherInfo(
        eq(LATITUDE), eq(LONGITUDE), eq(unit.getQueryParameterValue()), anyString()))
        .thenReturn(getCurrentWeatherInfoCallMock);

    Throwable error = new Throwable();

    doAnswer(invocation -> {
      Callback<WeatherGson> callback = invocation.getArgument(0);
      callback.onFailure(getCurrentWeatherInfoCallMock, error);
      return null;
    })
    .when(getCurrentWeatherInfoCallMock).enqueue(any(Callback.class));

    this.weatherService.fetchWeather(locationMock, unit, fetchWeatherOnSuccessMock, onErrorMock);

    verify(fetchWeatherOnSuccessMock, never()).accept(any(WeatherProto.class));
    verify(onErrorMock).accept(error);
  }
}
