package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.TimeLineEvent;
import vn.edu.fpt.repository.TimeLineRepository;
import vn.edu.fpt.service.TimeLineEventService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeLineEventServiceImpl implements TimeLineEventService {

    private final TimeLineRepository timeLineRepository;

    @Override
    public List<TimeLineEvent> findByEvent_EventIdOrderByTimeAsc(Long eventId) {
        return this.timeLineRepository.findByEvent_EventIdOrderByTimeAsc(eventId);
    }
}
