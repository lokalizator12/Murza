package com.work.rest.project.murza.service;

import com.work.rest.project.murza.entity.Requests.City;
import com.work.rest.project.murza.entity.Requests.TripIntermediateCity;

import java.util.List;

public interface CityService {

    public City getCityById(Long cityId);

    public List<City> getCitiesByIds(List<Long> cities);
    public City createCity(City city);

    public List<City> getAllCities();
}
