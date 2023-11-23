package com.fabiel.weather.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Optional;

/**
 * Stores app preferences.
 */
public final class AppPreferences {
  // https://developer.android.com/training/data-storage
  // https://developer.android.com/training/data-storage/shared-preferences#java

  private AppPreferences() {
  }

  private static final String PREFERENCES_FILE_NAME = "com.fabiel.weather.preference_file_name";
  private static final String INPUT_ADDRESS = "input_address";
  private static final String UNIT = "unit";

  /**
   * Stores the input address in application preferences so it persists executions.
   *
   * @param context context
   * @param inputAddress input address to store
   */
  public static void setInputAddress(Context context, String inputAddress) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(INPUT_ADDRESS, inputAddress);
    editor.apply();
  }

  /**
   * Gets the stored input address.
   *
   * @param context context
   * @return the input address if one has been persisted, Optional.empty otherwise.
   */
  public static Optional<String> getInputAddress(Context context) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    return Optional.ofNullable(sharedPreferences.getString(INPUT_ADDRESS, null));
  }

  /**
   * Stores the unit in application preferences so it persists executions.
   *
   * @param context context
   * @param unit unit to store
   */
  public static void setUnit(Context context, Unit unit) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(UNIT, unit.name());
    editor.apply();
  }

  /**
   * Gets the stored unit.
   *
   * @param context context
   * @return the unit if one has been persisted, Optional.empty otherwise.
   */
  public static Optional<Unit> getUnit(Context context) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    return Optional.ofNullable(
        sharedPreferences.getString(UNIT, null))
        .map(strUnit -> Unit.valueOf(Unit.class, strUnit));
  }

  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
  }
}
