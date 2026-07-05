package vn.edu.fpt.service;

import vn.edu.fpt.model.TimeLineEvent;

import java.util.List;

public interface TimeLineEventService {
    List<TimeLineEvent> findByEvent_EventIdOrderByTimeAsc(Long eventId);
}
