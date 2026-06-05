package vn.edu.fpt.service;

import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    List<Venue> findByDateNot(LocalDate dates);

    EventDetailModeratorDTO getEventDetailById(Long id);
}