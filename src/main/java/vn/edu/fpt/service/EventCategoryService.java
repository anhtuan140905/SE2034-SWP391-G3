package vn.edu.fpt.service;

import vn.edu.fpt.model.EventCategory;

import java.util.List;

public interface EventCategoryService {
    long countEventCategories();

    List<EventCategory> listEventCategories();
}
