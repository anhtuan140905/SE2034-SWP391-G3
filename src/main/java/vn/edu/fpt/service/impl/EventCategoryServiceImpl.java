package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.repository.EventCategoryRepository;
import vn.edu.fpt.service.EventCategoryService;

import java.util.List;

@Service("EventCategory")
@AllArgsConstructor
public class EventCategoryServiceImpl implements EventCategoryService {
    private final EventCategoryRepository eventCategoryRepository;
    @Override
    public long countEventCategories() {
        return this.eventCategoryRepository.countEventCategories();
    }

    @Override
    public List<EventCategory> listEventCategories() {
        return this.eventCategoryRepository.findAll();
    }


}
