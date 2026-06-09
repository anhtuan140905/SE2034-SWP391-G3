package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.EventCategory;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory,Long> {

    @Query("SELECT COUNT(*) FROM EventCategory")
    long countEventCategories();
}
