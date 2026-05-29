package vn.edu.fpt.service;

import vn.edu.fpt.model.City;
import vn.edu.fpt.model.Ward;

import java.util.List;

public interface WardService {
    public Ward findByNameAndCity(String name, City city);

    public List<Ward> findByCityId(Long cityId);
}
