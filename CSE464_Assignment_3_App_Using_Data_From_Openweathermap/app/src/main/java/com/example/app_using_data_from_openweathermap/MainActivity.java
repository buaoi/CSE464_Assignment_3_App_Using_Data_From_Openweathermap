package com.example.app_using_data_from_openweathermap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "ab76ff98e9bbaea97bdf147cbfc8d193";

    private TextView currentTempTextView, currentConditionTextView, locationTextView;
    private TextView minTextView, maxTextView, feelsTextView, humidityTextView, pressureTextView;
    private EditText searchEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        currentTempTextView = findViewById(R.id.currentTempTextView);
        currentConditionTextView = findViewById(R.id.currentConditionTextView);
        locationTextView = findViewById(R.id.locationTextView);
        minTextView = findViewById(R.id.minTextView);
        maxTextView = findViewById(R.id.maxTextView);
        feelsTextView = findViewById(R.id.feelsTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        // Set up Search Button Click Listener
        searchButton.setOnClickListener(v -> {
            String city = searchEditText.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherData(city);
            } else {
                Toast.makeText(MainActivity.this, "Enter a city name!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeatherData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String response = HttpRequest.excuteGet(url);
            runOnUiThread(() -> {
                if (response != null) {
                    try {
                        updateUI(new JSONObject(response));
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch weather data.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateUI(JSONObject json) {
        try {
            // Extract main data
            JSONObject main = json.getJSONObject("main");
            double temp = main.getDouble("temp");
            double tempMin = main.getDouble("temp_min");
            double tempMax = main.getDouble("temp_max");
            double feelsLike = main.getDouble("feels_like");
            int humidity = main.getInt("humidity");
            int pressure = main.getInt("pressure");

            // Extract weather condition
            String condition = json.getJSONArray("weather").getJSONObject(0).getString("description");

            // Extract location name
            String location = json.getString("name");

            // Update TextViews
            currentTempTextView.setText(String.format("%.1f°C", temp));
            currentConditionTextView.setText(condition);
            locationTextView.setText(location);
            minTextView.setText(String.format("Min: %.1f°C", tempMin));
            maxTextView.setText(String.format("Max: %.1f°C", tempMax));
            feelsTextView.setText(String.format("Feels Like: %.1f°C", feelsLike));
            humidityTextView.setText(String.format("Humidity: %d%%", humidity));
            pressureTextView.setText(String.format("Pressure: %dhPa", pressure));

        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
        }
    }
}
