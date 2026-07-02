package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.admin.CountEventByMonthDTO;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.modelview.request.organizer.OrganizerProfileDto;
import vn.edu.fpt.modelview.response.organizer.EventCardDTO;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.modelview.request.organizer.cityDto;
import vn.edu.fpt.modelview.request.organizer.wardDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import vn.edu.fpt.modelview.response.organizer.EventDetailDTO;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.repository.FeaturedEventDTO;
import vn.edu.fpt.repository.SumRevenueByMonthProjection;


public interface EventService {
  void SetStatusEvent();
   EventDTO UpdateEventById(Long id);
   OrganizerProfile GetOrganizerProfileByUserId(Long userId);
    List<cityDto> getListcity();
    List<EventCategory> getListEventCategory();
    List<wardDTO> listWardDtos(Long cityId);
    void saveEvent(EventDTO eventDTO ,OrganizerProfileDto organizerProfileDto);
    long countHostedEvents();
    List<EventSummaryDto> findTopFeaturedEvents();
    FeaturedEventDTO findFeaturedEvent();
    Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable);
    Event getEventById(Long id);
    EventSummaryProjection findEventDetailById(Long id);
    Page<EventCardDTO> getEventCards(Long organizerId, String[] statuses, String keyword, int page);
    List<EventSummaryDto> findTop10Events();
    EventDetailDTO getEventDetailById(Long id);
    long countAllEvent();
    long countAllUseActive();
    long countAllSoldTicket();
    List<CountEventByMonthDTO> countEventByMonth();
    List<SumRevenueByMonthProjection> sumRevenueByMonth();
    List<EventSummaryDto> findTop5EventsBySoldCount();
    long countUpcomingEvent(@Param("userId") Long userId);
    long countAttendedEvent(@Param("userId") Long userId);



}
