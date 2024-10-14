package com.work.rest.project.murza.service;

import com.work.rest.project.murza.entity.Requests.City;

import java.util.List;

public interface CityService {

    public City getCityById(Long cityId);

    public City createCity(City city);

    public List<City> getAllCities();
}
