package com.example.openweathermap;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView cityName, temperature, wind, description;

    public MyViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
        super(itemView);
        cityName = itemView.findViewById(R.id.city);
        temperature = itemView.findViewById(R.id.temperature);
        wind = itemView.findViewById(R.id.wind);
        description = itemView.findViewById(R.id.description);
    }
}
