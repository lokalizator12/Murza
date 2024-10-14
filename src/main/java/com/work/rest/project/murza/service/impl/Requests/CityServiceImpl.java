package com.work.rest.project.murza.service.impl.Requests;

import com.work.rest.project.murza.entity.Requests.City;
import com.work.rest.project.murza.exception.CityNotFoundException;
import com.work.rest.project.murza.repository.CityRepository;
import com.work.rest.project.murza.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public City getCityById(Long cityId) {
        Optional<City> city = cityRepository.findById(cityId);
        return city.orElseThrow(() -> new CityNotFoundException(cityId.toString()));
    }

    @Override
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}
