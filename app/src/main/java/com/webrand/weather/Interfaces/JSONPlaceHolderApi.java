package com.webrand.weather.Interfaces;


import com.webrand.weather.Model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    @GET("data/2.5/forecast?")
    Call<WeatherResponse> getForecastWeatherByCoordinates(@Query("lat") double lat,
                                                          @Query("lon") double lon,
                                                          @Query("units") String unit,
                                                          @Query("APPID") String appId);
    @GET("data/2.5/forecast?")
    Call<WeatherResponse> getForecastWeatherByName(@Query("q") String city,
                                                   @Query("units") String unit,
                                                   @Query("APPID") String appId);
}
