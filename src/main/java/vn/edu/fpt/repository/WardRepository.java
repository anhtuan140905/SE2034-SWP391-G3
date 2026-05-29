package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.City;
import vn.edu.fpt.model.Ward;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward, Integer> {
    Ward findByNameAndCity(String name, City city);

    List<Ward> findByCityId(Long cityId);
}
