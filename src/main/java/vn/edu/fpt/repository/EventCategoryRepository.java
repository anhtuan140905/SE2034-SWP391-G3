package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.model.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
}