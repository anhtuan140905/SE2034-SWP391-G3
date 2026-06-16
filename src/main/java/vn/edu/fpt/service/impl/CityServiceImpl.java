package vn.edu.fpt.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.City;
import vn.edu.fpt.repository.CityRepository;
import vn.edu.fpt.service.CityService;

import java.util.List;

@Service("CityService")
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City getCityByName(String name) {
        return this.cityRepository.findByName(name);
    }

    @Override
    public List<City> getCityList() {
        return this.cityRepository.findAll();
    }

    @Override
    public City getCityById(Long id) {
        return this.cityRepository.getCityById(id);
    }

//    @Override
//    public List<City> getListCityHaveApprovedEvents() {
//        return this.cityRepository.findAllCityHaveApprovedEvent();
//    }
}
