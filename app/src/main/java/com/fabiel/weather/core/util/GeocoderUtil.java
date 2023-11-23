package com.fabiel.weather.core.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Utility methods related to Geocoder. */
public final class GeocoderUtil {
  private GeocoderUtil() {
  }

  /**
   * Converts a location to the closest address.
   *
   * @param context context
   * @param location location
   * @param onSuccess callback method executed on success
   * @param onError callback method executed on error
   */
  public static void locationToAddress(
      Context context,
      Location location,
      Consumer<Address> onSuccess,
      Consumer<Exception> onError) {
    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    geocoder.getFromLocation(
        location.getLatitude(),
        location.getLongitude(),
        /* maxResults= */ 1,
        new Geocoder.GeocodeListener() {
          @Override
          public void onGeocode(@NonNull List<Address> addresses) {
            if (!addresses.isEmpty()) {
              onSuccess.accept(addresses.get(0));
            }
            onSuccess.accept(null);
          }
          @Override
          public void onError(@Nullable String errorMessage) {
            onError.accept(new Exception(errorMessage));
          }
        });
  }

  /**
   * Finds matches for the given partial address.
   *
   * @param context context
   * @param partialAddress partial address used as pattern for the search
   * @param onSuccess callback method executed on success
   * @param onError callback method executed on error
   */
  public static void findAddressMatches(
      Context context,
      String partialAddress,
      Consumer<List<Address>> onSuccess,
      Consumer<Exception> onError) {
    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    geocoder.getFromLocationName(
        partialAddress,
        /* maxResults= */ 5,
        new Geocoder.GeocodeListener() {
          @Override
          public void onGeocode(@NonNull List<Address> addresses) {
            onSuccess.accept(addresses);
          }
          @Override
          public void onError(@Nullable String errorMessage) {
            onError.accept(new Exception(errorMessage));
          }
        });
  }

  /**
   * @Deprecated use {@link #findAddressMatches(Context, String, Consumer, Consumer)} instead
   */
  public static List<Address> blockingFindAddressMatches(
      Context context,
      String partialAddress) throws IOException {
    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    return geocoder.getFromLocationName(partialAddress, /* maxResults= */ 5);
  }

  /**
   * Converts an address to text.
   *
   * @param address address
   * @return text representing the address
   */
  public static String addressToText(Address address) {
    List<String> addressComponents = new ArrayList<>();
    // Feature name of the address, for example, "Golden Gate Bridge", or null if it is unknown
    addressComponents.add(address.getFeatureName());
    // Street number
    // addressComponents.add(address.getSubThoroughfare());
    // Street name
    addressComponents.add(address.getThoroughfare());
    // City
    addressComponents.add(address.getLocality());
    // State
    addressComponents.add(address.getAdminArea());
    addressComponents.add(address.getPostalCode());
    addressComponents.add(address.getCountryName());
    return addressComponents.stream()
        .filter(str -> str != null && !str.isEmpty())
        .collect(Collectors.joining(" "));
  }
}
