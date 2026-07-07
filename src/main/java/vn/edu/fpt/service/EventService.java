package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Bank;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.admin.CountEventByMonthDTO;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.modelview.request.organizer.OrganizerProfileDto;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.modelview.request.admin.CountEventByMonthDTO;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.modelview.response.homepage.EventHomeDTO;
import vn.edu.fpt.modelview.response.homepage.EventSearchResultDTO;
import vn.edu.fpt.modelview.response.organizer.BankDto;
import vn.edu.fpt.modelview.response.organizer.EventCardDTO;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.modelview.request.organizer.cityDto;
import vn.edu.fpt.modelview.request.organizer.wardDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import vn.edu.fpt.modelview.response.organizer.EventDetailDTO;
import vn.edu.fpt.modelview.response.organizer.EventEditDTO;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.repository.FeaturedEventDTO;
import vn.edu.fpt.repository.SettlementSummaryProjection;
import vn.edu.fpt.repository.SumRevenueByMonthProjection;


public interface EventService {

  void publishEvent(Long eventId);
    void updateEvent(EventEditDTO eventDTO);
  void SetStatusEvent();
    EventEditDTO getEventUpdateById(Long id);
   Boolean GetOrganizerProfileByUserId(Long userId);
   List<BankDto> getListBank();
    List<cityDto> getListcity();
    List<EventCategory> getListEventCategory();
    List<wardDTO> listWardDtos(Long cityId);
    void saveEvent(EventDTO eventDTO ,OrganizerProfileDto organizerProfileDto);
    long countHostedEvents();
    List<EventSummaryDto> findTopFeaturedEvents();
    FeaturedEventDTO findFeaturedEvent();
    Page<EventSearchResultDTO> searchEvents(EventSearchCriteria criteria, Pageable pageable);
    Event getEventById(Long id);
    EventSummaryProjection findEventDetailById(Long id);
 Page<EventCardDTO> getEventCards(Long organizerId, String status, String keyword, int page);
 List<EventSummaryDto> findTop10Events();
    EventDetailDTO getEventDetailById(Long id);
    long countAllEvent();
    long countAllUseActive();
    List<CountEventByMonthDTO> countEventByMonth();
    List<SumRevenueByMonthProjection> sumRevenueByMonth();
    List<EventSummaryDto> findTop5EventsBySoldCount();
    long countUpcomingEvent(@Param("userId") Long userId);
    long countAttendedEvent(@Param("userId") Long userId);
    EventHomeDTO getFavouriteEvent(Long eventId);

    List<SettlementSummaryProjection> findEndedEventsWithSettlementStatus(String tab);
    long countEndedEvent();
    long countUnsettledEvents();
    Long sumTotalRevenue();
    List<SettlementSummaryProjection> searchEndedEvents(@Param("keyword") String keyword);
    List<Event> findCandidateEventsByCategories(
            List<Long> targetCatIds,
            EventStatus eventStatus,
            OrderStatus orderStatus,
            LocalDate today,
            Long userId,
            PageRequest page
    );
    List<Event> findUpcomingEvent(EventStatus status, LocalDate today, PageRequest page);

}
