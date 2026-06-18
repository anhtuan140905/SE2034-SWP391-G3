package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.model.City;

import java.util.List;

public interface CityRepository extends JpaRepository<City,Long> {
    City findByName(String name);

    City getCityById(Long id);

//    @Query(value = "SELECT DISTINCT(c.name), c.id FROM events e\n" +
//            "  JOIN venues v ON e.venue_id = v.venue_id\n" +
//            "  JOIN addresses a ON v.address_id = a.id\n" +
//            "  JOIN wards w ON a.ward_id = w.id\n" +
//            "  JOIN city c ON w.city_id = c.id\n" +
//            "  WHERE e.status = 'APPROVED'", nativeQuery = true)
//    List<City> findAllCityHaveApprovedEvent();
}