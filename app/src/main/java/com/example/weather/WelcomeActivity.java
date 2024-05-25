package com.example.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private com.example.weather.WeatherService weatherService;

    private TextView welcomeText;
    private EditText cityField;
    private TextView weatherResult;
    private ImageView weatherIcon;
    private Button btnExit;
    private Button btnGetWeather;
    private Button btnGetLocationWeather;
    private String apiKey = "1e7c2a80ffce996efc83dd30e83c70c0";  // Ваш API ключ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weatherService = ApiClient.getClient().create(com.example.weather.WeatherService.class);

        welcomeText = findViewById(R.id.welcomeText);
        cityField = findViewById(R.id.cityField);
        weatherResult = findViewById(R.id.weatherResult);
        weatherIcon = findViewById(R.id.weatherIcon);
        btnExit = findViewById(R.id.btnExit);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        btnGetLocationWeather = findViewById(R.id.btnGetLocationWeather);

        String login = getIntent().getStringExtra("LOGIN");
        welcomeText.setText("Добро пожаловать, " + login);

        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityField.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeatherByCity(city);
                } else {
                    Toast.makeText(WelcomeActivity.this, "Пожалуйста, введите название города", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGetLocationWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherByLocation();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getWeatherByCity(String cityName) {
        weatherService.getWeatherByCity(cityName, apiKey, "metric").enqueue(new Callback<com.example.weather.WeatherResponse>() {
            @Override
            public void onResponse(Call<com.example.weather.WeatherResponse> call, Response<com.example.weather.WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayWeather(response.body());
                } else {
                    Toast.makeText(WelcomeActivity.this, "Не удалось получить данные о погоде", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<com.example.weather.WeatherResponse> call, Throwable t) {
                Toast.makeText(WelcomeActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeatherByLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    weatherService.getWeatherByCoordinates(latitude, longitude, apiKey, "metric").enqueue(new Callback<com.example.weather.WeatherResponse>() {
                        @Override
                        public void onResponse(Call<com.example.weather.WeatherResponse> call, Response<com.example.weather.WeatherResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                displayWeather(response.body());
                            } else {
                                Toast.makeText(WelcomeActivity.this, "Не удалось получить данные о погоде", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<com.example.weather.WeatherResponse> call, Throwable t) {
                            Toast.makeText(WelcomeActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(WelcomeActivity.this, "Не удалось получить местоположение", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayWeather(com.example.weather.WeatherResponse weather) {
        String weatherInfo = "Температура: " + weather.main.temp + "°C\n" +
                "Влажность: " + weather.main.humidity + "%\n" +
                "Скорость ветра: " + weather.wind.speed + "m/s";
        weatherResult.setText(weatherInfo);

        String iconUrl = "https://openweathermap.org/img/w/" + weather.weather[0].icon + ".png";
        Picasso.get().load(iconUrl).into(weatherIcon);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherByLocation();
            } else {
                Toast.makeText(this, "Доступ запрещен", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
