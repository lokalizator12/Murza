package com.work.rest.project.murza.service;

import com.work.rest.project.murza.entity.Requests.City;

import java.util.List;

public interface CityService {

    City getCityById(Long cityId);

    List<City> getCitiesByIds(List<Long> cities);

    City createCity(City city);

    List<City> getAllCities();
}
