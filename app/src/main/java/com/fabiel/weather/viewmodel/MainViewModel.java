package com.fabiel.weather.viewmodel;

import static com.fabiel.weather.core.util.Util.equal;

import android.app.Activity;
import android.location.Address;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.fabiel.weather.R;
import com.fabiel.weather.common.CommonProtos.QuantityProto;
import com.fabiel.weather.common.CommonProtos.WeatherProto;
import com.fabiel.weather.core.LocationService;
import com.fabiel.weather.core.util.ActivityUtil;
import com.fabiel.weather.core.util.GeocoderUtil;
import com.fabiel.weather.model.AppPreferences;
import com.fabiel.weather.model.Unit;
import com.fabiel.weather.model.WeatherService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

/** View model for MainActivity. */
public class MainViewModel extends BaseObservable {

  private final LocationService locationService;
  private final WeatherService weatherService;

  private boolean isLoading;
  private String errorMessage;
  private String inputAddress;
  private Unit unit;
  private String weatherIconUrl;
  private String weatherSummary;
  private final List<QuantityProto> weatherQuantities;

  private Activity activity;

  @Inject
  MainViewModel(
      LocationService locationService,
      WeatherService weatherService) {
    this.locationService = locationService;
    this.weatherService = weatherService;
    this.isLoading = false;
    this.weatherQuantities = new ArrayList<>();
    this.unit = Unit.STANDARD;
  }

  /**
   * Sets the activity associated to this model view.
   *
   * Note: it is recommended that the view model doesn't have a reference to the activity and
   * everything is handled with callback methods and data binding. This reference is only used to
   * get resources like application preferences and texts. Also, the location service needs the
   * context to operate.
   *
   * @param activity activity
   */
  public void setActivity(Activity activity) {
    this.activity = activity;
    setUnit(AppPreferences.getUnit(activity).orElse(Unit.STANDARD));
    Optional<String> lastInputAddress = AppPreferences.getInputAddress(activity);
    if (lastInputAddress.isPresent()) {
      setInputAddress(lastInputAddress.get());
    }
  }

  private void setIsLoading(boolean value) {
    if (!equal(this.isLoading, value)) {
      this.isLoading = value;
      // BR is autogenerated based on @Bindable annotations
      notifyPropertyChanged(BR.isLoading);
    }
  }

  @Bindable
  public boolean getIsLoading() {
    return this.isLoading;
  }

  private void setErrorMessage(String value) {
    // We always trigger the property change, since the same error can happen multiple times. If the
    // error is presented as a toast, we want the toast to appear again if the errir happens again.
    // if (!equal(this.errorMessage, value)) {
      this.errorMessage = value;
      notifyPropertyChanged(BR.errorMessage);
    // }
  }

  @Bindable
  public String getErrorMessage() {
    return this.errorMessage;
  }

  private void setInputAddress(String value) {
    if (!equal(this.inputAddress, value)) {
      this.inputAddress = value;
      notifyPropertyChanged(BR.inputAddress);
      AppPreferences.setInputAddress(this.activity, this.inputAddress);
    }
  }

  // Two-way data binding is not used, thus, this method is explicitly bound in activity_main.xml
  // to update the model when the UI changes.
  // https://developer.android.com/topic/libraries/data-binding/two-way#java
  public void onInputAddressChanged(CharSequence text) {
    this.inputAddress = text.toString();
    AppPreferences.setInputAddress(this.activity, this.inputAddress);
  }

  @Bindable
  public String getInputAddress() {
    return this.inputAddress;
  }

  private void setUnit(Unit value) {
    if (!equal(this.unit, value)) {
      this.unit = value;
      notifyPropertyChanged(BR.unit);
      AppPreferences.setUnit(this.activity, this.unit);
    }
  }

  // Two-way data binding is not used, thus, this method is explicitly bound in activity_main.xml
  // to update the model when the UI changes.
  // https://developer.android.com/topic/libraries/data-binding/two-way#java
  public void onUnitSelected(int position) {
    this.unit = Unit.values()[position];
    AppPreferences.setUnit(this.activity, this.unit);
  }

  @Bindable
  public Unit getUnit() {
    return this.unit;
  }

  public int getUnitPosition() {
    // This implementation is very inefficient, however this method is called only once at activity
    // initialization.
    return Arrays.asList(Unit.values()).indexOf(this.unit);
  }

  private void setWeatherIconUrl(String value) {
    if (!equal(this.weatherIconUrl, value)) {
      this.weatherIconUrl = value;
      notifyPropertyChanged(BR.weatherIconUrl);
    }
  }

  @Bindable
  public String getWeatherIconUrl() {
    return this.weatherIconUrl;
  }

