package vn.edu.fpt.service.impl;

import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.repository.CreateEventRepository;
import vn.edu.fpt.service.EventService;

import java.time.LocalDate;
import java.util.List;

public class EventServiceImpl implements EventService {
    private CreateEventRepository createEventRepository;
    @Override
    public Event createEvent(Event event) {
        return null;
    }

    @Override
    public List<Venue> findByDateNot(LocalDate dates) {
        List<Venue> venues = createEventRepository.findByDateNot(dates);
        return venues;

    }


}
