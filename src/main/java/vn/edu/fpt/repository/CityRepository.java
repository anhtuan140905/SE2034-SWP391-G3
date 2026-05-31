package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.model.City;

public interface CityRepository extends JpaRepository<City,Long> {
    City findByName(String name);

    City getCityById(Long id);
}
