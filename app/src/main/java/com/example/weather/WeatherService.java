package com.example.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("weather")
    Call<com.example.weather.WeatherResponse> getWeatherByCity(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("weather")
    Call<com.example.weather.WeatherResponse> getWeatherByCoordinates(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}

