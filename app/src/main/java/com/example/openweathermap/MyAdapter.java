package com.example.openweathermap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<City> cities;

    public MyAdapter(Context context, List<City> cities) {
        this.context = context;
        this.cities = cities;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.country_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.cityName.setText(cities.get(position).name);
        holder.temperature.setText(cities.get(position).temperature);
        holder.wind.setText(cities.get(position).wind);
        holder.description.setText(cities.get(position).description);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }
}
