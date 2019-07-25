package com.webrand.weather;

import com.webrand.weather.Helper.NetworkService;

import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NetworkUnitTest {
    private String appId="b03326d8c668ab2a608b6e86c61ee31c";
    private String unit="metric";

    @Test
    public void testNetworkByName() throws IOException {
        String testCity = "Bishkek";
        String expectedCity =  Objects.requireNonNull(NetworkService.getInstance()
              .getJSONApi()
              .getForecastWeatherByName(testCity,unit,appId)
              .execute()
              .body())
                .city.name;
      assertEquals(expectedCity, testCity);
    }

    @Test
    public void testNetworkByLatLon() throws IOException {
        double lat = 42.882004;
        double lon = 74.582748;
        boolean result = NetworkService
                .getInstance()
                .getJSONApi()
                .getForecastWeatherByCoordinates(lat, lon,unit,appId)
                .execute()
                .isSuccessful();

        assertTrue(result);
    }
}