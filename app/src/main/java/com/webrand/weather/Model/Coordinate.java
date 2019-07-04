package com.webrand.weather.Model;

import com.google.gson.annotations.SerializedName;

public class Coordinate {
    @SerializedName("lat")
    double latitude;

    @SerializedName("lon")
    double longitude;
}
