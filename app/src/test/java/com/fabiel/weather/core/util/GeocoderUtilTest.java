package com.fabiel.weather.core.util;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import android.location.Address;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

/** Unit tests for {@link GeocoderUtil} */
@RunWith(MockitoJUnitRunner.class)
public final class GeocoderUtilTest {

  // TODO: change GeocoderUtil from static methods to instance methods and use constructor injection
  // to inject an instance of android.location.Geocoder (provided by a module). In this way we can
  // create an instance of GeocoderUtil with a mock of Geocoder and test it. Rename GeocoderUtil
  // to GeocoderService.

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private Address addressMock;

  @Test
  public void addressToText_completeAddress() {
    when(addressMock.getFeatureName()).thenReturn("Golden Gate Bridge");
    when(addressMock.getThoroughfare()).thenReturn("12345 street name");
    when(addressMock.getLocality()).thenReturn("City");
    when(addressMock.getAdminArea()).thenReturn("State");
    when(addressMock.getPostalCode()).thenReturn("54321");
    when(addressMock.getCountryName()).thenReturn("Country");
    assertThat(GeocoderUtil.addressToText(addressMock))
        .isEqualTo("Golden Gate Bridge 12345 street name City State 54321 Country");
  }

  @Test
  public void addressToText_partialAddress() {
    when(addressMock.getLocality()).thenReturn("City");
    when(addressMock.getAdminArea()).thenReturn("State");
    when(addressMock.getCountryName()).thenReturn("Country");
    assertThat(GeocoderUtil.addressToText(addressMock))
        .isEqualTo("City State Country");
  }
}
