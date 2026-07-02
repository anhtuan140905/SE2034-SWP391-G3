package vn.edu.fpt.configuration;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.fpt.service.EventService;

@Component
@AllArgsConstructor
public class EventStatusScheduler {
    private EventService eventService;
    @PostConstruct
    public void onStartup() {
        eventService.SetStatusEvent();
    }

    @Scheduled(fixedRate = 30000)
    public void onSchedule() {
        eventService.SetStatusEvent();
    }
}
