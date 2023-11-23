package com.fabiel.weather.core.util;

import com.google.android.gms.common.internal.Objects;

/** Utility methods. */
public final class Util {
  private Util() {
  }

  /**
   * Verifies if the two objects are equal considering {@code null}.
   *
   * @param obj1 one of the objects to compare
   * @param obj2 the other object to compare
   * @param <T> type of the objects to compare
   * @return {@code true} if both objects are either {@code null} or equals (as stated in {@link
   *     Object#equals(Object)}), {@code false} otherwise
   */
  public static <T> boolean equal(T obj1, T obj2) {
    return Objects.equal(obj1, obj2);
  }
}
