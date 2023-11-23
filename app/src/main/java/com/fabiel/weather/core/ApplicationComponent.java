package com.fabiel.weather.core;

import com.fabiel.weather.view.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/** Dagger application component. */
@Component (modules = {NetworkModule.class})
@Singleton
public interface ApplicationComponent {

  // https://developer.android.com/training/dependency-injection/dagger-android#java

  // This tells Dagger that MainActivity requests injection so the graph needs to satisfy all the
  // dependencies of the fields that MainActivity is injecting.
  void inject(MainActivity mainActivity);
}
