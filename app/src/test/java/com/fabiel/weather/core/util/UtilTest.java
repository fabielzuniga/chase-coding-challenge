package com.fabiel.weather.core.util;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/** Unit tests for {@link Util} */
public final class UtilTest {

  @Test
  public void equal() {
    assertThat(Util.equal(null, null)).isTrue();
    assertThat(Util.equal("Hello World", "Hello World")).isTrue();
    assertThat(Util.equal("Hello World", "Hello")).isFalse();
    assertThat(Util.equal("Hello World", null)).isFalse();
    assertThat(Util.equal(null, "Hello World")).isFalse();
  }
}
