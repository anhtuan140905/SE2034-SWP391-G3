package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.constant.EventStatus;

import vn.edu.fpt.modelview.response.moderator.DashboardStatsDTO;

import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.repository.EventRepository;

@Service("EventService")
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private EventCategoryRepository eventCategoryRepository;

    private UserRepository userRepository;
    private CloudinaryService cloudinaryService;
    private TicketRepository ticketRepository;

//    @Override
//    public List<EventCategory> getListEventCategory() {
//        List<EventCategory> listAllEventCategory = eventCategoryRepository.findAll();
//        return listAllEventCategory;
//    }
//
//    @Override
//    public long countHostedEvents() {
//        return this.eventRepository.countHostedEvents(List.of(EventStatus.ACTIVE, EventStatus.ENDED));
//    }
//
//    @Override
//    public List<EventSummaryDto> findTopFeaturedEvents() {
//        List<EventSummaryProjection> projections = this.eventRepository.findTopFeaturedEvents();
//
//        return projections.stream().map(EventSummaryDto::new).collect(Collectors.toList());
//    }
//
//    @Override
//    public FeaturedEventDTO findFeaturedEvent() {
//        return this.eventRepository.findFeaturedEvent();
//    }

//    @Override
//    public EventDetailModeratorDTO getEventDetailById(Long eventId) {
//
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
//
//        EventDetailModeratorDTO eventDetailModeratorDTO = new EventDetailModeratorDTO();
//        eventDetailModeratorDTO.setId(event.getEventId());
//        eventDetailModeratorDTO.setTitle(event.getTitle());
//        eventDetailModeratorDTO.setStatus(event.getStatus().name());
//        eventDetailModeratorDTO.setCoverImageUrl(event.getThumbnailUrl());
//
//        // lay ten category
//        eventDetailModeratorDTO.setCategoryId(event.getCategory().getCategoryId());
//        eventDetailModeratorDTO.setCategory(event.getCategory().getCategoryName());
//        eventDetailModeratorDTO.setDescription(event.getDescription());
//        eventDetailModeratorDTO.setStartTime(event.getStartTime());
//
//        // Tinh toan thoi luong Duration
//        if (event.getStartTime() != null && event.getEndTime() != null) {
//            long hours = Duration.between(event.getStartTime(), event.getEndTime()).toHours();
//            eventDetailModeratorDTO.setDuration(hours + " hours");
//        } else {
//            eventDetailModeratorDTO.setDuration("N/A");
//        }
//
//        // Mapping venue (Venue & Address)
//        if (event.getVenue() != null) {
//            eventDetailModeratorDTO.setVenueName(event.getVenue().getVenueName());
//            eventDetailModeratorDTO.setCapacity(event.getVenue().getCapacity());
//            eventDetailModeratorDTO.setTotalTickets(event.getVenue().getCapacity());
//
//            if (event.getVenue().getAddress() != null) {
//                eventDetailModeratorDTO.setVenueAddress(event.getVenue().getAddress().getSpecificAddress());
//            }
//        }
//
//        // Mapping organizer (Organizer / User)
//        if (event.getOrganizer() != null) {
//            eventDetailModeratorDTO.setOrganizerId(event.getOrganizer().getId());
//            String fullName = event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName();
//            eventDetailModeratorDTO.setOrganizerName(fullName);
//            eventDetailModeratorDTO.setOrganizerAvatarUrl(event.getOrganizer().getAvatar());
//        }
//
//        //truong gia dinh (se dung khi database update them cot)
//        eventDetailModeratorDTO.setRejectReason("");
//
//        return eventDetailModeratorDTO;
//    }

//    @Override
//    public List<Event> findEventbyVenueID(Long id) {
//        return eventRepository.findByVenue_VenueId(id);
//    }

