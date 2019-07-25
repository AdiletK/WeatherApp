package com.webrand.weather.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;
import com.webrand.weather.Model.WeatherData;
import com.webrand.weather.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<WeatherData> weatherDataArrayList;

    private static final String IMG_URL = "https://openweathermap.org/img/w/";

    public WeatherAdapter(Context ctx, ArrayList<WeatherData> weatherDataArrayList){

        inflater = LayoutInflater.from(ctx);
        this.weatherDataArrayList = weatherDataArrayList;
    }

    @Override
    public WeatherAdapter.MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder( WeatherAdapter.MyViewHolder holder, int position) {
        String output = ConvertDateToString(position);

        holder.day.setText(output);
        holder.temp.setText(String.valueOf(weatherDataArrayList.get(position).main.temp));
        Picasso.get()
                .load(IMG_URL + weatherDataArrayList.get(position).skyPosition.get(0).icon +".png")
                .into(holder.icon);
    }

    @Nullable
    private String ConvertDateToString(int position) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String output = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(weatherDataArrayList.get(position).date)));
            output = calendar.get(Calendar.DAY_OF_MONTH) +"/"+ calendar.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public int getItemCount() {
        return weatherDataArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView day;
        TextView temp;
        ImageView icon;

        MyViewHolder(View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.day);
            temp = itemView.findViewById(R.id.day_temp);
            icon = itemView.findViewById(R.id.day_icon);
        }

    }
}