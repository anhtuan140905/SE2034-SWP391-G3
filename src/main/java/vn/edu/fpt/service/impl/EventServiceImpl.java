package vn.edu.fpt.service.impl;

import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.model.constant.TicketStatus;
import vn.edu.fpt.modelview.request.moderator.DashboardStatsDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.repository.FeaturedEventDTO;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.model.*;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;
import vn.edu.fpt.modelview.request.moderator.DashboardStatsDTO;
import vn.edu.fpt.modelview.request.organizer.*;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.EventService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("EventService")
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    private EventRepository eventRepository;
    private EventCategoryRepository eventCategoryRepository;
    private VenueRepository venueRepository;
    private VenueZoneRepository venueZoneRepository;
    private UserRepository userRepository;
    private CloudinaryService cloudinaryService;
    private TickRepository tickRepository;
    @Override
    public List<EventCategory> getListEventCategory() {
        List<EventCategory> listAllEventCategory = eventCategoryRepository.findAll();
        return listAllEventCategory;
    }

    @Override
    public List<VenueZoneOrganizerDTO> getVenueZoneByVenueId(Long id) {
        List<VenueZone> venueZones = venueZoneRepository.findByVenueVenueId(id);
        List<VenueZoneOrganizerDTO> VenueZoneOrganizerDTOS = new ArrayList<>();
        for (VenueZone venueZone : venueZones) {
            VenueZoneOrganizerDTO dto = new VenueZoneOrganizerDTO();
            dto.setZoneID(venueZone.getZoneId());
            dto.setZoneName(venueZone.getZoneName());
            dto.setRows(venueZone.getRows());
            dto.setSeatsPerRow(venueZone.getSeatsPerRow());
            VenueZoneOrganizerDTOS.add(dto);
        }
        return VenueZoneOrganizerDTOS;
    }

    @Override
    public List<VenueDto> findByDateNot(LocalDate dates) {
        List<Venue> venues = venueRepository.findAvailableVenuesByDate(dates);
        List<VenueDto> venueDtos = new ArrayList<>();
        for (Venue venue : venues) {
            VenueDto dto = new VenueDto();
            dto.setVenueID(venue.getVenueId());
            dto.setVenueName(venue.getVenueName());
            dto.setCapacity(venue.getCapacity());
            dto.setDescription(venue.getDescription());
            dto.setImageUrl(venue.getImageUrl());
            venueDtos.add(dto);
        }
        return venueDtos;
    }

    @Override
    public VenueDto getVenuebyId(Long venueID) {
        Venue venue = venueRepository.findById(venueID)
                .orElseThrow(() -> new RuntimeException("Venue Not Found with: " + venueID));
        VenueDto venueDto = new VenueDto();
        venueDto.setVenueID(venue.getVenueId());
        venueDto.setVenueName(venue.getVenueName());
        AddressDto addressDto = new AddressDto();
        addressDto.setSpecificAddress(venue.getAddress().getSpecificAddress());
        wardDTO wardDTO = new wardDTO();
        wardDTO.setName(venue.getAddress().getWard().getName());
        cityDto cityDto = new cityDto();
        cityDto.setId(venue.getAddress().getWard().getCity().getId());
        cityDto.setName(venue.getAddress().getWard().getCity().getName());
        wardDTO.setCity(cityDto);
        addressDto.setWard(wardDTO);
        venueDto.setAddress(addressDto);
        return venueDto;
    }

    @Override
    public void saveEvent(EventDTO eventDTO) {
        EventCategory eventCategory = eventCategoryRepository.findById(eventDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("EventCategory Not Found with: " + eventDTO.getCategoryId()));
        User organizer = userRepository.findById(eventDTO.getOrganizerDtoID())
                .orElseThrow(() -> new RuntimeException("Organizer Not Found with: " + eventDTO.getOrganizerDtoID()));
        Venue venue = venueRepository.findById(eventDTO.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue Not Found with: " + eventDTO.getVenueId()));
                .orElseThrow(()->new RuntimeException("EventCategory Not Found with: "+eventDTO.getCategoryId()));
        User organizer =  userRepository.findById(eventDTO.getOrganizerDtoID())
                .orElseThrow(()-> new RuntimeException("Organizer Not Found with: " + eventDTO.getOrganizerDtoID()));
        Venue venue = venueRepository.findById(eventDTO.getVenueId())
                .orElseThrow(()-> new RuntimeException("Venue Not Found with: "+ eventDTO.getVenueId()));
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setCategory(eventCategory);
        event.setVenue(venue);
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setDate(eventDTO.getEventDate());
        event.setStartTime(LocalDateTime.of(eventDTO.getEventDate(), eventDTO.getStartTime()));
        event.setEndTime(LocalDateTime.of(eventDTO.getEventDate(), eventDTO.getEndTime()));
        event.setCreatedBy(organizer.getLastName() + organizer.getMiddleName() + organizer.getFirstName());
        event.setStatus(EventStatus.PENDING);

        if (eventDTO.getThumbnailFile() != null
                && !eventDTO.getThumbnailFile().isEmpty()) {
            String thumbnailUrl = cloudinaryService.uploadFile(eventDTO.getThumbnailFile(), "EventBanner");
            event.setThumbnailUrl(thumbnailUrl);
        }
        List<EventImage> eventImages = new ArrayList<>();
        if (eventDTO.getImageFiles() != null) {
            for (MultipartFile file : eventDTO.getImageFiles()) {
                if (file.isEmpty()) {
                    continue;
                }
                String imageurl = cloudinaryService.uploadFile(file, "EventImg");
                EventImage image = new EventImage();
                image.setEvent(event);
                image.setCreatedBy(organizer.getLastName() + organizer.getMiddleName() + organizer.getFirstName());
                image.setImageUrl(imageurl);
                eventImages.add(image);
            }
            event.setImages(eventImages);

        }
        List<TicketType> ticketTypes = new ArrayList<>();
        if(eventDTO.getTicketTypes()!=null){
            for(TicketTypeRequestDTO ticketTypeDto:  eventDTO.getTicketTypes()){
                TicketType type = new TicketType();
                VenueZone zone = venueZoneRepository.findById(ticketTypeDto.getZoneID())
                        .orElseThrow(()->new RuntimeException("Zone not found with ID: " + ticketTypeDto.getZoneID()));
                type.setZone(zone);
                type.setEvent(event);
                type.setTypeName(ticketTypeDto.getTypeName());
                type.setPrice(ticketTypeDto.getPrice());
                type.setCreatedBy(organizer.getEmail());
                type.setStock(Math.toIntExact(ticketTypeDto.getStock()));
                type.setDescription(ticketTypeDto.getDescription());
                ticketTypes.add(type);

            }
        }
        event.setTicketTypes(ticketTypes);
        eventRepository.save(event);
        CreateTicket(ticketTypes,organizer.getEmail());
    }
    public void CreateTicket(List<TicketType> ticketTypeList,String emailUser ){
        for (TicketType type : ticketTypeList){
            for (int i = 0; i< type.getStock();i++){
                Ticket ticket = new Ticket();
                ticket.setTicketType(type);
                ticket.setStatus(TicketStatus.UNSOLD);
                ticket.setCreatedBy(emailUser);
                ticket.setIsCheckedIn(false);
                do{
                    String code = UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
                    ticket.setQrCode(type.getTypeName()+code);
                } while(tickRepository.existsByQrCode(ticket.getQrCode()));
                tickRepository.save(ticket);
            }
        }
    }

    @Override
    public long countHostedEvents() {
        return this.eventRepository.countHostedEvents(List.of(EventStatus.APPROVED, EventStatus.ENDED));
    }

    @Override
    public List<EventSummaryDto> findTopFeaturedEvents() {
        List<EventSummaryProjection> projections = this.eventRepository.findTopFeaturedEvents();

        return projections.stream().map(EventSummaryDto::new).collect(Collectors.toList());
    }

    @Override
    public FeaturedEventDTO findFeaturedEvent() {
        return this.eventRepository.findFeaturedEvent();
    }

    @Override
    public EventDetailModeratorDTO getEventDetailById(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        EventDetailModeratorDTO eventDetailModeratorDTO = new EventDetailModeratorDTO();
        eventDetailModeratorDTO.setId(event.getEventId());
        eventDetailModeratorDTO.setTitle(event.getTitle());
        eventDetailModeratorDTO.setStatus(event.getStatus().name());
        eventDetailModeratorDTO.setCoverImageUrl(event.getThumbnailUrl());

        // lay ten category
        eventDetailModeratorDTO.setCategoryId(event.getCategory().getCategoryId());
        eventDetailModeratorDTO.setCategory(event.getCategory().getCategoryName());
        eventDetailModeratorDTO.setDescription(event.getDescription());
        eventDetailModeratorDTO.setStartTime(event.getStartTime());

        // Tinh toan thoi luong Duration
        if (event.getStartTime() != null && event.getEndTime() != null) {
            long hours = Duration.between(event.getStartTime(), event.getEndTime()).toHours();
            eventDetailModeratorDTO.setDuration(hours + " hours");
        } else {
            eventDetailModeratorDTO.setDuration("N/A");
        }

        // Mapping venue (Venue & Address)
        if (event.getVenue() != null) {
            eventDetailModeratorDTO.setVenueName(event.getVenue().getVenueName());
            eventDetailModeratorDTO.setCapacity(event.getVenue().getCapacity());
            eventDetailModeratorDTO.setTotalTickets(event.getVenue().getCapacity());

            if (event.getVenue().getAddress() != null) {
                eventDetailModeratorDTO.setVenueAddress(event.getVenue().getAddress().getSpecificAddress());
            }
        }

        // Mapping organizer (Organizer / User)
        if (event.getOrganizer() != null) {
            eventDetailModeratorDTO.setOrganizerId(event.getOrganizer().getId());
            String fullName = event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName();
            eventDetailModeratorDTO.setOrganizerName(fullName);
            eventDetailModeratorDTO.setOrganizerAvatarUrl(event.getOrganizer().getAvatar());
        }

        //truong gia dinh (se dung khi database update them cot)
        eventDetailModeratorDTO.setRejectReason("");

        return eventDetailModeratorDTO;
    }

    @Override
    public Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getKeyword().toLowerCase() + "%"));
            }

            if (criteria.getCategory() != null && !criteria.getCategory().trim().isEmpty()) {
                Join<Event, EventCategory> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("categoryName")), criteria.getCategory().toLowerCase()));
            }

            if (criteria.getCity() != null && !criteria.getCity().trim().isEmpty() && !criteria.getCity().equals("all")) {

                Join<Event, Venue> venueJoin = root.join("venue", JoinType.INNER);

                Join<Venue, Address> addressJoin = venueJoin.join("address", JoinType.INNER);

                Join<Address, Ward> wardJoin = addressJoin.join("ward", JoinType.INNER);

                Join<Ward, City> cityJoin = wardJoin.join("city", JoinType.INNER);

                predicates.add(cb.equal(cb.lower(cityJoin.get("name")), criteria.getCity().toLowerCase()));
            }

            if (criteria.getMonth() != null && !criteria.getMonth().trim().isEmpty() && !criteria.getMonth().equals("all")) {
                try {
                    String[] parts = criteria.getMonth().split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);

                    LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
                    LocalDateTime endOfMonth = startOfMonth.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())
                            .withHour(23).withMinute(59).withSecond(59);

                    predicates.add(cb.between(root.get("startTime"), startOfMonth, endOfMonth));
                } catch (Exception e) {
                    System.err.println("Lỗi parse định dạng tháng: " + e.getMessage());
                }
            }


            if (criteria.getPrice() != null && !criteria.getPrice().trim().isEmpty() && !criteria.getPrice().equals("all")) {

                Subquery<Double> subquery = query.subquery(Double.class);

                Root<TicketType> ticketRoot = subquery.from(TicketType.class);

                subquery.select(cb.min(ticketRoot.get("price")));

                subquery.where(cb.equal(ticketRoot.get("event"), root));

                Expression<Double> minPriceExpr = subquery;

                switch (criteria.getPrice()) {
                    case "free":
                        predicates.add(cb.equal(minPriceExpr, 0D));
                        break;
                    case "under200":
                        predicates.add(cb.lessThanOrEqualTo(minPriceExpr, 200000D));
                        break;
                    case "200to1000":
                        predicates.add(cb.between(minPriceExpr, 200000D, 1000000D));
                        break;
                    case "over1000":
                        predicates.add(cb.greaterThan(minPriceExpr, 1000000D));
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return eventRepository.findAll(spec, pageable);
    }


    public List<Event> findEventbyVenueID(Long id) {
        return eventRepository.findByVenue_VenueId(id);
    }
    @Override
    public DashboardStatsDTO getDashboardStats() {

        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setPendingEvents(eventRepository.countByStatus(EventStatus.PENDING));
        stats.setActiveEvents(eventRepository.countByStatus(EventStatus.APPROVED));
        stats.setRejectedEvents(eventRepository.countByStatus(EventStatus.REJECTED));

        return stats;
    }

    @Override
    public List<Event> getTopThreePendingEvents() {
        return eventRepository.findByStatusOrderByCreatedAtDesc(
                EventStatus.PENDING,
                org.springframework.data.domain.PageRequest.of(0, 3)
        );
    }

    @Override
    public List<Event> getTodayActiveEvents() {

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        return eventRepository.findByStatusAndStartTimeBetween(
                EventStatus.APPROVED,
                startOfDay,
                endOfDay
        );
    }
    @Override
    public Page<EventCardDTO> getEventCards(Long organizerId,String[] statuses, String keyword, int page) {
        // Chuẩn hoá: bỏ "ALL", bỏ null, trim khoảng trắng
        List<String> statusList = statuses == null
                ? List.of()
                : Arrays.stream(statuses)
                .filter(s -> s != null && !s.isBlank() && !s.equalsIgnoreCase("ALL"))
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(
                Math.max(page - 1, 0),
                9
        );
        Page<Event> entityPage = eventRepository
                .findByMultiStatusAndKeyword(organizerId,statusList, keyword, pageable);
        return entityPage.map(this::toDTO);
    }
    private EventCardDTO toDTO(Event event) {
        EventCardDTO dto = new EventCardDTO();
        dto.setId(event.getEventId());
        dto.setEventName(event.getTitle());
        dto.setThumnail(event.getThumbnailUrl());
        dto.setDate(event.getDate());
        dto.setStartime(event.getStartTime().toLocalTime());
        dto.setEndtime(event.getEndTime().toLocalTime());
        dto.setStatusEvent(event.getStatus().name());
        dto.setEventCatagory(event.getCategory().getCategoryName());
        dto.setVenueName(event.getVenue().getVenueName());

        List<TicketType> ticketTypes = event.getTicketTypes();
        int stock = 0;
        int numSelled = 0;
        for (TicketType tt : ticketTypes) {
            stock     += tt.getStock();
            numSelled += tickRepository.getNumTicketSelled(tt.getTicketTypeId());
        }

        dto.setStock(stock);
        dto.setTicketSelled(numSelled);
        dto.setPercent(stock == 0 ? 0 : numSelled * 100 / stock);

    public List<EventSummaryProjection> getEventStatisticsByVenue(Long id){
        return eventRepository.getEventStatisticsByVenue(id);
    }
    public VenueSummaryProjection getVenueStatisticSummary( Long id){
        return  eventRepository.getVenueStatisticSummary(id);
    }

    @Override
    public Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getKeyword().toLowerCase() + "%"));
            }

            if (criteria.getCategory() != null && !criteria.getCategory().trim().isEmpty()) {
                Join<Event, EventCategory> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("categoryName")), criteria.getCategory().toLowerCase()));
            }

            if (criteria.getCity() != null && !criteria.getCity().trim().isEmpty() && !criteria.getCity().equals("all")) {

                Join<Event, Venue> venueJoin = root.join("venue", JoinType.INNER);

                Join<Venue, Address> addressJoin = venueJoin.join("address", JoinType.INNER);

                Join<Address, Ward> wardJoin = addressJoin.join("ward", JoinType.INNER);

                Join<Ward, City> cityJoin = wardJoin.join("city", JoinType.INNER);

                predicates.add(cb.equal(cb.lower(cityJoin.get("name")), criteria.getCity().toLowerCase()));
            }

            if (criteria.getMonth() != null && !criteria.getMonth().trim().isEmpty() && !criteria.getMonth().equals("all")) {
                try {
                    String[] parts = criteria.getMonth().split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);

                    LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
                    LocalDateTime endOfMonth = startOfMonth.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())
                            .withHour(23).withMinute(59).withSecond(59);

                    predicates.add(cb.between(root.get("startTime"), startOfMonth, endOfMonth));
                } catch (Exception e) {
                    System.err.println("Lỗi parse định dạng tháng: " + e.getMessage());
                }
            }


            if (criteria.getPrice() != null && !criteria.getPrice().trim().isEmpty() && !criteria.getPrice().equals("all")) {

                Subquery<Double> subquery = query.subquery(Double.class);

                Root<TicketType> ticketRoot = subquery.from(TicketType.class);

                subquery.select(cb.min(ticketRoot.get("price")));

                subquery.where(cb.equal(ticketRoot.get("event"), root));

                Expression<Double> minPriceExpr = subquery;

                switch (criteria.getPrice()) {
                    case "free":
                        predicates.add(cb.equal(minPriceExpr, 0D));
                        break;
                    case "under200":
                        predicates.add(cb.lessThanOrEqualTo(minPriceExpr, 200000D));
                        break;
                    case "200to1000":
                        predicates.add(cb.between(minPriceExpr, 200000D, 1000000D));
                        break;
                    case "over1000":
                        predicates.add(cb.greaterThan(minPriceExpr, 1000000D));
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return eventRepository.findAll(spec, pageable);
    }

    public List<VenueSummaryProjection> getMonthlyRevenueByVenue(Long id){
        return eventRepository.getMonthlyRevenueByVenue(id);
        return dto;
    }
}