//    @Override
//    public Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
//        Specification<Event> spec = (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            predicates.add(cb.equal(root.get("status"), EventStatus.ACTIVE));
//            predicates.add(cb.greaterThan(root.get("endTime"), LocalDateTime.now()));
//
//            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
//                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getKeyword().toLowerCase() + "%"));
//            }
//
//            if (criteria.getCategory() != null && !criteria.getCategory().trim().isEmpty()) {
//                Join<Event, EventCategory> categoryJoin = root.join("category", JoinType.INNER);
//                predicates.add(cb.equal(cb.lower(categoryJoin.get("categoryName")), criteria.getCategory().toLowerCase()));
//            }
//
//            if (criteria.getCity() != null && !criteria.getCity().trim().isEmpty() && !criteria.getCity().equals("all")) {
//
//                Join<Event, Venue> venueJoin = root.join("venue", JoinType.INNER);
//
//                Join<Venue, Address> addressJoin = venueJoin.join("address", JoinType.INNER);
//
//                Join<Address, Ward> wardJoin = addressJoin.join("ward", JoinType.INNER);
//
//                Join<Ward, City> cityJoin = wardJoin.join("city", JoinType.INNER);
//
//                predicates.add(cb.equal(cb.lower(cityJoin.get("name")), criteria.getCity().toLowerCase()));
//            }
//
//            if (criteria.getMonth() != null && !criteria.getMonth().trim().isEmpty() && !criteria.getMonth().equals("all")) {
//                try {
//                    String[] parts = criteria.getMonth().split("-");
//                    int year = Integer.parseInt(parts[0]);
//                    int month = Integer.parseInt(parts[1]);
//
//                    LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
//                    LocalDateTime endOfMonth = startOfMonth.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())
//                            .withHour(23).withMinute(59).withSecond(59);
//
//                    predicates.add(cb.between(root.get("startTime"), startOfMonth, endOfMonth));
//                } catch (Exception e) {
//                    System.err.println("Lỗi parse định dạng tháng: " + e.getMessage());
//                }
//            }
//
//
//            if (criteria.getPrice() != null && !criteria.getPrice().trim().isEmpty() && !criteria.getPrice().equals("all")) {
//
//                Subquery<Double> subquery = query.subquery(Double.class);
//
//                Root<TicketType> ticketRoot = subquery.from(TicketType.class);
//
//                subquery.select(cb.min(ticketRoot.get("price")));
//
//                subquery.where(cb.equal(ticketRoot.get("event"), root));
//
//                Expression<Double> minPriceExpr = subquery;
//
//                switch (criteria.getPrice()) {
//                    case "free":
//                        predicates.add(cb.equal(minPriceExpr, 0D));
//                        break;
//                    case "under200":
//                        predicates.add(cb.lessThanOrEqualTo(minPriceExpr, 200000D));
//                        break;
//                    case "200to1000":
//                        predicates.add(cb.between(minPriceExpr, 200000D, 1000000D));
//                        break;
//                    case "over1000":
//                        predicates.add(cb.greaterThan(minPriceExpr, 1000000D));
//                        break;
//                }
//            }
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//
//        return eventRepository.findAll(spec, pageable);
//    }

//    @Override
//    public EventSummaryProjection findEventDetailById(Long id) {
//        return this.eventRepository.findEventDetailById(id);
//    }




//    @Override
//    public Page<EventCardDTO> getEventCards(Long organizerId, String[] statuses, String keyword, int page) {
//        // Chuẩn hoá: bỏ "ALL", bỏ null, trim khoảng trắng
//        List<String> statusList = statuses == null
//                ? List.of()
//                : Arrays.stream(statuses)
//                .filter(s -> s != null && !s.isBlank() && !s.equalsIgnoreCase("ALL"))
//                .map(String::toUpperCase)
//                .distinct()
//                .collect(Collectors.toList());
//
//        Pageable pageable = PageRequest.of(
//                Math.max(page - 1, 0),
//                9
//        );
//        Page<Event> entityPage = this.eventRepository
//                .findByMultiStatusAndKeyword(organizerId, statusList, keyword, pageable);
//        return entityPage.map(this::toDTO);
//    }

//    private EventCardDTO toDTO(Event event) {
//        EventCardDTO dto = new EventCardDTO();
//        dto.setId(event.getEventId());
//        dto.setEventName(event.getTitle());
//        dto.setThumnail(event.getThumbnailUrl());
//        dto.setDate(event.getDate());
//        dto.setStartime(event.getStartTime().toLocalTime());
//        dto.setEndtime(event.getEndTime().toLocalTime());
//        dto.setStatusEvent(event.getStatus().name());
//        dto.setEventCatagory(event.getCategory().getCategoryName());
//        dto.setVenueName(event.getVenue().getVenueName());
//
//        List<TicketType> ticketTypes = event.getTicketTypes();
//        int stock = 0;
//        int numSelled = 0;
//        for (TicketType tt : ticketTypes) {
//            stock += tt.getStock();
//            numSelled += tickRepository.getNumTicketSelled(tt.getTicketTypeId());
//        }
//
//        dto.setStock(stock);
//        dto.setTicketSelled(numSelled);
//        dto.setPercent(stock == 0 ? 0 : numSelled * 100 / stock);
//
//        return dto;
//    }


}


