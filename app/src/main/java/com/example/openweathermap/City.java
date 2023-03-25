package com.example.openweathermap;

public class City {
    String name;
    String temperature;
    String wind;
    String description;

    City(String name, String temperature, String wind, String description) {
        this.name = name;
        this.temperature = temperature;
        this.wind = wind;
        this.description = description;
    }
}
