package vn.edu.fpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE " +
            "(:status IS NULL OR e.status = :status) " +
            "AND " +
            "(:categoryId IS NULL OR e.category.categoryId = :categoryId) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR e.title LIKE CONCAT('%', :keyword, '%'))")
    Page<Event> searchAndFilterEvents(
            @Param("keyword") String keyword,
            @Param("status") EventStatus status,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    long countByStatus(EventStatus status);
}
