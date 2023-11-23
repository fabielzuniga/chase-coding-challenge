package com.fabiel.weather.core;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;

import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

/** GPS location service */
@Singleton
public final class LocationService {

  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  private static final int GPS_ACTIVATION_REQUEST_CODE = 1;

  private final LocationRequest locationRequest;

  @Inject
  LocationService() {
    this.locationRequest = new LocationRequest.Builder(/* intervalMillis= */ 5000)
        .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        .build();
  }

  /**
   * This method can be used to override
   * Activity.onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
   * and check if the location permission was granted.
   *
   * @param requestCode request code from onRequestPermissionsResult()
   * @param permissions permissions code from onRequestPermissionsResult()
   * @param grantResults grant results from onActivityResult()
   * @param onPermissionGranted callback method executed if the location permission was granted.
   * @param onPermissionDenied callback method executed if the location permission was denied.
   */
  public void checkLocationPermissionOnRequestPermissionsResult(
      int requestCode,
      @SuppressWarnings("unused")
      String[] permissions,
      int[] grantResults,
      Runnable onPermissionGranted,
      Runnable onPermissionDenied) {
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted.run();
      } else {
        onPermissionDenied.run();
      }
    }
  }

  /**
   * This method can be used to override
   * Activity.onActivityResult(int requestCode, int resultCode, @Nullable Intent data) and check if
   * the GPS has been enabled.
   *
   * @param requestCode request code from onActivityResult()
   * @param resultCode result code from onActivityResult()
   * @param data data from onActivityResult()
   * @param onGpsEnabled callback method executed if the GPS was enabled.
   * @param onGpsNotEnabled callback method executed if the GPS was not enabled.
   */
  public void checkGpsEnabledOnActivityResult(
      int requestCode,
      int resultCode,
      @SuppressWarnings("unused")
      Intent data,
      Runnable onGpsEnabled,
      Runnable onGpsNotEnabled) {
    if (requestCode == GPS_ACTIVATION_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        onGpsEnabled.run();
      } else {
        onGpsNotEnabled.run();
      }
    }
  }

  /**
   * Gets the device GPS location.
   *
   * @param context context
   * @param onSuccess callback method executed on success
   * @param onPermissionRequested callback method executed if location permission was requested
   * @param onGpsActivationRequested callback method executed if the GPS activation was requested
   * @param onLocationUnavailable callback method executed if location services is unavailable
   * @param onError callback method executed on error
   */
  public void getLocation(
      Activity context,
      Consumer<Location> onSuccess,
      Runnable onPermissionRequested,
      Runnable onGpsActivationRequested,
      Runnable onLocationUnavailable,
      Consumer<Exception> onError) {
    // This is unnecessary since the min version in build.gradle is set to 33.
    // It was left for completeness.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onLocationUnavailable.run();
      return;
    }

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      context.requestPermissions(
          new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
      onPermissionRequested.run();
      return;
    }

    if (!isGpsEnabled(context)) {
      requestGpsActivation(context, onLocationUnavailable, onError);
      onGpsActivationRequested.run();
      return;
    }

    LocationServices.getFusedLocationProviderClient(context)
        .requestLocationUpdates(locationRequest, new LocationCallback() {
          @Override
          public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
            if (locationResult != null && locationResult.getLocations().size() > 0) {
              int lastLocation = locationResult.getLocations().size() - 1;
              Location location = locationResult.getLocations().get(lastLocation);
              onSuccess.accept(location);
            }
          }
        }, Looper.getMainLooper());
  }

  private boolean isGpsEnabled(Context context) {
    LocationManager locationManager =
        (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  private void requestGpsActivation(
      Activity context,
      Runnable onSettingUnavailable,
      Consumer<Exception> onError) {
    LocationSettingsRequest.Builder locationSettingsRequestBuilder =
        new LocationSettingsRequest.Builder().addLocationRequest(this.locationRequest);
    locationSettingsRequestBuilder.setAlwaysShow(true);

    Task<LocationSettingsResponse> checkLocationSettingsTask = LocationServices
        .getSettingsClient(context.getApplicationContext())
        .checkLocationSettings(locationSettingsRequestBuilder.build());

    checkLocationSettingsTask.addOnCompleteListener(task -> {
      try {
        task.getResult(ApiException.class);
      } catch (ApiException e) {
        switch (e.getStatusCode()) {
          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            try {
              ResolvableApiException resolvableApiException = (ResolvableApiException) e;
              resolvableApiException.startResolutionForResult(context, GPS_ACTIVATION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException ex) {
              onError.accept(ex);
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            onSettingUnavailable.run();
            break;
        }
      }
    });
  }
}
