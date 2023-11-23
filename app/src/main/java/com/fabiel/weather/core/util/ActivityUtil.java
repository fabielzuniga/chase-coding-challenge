package com.fabiel.weather.core.util;

import android.content.Context;

/** Utility methods related to activities. */
public final class ActivityUtil {

  private ActivityUtil() {
  }

  /**
   * Returns the text given by {@code resource}
   * @param context context
   * @param resource resource id
   * @return the associated text
   */
  public static String getText(Context context, int resource) {
    return context.getResources().getText(resource).toString();
  }
}
