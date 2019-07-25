package com.webrand.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.squareup.picasso.Picasso;
import com.webrand.weather.Adapter.WeatherAdapter;
import com.webrand.weather.Helper.NetworkService;
import com.webrand.weather.Helper.Utils;
import com.webrand.weather.Model.WeatherData;
import com.webrand.weather.Model.WeatherResponse;
import com.webrand.weather.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity implements  Toolbar.OnMenuItemClickListener {

    private static final String IMG_URL = "https://openweathermap.org/img/w/";
    private static final String TAG = "MainActivity";

    private String defaultCity = "Bishkek";

    Utils utils;
    boolean isByName = true;

    double latitude;
    double longitude;

    private static final int REQUEST_CODE_PERMISSION = 2;

    ActivityMainBinding binding;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    PlacesClient placesClient;
    List<Place.Field> placeField = Collections.singletonList(Place.Field.NAME);
    AutocompleteSupportFragment places_fragment = new AutocompleteSupportFragment();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.toolbar.inflateMenu(R.menu.menu_main);
        binding.toolbar.setOnMenuItemClickListener(this);


        recyclerView = findViewById(R.id.recycler);
        swipeRefreshLayout = findViewById(R.id.swipeUpRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }

        });

        checkLocationPermission();

        utils = new Utils(this);

        initPlaces();
//        setupPlaceAutoComplete();

        getWeatherByName(defaultCity);

        utils.setLocalNotification();

    }

    private void update() {
        if (isByName) {
            getWeatherByName(defaultCity);
        } else {
            getWeatherByCoordinates();
        }
    }

    private void getCurrentLocation() {
        if (checkLocationPermission()){
            if (utils.checkStatusOfGPS()) {

                FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                try {
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: found location!");
                                Location currentLocation = (Location) task.getResult();
                                if (currentLocation != null) {
                                    latitude = currentLocation.getLatitude();
                                    longitude = currentLocation.getLongitude();
                                    isByName = false;
                                    getWeatherByCoordinates();
                                    showToastMessage("Latitude:" + latitude + " \nLongitude:" + longitude);
                                }
                            } else {
                                Log.d(TAG, "onComplete: current location is null");
                                Toast.makeText(MainActivity.this, "Could'nt get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } catch (SecurityException e) {
                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
                }
            }
            else {
                utils.showSettingsDialog();
            }
        }else {
            showToastMessage("Permission denied");
        }
    }

    private void setupPlaceAutoComplete() {
        places_fragment = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.text_autocomplete);
        Objects.requireNonNull(places_fragment).setPlaceFields(placeField);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                defaultCity = place.getName();
                isByName = true;
                getWeatherByName(place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {
                showToastMessage(getResources().getString(R.string.msg_cant_find_city));
            }
        });
    }

    private void initPlaces() {
//        Places.initialize(getApplicationContext(),getString(R.string.key_api));
//        placesClient = Places.createClient(this);
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.key_api));
        }

// Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.text_autocomplete);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Toast.makeText(MainActivity.this, "Place: " + place.getName() + ", " + place.getId(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private boolean checkLocationPermission() {
        try {
            String mPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != RESULT_OK) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    void getWeatherByCoordinates() {
        NetworkService
                .getInstance()
                .getJSONApi()
                .getForecastWeatherByCoordinates(
                        latitude,
                        longitude,
                        getResources().getString(R.string.unit),
                        getResources().getString(R.string.appId))
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        WeatherResponse  weatherResponse = response.body();
                        if (weatherResponse != null) {
                            setDataToView(weatherResponse);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call,@NonNull Throwable t) {
                        t.printStackTrace();
                        showToastMessage("Error occurred while getting request!");
                    }
                });
    }

    void getWeatherByName(String city){
        NetworkService
                .getInstance()
                .getJSONApi()
                .getForecastWeatherByName(
                        city,
                        getResources().getString(R.string.unit),
                        getResources().getString(R.string.appId))
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        WeatherResponse  weatherResponse = response.body();
                        if (weatherResponse != null) {
                            setDataToView(weatherResponse);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call,@NonNull Throwable t) {
                        t.printStackTrace();
                        showToastMessage("Error occurred while getting request!");
                    }
                });
    }

    private void setDataToView(WeatherResponse weatherResponse) {
        ArrayList<WeatherData> fiveDaysData = new ArrayList<>();
        ArrayList<WeatherData> weatherData ;
        weatherData = weatherResponse.weatherData;
        int RANGE_OF_DAY=8;
        for (int i = 0; i<weatherData.size(); i+=RANGE_OF_DAY){
            fiveDaysData.add(weatherData.get(i));
        }

        setCurrentDayWeather(weatherResponse, fiveDaysData.get(0));

        setRecyclerView(fiveDaysData);

        showToastMessage("Succeed");
    }

    private void setRecyclerView(ArrayList<WeatherData> fiveDaysData) {
        WeatherAdapter adapter = new WeatherAdapter(this, fiveDaysData);
        recyclerView.setAdapter(adapter);
        int COLUMN_COUNT=5;
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMN_COUNT));
    }

    private void setCurrentDayWeather(WeatherResponse weatherResponse, WeatherData currentDay) {
        binding.cityName.setText(weatherResponse.city.name);
        binding.valueOfTemp.setText(String.valueOf(currentDay.main.temp));
        binding.weatherPosition.setText(String.valueOf(currentDay.skyPosition.get(0).main));
        binding.speedWind.setText(String.valueOf(currentDay.wind.speed));
        binding.countOfClouds.setText(String.valueOf(currentDay.clouds.count));
        binding.pressure.setText(String.valueOf(currentDay.main.pressure));
        binding.humidity.setText(String.valueOf(currentDay.main.humidity)) ;
        Picasso.get()
                .load(IMG_URL + currentDay.skyPosition.get(0).icon +".png")
                .into(binding.weatherImage);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.current_location){
            showToastMessage("Search");
            getCurrentLocation();
        }
        return true;
    }

    private void showToastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }



}