  private void setWeatherSummary(String value) {
    if (!equal(this.weatherSummary, value)) {
      this.weatherSummary = value;
      notifyPropertyChanged(BR.weatherSummary);
    }
  }

  @Bindable
  public String getWeatherSummary() {
    return this.weatherSummary;
  }

  private void setWeatherQuantities(List<QuantityProto> value) {
    this.weatherQuantities.clear();
    this.weatherQuantities.addAll(value);
    notifyPropertyChanged(BR.weatherQuantities);
  }

  @Bindable
  public List<QuantityProto> getWeatherQuantities() {
    return Collections.unmodifiableList(this.weatherQuantities);
  }

  /** Fetched the current location. */
  public void onGetLocation() {
    setIsLoading(true);
    this.locationService.getLocation(
        this.activity,
        /* onSuccess= */ location -> {
          GeocoderUtil.locationToAddress(
              this.activity,
              location,
              /* onSuccess= */ address -> {
                if (address != null) {
                  setInputAddress(GeocoderUtil.addressToText(address));
                }
                setIsLoading(false);
              },
              /* onError= */ error -> {
                setErrorMessage(getText(R.string.error_mapping_location_to_address));
                setIsLoading(false);
              });
        },
        /* onPermissionRequested= */ () -> setIsLoading(false),
        /* onGpsActivationRequested= */  () -> setIsLoading(false),
        /* onLocationUnavailable= */  () -> setIsLoading(false),
        /* onError= */ error -> {
          setErrorMessage(getText(R.string.error_getting_current_location));
          setIsLoading(false);
        });
  }

  /** Fetched the weather for the input address */
  public void onFetchWeather() {
    // TODO: disable the fetch weather button if no input address is available.
    if (this.inputAddress == null || this.inputAddress.isEmpty()) {
      setErrorMessage(getText(R.string.error_input_address_is_required));
      return;
    }
    setIsLoading(true);
    GeocoderUtil.findAddressMatches(
        this.activity,
        this.inputAddress,
        /* onSuccess= */ addresses -> {
          if (!addresses.isEmpty()) {
            Address address = addresses.get(0);
            this.weatherService.fetchWeather(
                address.getLatitude(),
                address.getLongitude(),
                this.unit,
                /* onSuccess= */ weatherProto -> {
                  setWeatherIconUrl(weatherProto.getIconUrl());
                  setWeatherSummary(
                      String.format(
                          "%s (%s)", weatherProto.getMain(), weatherProto.getDescription()));
                  setWeatherQuantities(getQuantities(weatherProto));
                  setIsLoading(false);
                },
                /* onError= */ error -> {
                  setErrorMessage(getText(R.string.error_getting_weather));
                  setIsLoading(false);
                });
          } else {
            setErrorMessage(getText(R.string.error_unknown_location));
            setIsLoading(false);
          }
        },
        /* onError= */ error -> {
          setErrorMessage(getText(R.string.error_mapping_location_to_address));
          setIsLoading(false);
        }
    );
  }

  private String getText(int resource) {
    return ActivityUtil.getText(this.activity, resource);
  }

  private static List<QuantityProto> getQuantities(WeatherProto weather) {
    List<QuantityProto> quantities = new ArrayList<>();
    Unit unit = Unit.fromProto(weather.getUnit());
    // TODO: Add resources for these texts
    quantities.add(QuantityProto.newBuilder()
        .setName("Temp")
        .setValue(Double.toString(weather.getTemperature()))
        .setUnit(unit.getTemperatureUnit())
        .build());
    quantities.add(QuantityProto.newBuilder()
        .setName("Feels like")
        .setValue(Double.toString(weather.getFeelsLike()))
        .setUnit(unit.getTemperatureUnit())
        .build());
    quantities.add(QuantityProto.newBuilder()
        .setName("Temp min")
        .setValue(Double.toString(weather.getTemperatureMin()))
        .setUnit(unit.getTemperatureUnit())
        .build());
    quantities.add(QuantityProto.newBuilder()
        .setName("Temp max")
        .setValue(Double.toString(weather.getTemperatureMax()))
        .setUnit(unit.getTemperatureUnit())
        .build());
    quantities.add(QuantityProto.newBuilder()
        .setName("Pressure")
        .setValue(Integer.toString(weather.getPressure()))
        .setUnit(unit.getPressureUnit())
        .build());
    quantities.add(QuantityProto.newBuilder()
        .setName("Humidity")
        .setValue(Integer.toString(weather.getHumidity()))
        .setUnit(unit.getHumidity())
        .build());
    quantities.add(QuantityProto.newBuilder()
        .setName("Wind speed")
        .setValue(Double.toString(weather.getWindSpeed()))
        .setUnit(unit.getWindSpeedUnit())
        .build());
    return quantities;
  }
}
