package vn.edu.fpt.service;

import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.FavouriteEvent;
import vn.edu.fpt.modelview.response.homepage.EventHomeDTO;

import java.util.List;

public interface FavouriteEventService {
    boolean checkUserHaveFavouriteEvent(Long userId);
    boolean checkFavouriteEventByUserId(Long userId, Long eventId);
    FavouriteEvent handleAddEventToFavouriteEvent(Long userId, Long eventId);
    List<EventHomeDTO> findAllByUserId(Long userId);
    List<Long> findFavouriteCategoryByUserId(Long userId);
    void removeFavourite(Long userId, Long eventId);
}
