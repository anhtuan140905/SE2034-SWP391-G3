package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.FavouriteEvent;
import vn.edu.fpt.model.FavouriteEventId;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.response.homepage.EventHomeDTO;
import vn.edu.fpt.repository.FavouriteEventRepository;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.FavouriteEventService;
import vn.edu.fpt.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavouriteEventServiceImpl implements FavouriteEventService {

    private final FavouriteEventRepository favouriteEventRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public boolean checkUserHaveFavouriteEvent(Long userId) {
        return this.favouriteEventRepository.existsByUserId(userId);
    }

    @Override
    public boolean checkFavouriteEventByUserId(Long userId, Long eventId) {
        return this.favouriteEventRepository.existsByUserIdAndEventEventId(userId, eventId);
    }

    @Override
    @Transactional
    public FavouriteEvent handleAddEventToFavouriteEvent(Long userId, Long eventId) {
        if(!this.favouriteEventRepository.existsByUserIdAndEventEventId(userId, eventId)) {
            FavouriteEvent favouriteEvent = new FavouriteEvent();
            Event event = this.eventService.getEventById(eventId);
            if(event == null) {
                throw new IllegalArgumentException("Event không tồn tại!");
            }
            User user = this.userService.getUserById(userId);
            if(user == null) {
                throw new IllegalArgumentException("Vui lòng đăng nhập để tiếp tục!");
            }
            FavouriteEventId feventId = new FavouriteEventId(user.getId(), eventId);
            favouriteEvent.setId(feventId);
            favouriteEvent.setUser(user);
            favouriteEvent.setEvent(event);
            return this.favouriteEventRepository.save(favouriteEvent);
        }
        return null;
    }

    @Override
    public List<EventHomeDTO> findAllByUserId(Long userId) {
        List<FavouriteEvent> favouriteEvents= this.favouriteEventRepository.findAllByUserId(userId);
        List<Event> events = new ArrayList<>();
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (FavouriteEvent favouriteEvent : favouriteEvents) {
            events.add(favouriteEvent.getEvent());
        }
        for (Event event : events) {
            EventHomeDTO eventHomeDTO = this.eventService.getFavouriteEvent(event.getEventId());
            eventHomeDTOs.add(eventHomeDTO);
        }
        return eventHomeDTOs;
    }

    @Override
    public List<Long> findFavouriteCategoryByUserId(Long userId) {
        return this.favouriteEventRepository.findFavouriteCategoryIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void removeFavourite(Long userId, Long eventId) {
        this.favouriteEventRepository.deleteByUserIdAndEventEventId(userId, eventId);
    }
}
