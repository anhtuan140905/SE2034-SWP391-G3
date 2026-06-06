package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.model.FavouriteEvent;

public interface FavouriteEventRepository extends JpaRepository<FavouriteEvent, Integer> {
//    long countById_UserId(Long userId);
}
