package com.fabiel.weather.model;

import com.fabiel.weather.common.CommonProtos.WeatherProto;

/** Weather quantities unit */
public enum Unit {
  STANDARD(WeatherProto.Unit.STANDARD, "Standard Unit", "standard", "Kelvin", "meter/sec"),
  METRIC(WeatherProto.Unit.METRIC, "Metric Unit", "metric", "Celsius", "meter/sec"),
  IMPERIAL(WeatherProto.Unit.IMPERIAL, "Imperial Unit", "imperial", "Fahrenheit", "miles/hour");

  private WeatherProto.Unit proto;
  private String text;
  private String queryParameterValue;
  private String temperatureUnit;
  private String windSpeedUnit;

  Unit(
      WeatherProto.Unit proto,
      String text,
      String queryParameterValue,
      String temperatureUnit,
      String windSpeedUnit) {
    this.proto = proto;
    this.text = text;
    this.queryParameterValue = queryParameterValue;
    this.temperatureUnit = temperatureUnit;
    this.windSpeedUnit = windSpeedUnit;
  }

  /**
   * Gets an instance of unit from its corresponding proto representation.
   *
   * @param unit proto unit
   * @return a unit
   */
  public static Unit fromProto(WeatherProto.Unit unit) {
    if (unit == WeatherProto.Unit.STANDARD) {
      return STANDARD;
    }
    if (unit == WeatherProto.Unit.METRIC) {
      return METRIC;
    }
    if (unit == WeatherProto.Unit.IMPERIAL) {
      return IMPERIAL;
    }
    return null;
  }

  /**
   * Converts this unit to its corresponding proto representation.
   *
   * @return the proto unit
   */
  public WeatherProto.Unit toProto() {
    return this.proto;
  }

  /** @return the unit text (or name). */
  public String getText() {
    return this.text;
  }

  String getQueryParameterValue() {
    return this.queryParameterValue;
  }

  /** @return the unit for temperature */
  public String getTemperatureUnit() {
    return this.temperatureUnit;
  }

  /** @return the unit for pressure */
  public String getPressureUnit() {
    return "hPa";
  }

  /** @return the unit for humidity */
  public String getHumidity() {
    return "%";
  }

  /** @return the unit for weend speed */
  public String getWindSpeedUnit() {
    return this.windSpeedUnit;
  }
}
