package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.FavouriteEvent;
import vn.edu.fpt.model.User;

import java.util.List;

@Repository
public interface FavouriteEventRepository extends JpaRepository<FavouriteEvent, Long> {
    boolean existsByUserId(Long userId);
    boolean existsByUserIdAndEventEventId(Long userId, Long eventId);
    List<FavouriteEvent> findAllByUserId(Long userId);
    @Query("SELECT DISTINCT e.category.categoryId " +
            "FROM FavouriteEvent fe " +
            "JOIN fe.event e " +
            "WHERE fe.user.id = :userId")
    List<Long> findFavouriteCategoryIdsByUserId(@Param("userId") Long userId);
    void deleteByUserIdAndEventEventId(Long userId, Long eventId);
}
