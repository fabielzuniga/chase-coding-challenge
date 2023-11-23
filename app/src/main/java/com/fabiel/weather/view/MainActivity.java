package com.fabiel.weather.view;

import static com.fabiel.weather.core.util.ImageUtil.loadImageFromUrl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabiel.weather.R;
import com.fabiel.weather.common.CommonProtos.QuantityProto;
import com.fabiel.weather.core.LocationService;
import com.fabiel.weather.core.WeatherApplication;
import com.fabiel.weather.core.util.adapter.AutoCompleteAddressAdapter;
import com.fabiel.weather.core.util.adapter.QuantityRecyclerViewAdapter;
import com.fabiel.weather.core.util.adapter.TextSpinnerAdapter;
import com.fabiel.weather.databinding.ActivityMainBinding;
import com.fabiel.weather.model.Unit;
import com.fabiel.weather.viewmodel.MainViewModel;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
  // https://developer.android.com/topic/libraries/data-binding

  // Because certain Android framework classes such as activities and fragments are instantiated by
  // the system, Dagger can't create instances. For activities specifically, any initialization code
  // needs to go into the onCreate() method. That means we cannot use @Inject annotation in the
  // constructor of the class (constructor injection). Instead, we need to use field injection.
  // com.fabiel.weather.core.ApplicationComponent also must define a method to tell Dagger that
  // this activity needs injection.
  @Inject
  MainViewModel mainViewModel;
  @Inject
  LocationService locationService;

  private RecyclerView recyclerView;
  private QuantityRecyclerViewAdapter recyclerViewAdapter;

  // TODO: save/read instance state (override onSaveInstanceState)

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ((WeatherApplication) getApplicationContext()).getApplicationComponent().inject(this);

    ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(
        this, R.layout.activity_main);
    // setViewModel is defined in the tag data/variable in activity_main.xml.
    // Android Studio offers a handy way to convert the layout to Data Binding: Right-click the root
    // element, select Show Context Actions, then Convert to data binding layout:
    // https://developer.android.com/codelabs/android-databinding#2
    mainViewModel.setActivity(this);
    activityMainBinding.setViewModel(mainViewModel);
    activityMainBinding.executePendingBindings();

    this.recyclerView = findViewById(R.id.main_activity_recycler_view);
    this.recyclerViewAdapter = new QuantityRecyclerViewAdapter();
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    this.recyclerView.setAdapter(this.recyclerViewAdapter);

    AutoCompleteTextView locationTextTextView = findViewById(
        R.id.main_activity_auto_complete_text_view_input_address);
    locationTextTextView.setAdapter(new AutoCompleteAddressAdapter(this));

    Spinner spinnerUnit = findViewById(R.id.main_activity_spinner_unit);
    spinnerUnit.setAdapter(new TextSpinnerAdapter<>(
        this, Arrays.asList(Unit.values()), Unit::getText));
    spinnerUnit.setSelection(mainViewModel.getUnitPosition());
    spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mainViewModel.onUnitSelected(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    this.locationService.checkLocationPermissionOnRequestPermissionsResult(
        requestCode,
        permissions,
        grantResults,
        // TODO: we could call get location on permission granted
        /* onPermissionGranted= */ () ->
            Toast.makeText(this, R.string.location_permission_granted, Toast.LENGTH_SHORT).show(),
        /* onPermissionDenied= */ () ->
            Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    this.locationService.checkGpsEnabledOnActivityResult(
        requestCode,
        resultCode,
        data,
        // TODO: we could call get location on GPS enabled
        /* onGpsEnabled= */ () ->
            Toast.makeText(this, R.string.gps_enabled, Toast.LENGTH_SHORT).show(),
        /* onGpsNotEnabled= */ () ->
            Toast.makeText(this, R.string.gps_not_enabled, Toast.LENGTH_SHORT).show());
  }

  @BindingAdapter("errorMessage")
  @SuppressWarnings("unused")
  public static void bindErrorMessage(View view, String errorMessage) {
    if (errorMessage != null)
      Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
  }

  @BindingAdapter("weatherIconUrl")
  @SuppressWarnings("unused")
  public static void bindWeatherIcon(ImageView imageView, String imageUrl) {
    if (imageUrl != null) {
      loadImageFromUrl(imageUrl, imageView);
    }
  }

  @BindingAdapter("weatherQuantities")
  @SuppressWarnings("unused")
  public static void bindWeatherQuantities(
      RecyclerView recyclerView, List<QuantityProto> quantities) {
    if (quantities != null) {
      QuantityRecyclerViewAdapter recyclerViewAdapter
          = (QuantityRecyclerViewAdapter) recyclerView.getAdapter();
      if (recyclerViewAdapter != null) {
        recyclerViewAdapter.setData(quantities);
      }
    }
  }
}
