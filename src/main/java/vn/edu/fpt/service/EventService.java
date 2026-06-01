package vn.edu.fpt.service;

import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Venue;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    List<Venue> findByDateNot(LocalDate dates);

}