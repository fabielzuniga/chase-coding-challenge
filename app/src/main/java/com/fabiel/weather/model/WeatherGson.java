package com.fabiel.weather.model;

/** POJO class to be generated from a JSON response. */
public class WeatherGson {
  // GSON format: https://openweathermap.org/current

  Coord  coord;
  Weather[] weather;
  Main main;
  Wind wind;

  WeatherGson() {
  }

  static class Coord {
    double lat;
    double lon;
  }

  static class Weather {
    String main;
    String description;
    String icon;
  }

  static class Main {
    double temp;
    double feels_like;
    double temp_min;
    double temp_max;
    int pressure;
    int humidity;
  }

  static class Wind {
    double speed;
  }
}
