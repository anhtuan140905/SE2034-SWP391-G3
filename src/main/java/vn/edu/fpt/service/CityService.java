package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.City;

import java.util.List;

public interface CityService {
    public City getCityByName(String name);
    public List<City> getCityList();
    public City getCityById(Long id);
//    public List<City> getListCityHaveApprovedEvents();
}
