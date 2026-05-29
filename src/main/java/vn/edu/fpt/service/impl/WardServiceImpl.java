package vn.edu.fpt.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.City;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.repository.WardRepository;
import vn.edu.fpt.service.WardService;

import java.util.List;

@Service("WardService")
public class WardServiceImpl implements WardService {
    private final WardRepository wardRepository;

    public WardServiceImpl(WardRepository wardRepository) {
        this.wardRepository = wardRepository;
    }

    public Ward findByNameAndCity(String name, City city) {
        return findByNameAndCity(name, city);
    }

    @Override
    public List<Ward> findByCityId(Long cityId) {
        return this.wardRepository.findByCityId(cityId);
    }

}
