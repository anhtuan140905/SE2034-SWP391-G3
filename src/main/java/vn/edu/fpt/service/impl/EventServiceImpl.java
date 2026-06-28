package vn.edu.fpt.service.impl;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.admin.CountEventByMonthDTO;

import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;


import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import vn.edu.fpt.model.*;
import vn.edu.fpt.modelview.request.organizer.*;
import vn.edu.fpt.modelview.response.organizer.EventCardDTO;
import vn.edu.fpt.modelview.response.organizer.EventDetailDTO;
import vn.edu.fpt.modelview.response.organizer.TicketTypeDTO;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.service.StaffService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("EventService")
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private EventCategoryRepository eventCategoryRepository;
    private CityRepository cityRepository;
    private WardRepository wardRepository;
    private final EventRepository eventRepository;
    private PermissionRepository permissionRepository;
    private UserRepository userRepository;
    private CloudinaryService cloudinaryService;
    private StaffService staffService;
    private OrganizerProfileRepository organizerProfileRepository;
    @Override
    public List<cityDto> getListcity() {
        List<City> citys = cityRepository.findAll();
        List<cityDto> cityDtos = new ArrayList<>();
        for(City city: citys){
            cityDto CityDto = new cityDto();
            CityDto.setId(city.getId());
            CityDto.setName(city.getName());
            cityDtos.add(CityDto);
        }
        return cityDtos;
    }

    @Override
    public List<EventCategory> getListEventCategory() {
        List<EventCategory> listAllEventCategory = eventCategoryRepository.findAll();
        return listAllEventCategory;
    }
    @Override
    public EventDetailDTO getEventDetailById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Event Not Found with " + id));
        EventDetailDTO eventDetailDTO = new EventDetailDTO();
        eventDetailDTO.setBanner(event.getThumbnailUrl());
        eventDetailDTO.setEventName(event.getTitle());
        eventDetailDTO.setVenueName(event.getVenueName());
        eventDetailDTO.setDate(event.getDate().toString());
        eventDetailDTO.setStatus(event.getStatus().name());
        eventDetailDTO.setDescription(event.getDescription());
        List<String> urlimage = new ArrayList<>();
        for (EventImage image: event.getImages()){
            urlimage.add(image.getImageUrl());
        }
        eventDetailDTO.setUrlImage(urlimage);
        eventDetailDTO.setCity(event.getAddress().getWard().getCity().getName());
        List<timeLineDTO> timelines = new ArrayList<>();
        for (TimeLineEvent timeline : event.getTimeLine()){
            timeLineDTO dto = new timeLineDTO(timeline.getTime(),timeline.getDescription());
            timelines.add(dto);
        }
        eventDetailDTO.setTimelines(timelines);
        List<TicketTypeDTO> ticketTypes = new ArrayList<>();
        for (TicketType ticketType : event.getTicketTypes()){
            TicketTypeDTO dto =new TicketTypeDTO(ticketType.getZoneName(),ticketType.getPrice(),
                    ticketType.getTotalQuantity(),ticketType.getSoldQuantity());
            ticketTypes.add(dto);
        }
        eventDetailDTO.setTicketType(ticketTypes);
        eventDetailDTO.setStartTime(event.getStartTime().toLocalTime().toString());
        eventDetailDTO.setEndTime(event.getEndTime().toLocalTime().toString());
        eventDetailDTO.setWard(event.getAddress().getWard().getName());
        eventDetailDTO.setSpecificAddress(event.getAddress().getSpecificAddress());
        return eventDetailDTO;
    }

    @Override
    public List<wardDTO> listWardDtos(Long cityId) {
        List<Ward> wards = wardRepository.findByCityId(cityId);
        City city = cityRepository.getCityById(cityId);
        cityDto citydto = new cityDto();
        citydto.setName(city.getName());
        citydto.setId(city.getId());
        List<wardDTO> wardDTOS = new ArrayList<>();
        for (Ward ward :wards){
            wardDTO dto = new wardDTO();
            dto.setWardId(ward.getId());
            dto.setName(ward.getName());
            dto.setCity(citydto);
            wardDTOS.add(dto);
        }
        return wardDTOS;
    }

    @Override
    @Transactional
    public void saveEvent(EventDTO eventDTO) {
        OrganizerProfileDto profileDto  = eventDTO.getOrganizerProfile();
        OrganizerProfile organizerProfile = new OrganizerProfile();
        Event event =  new Event();
        User user = userRepository.findById(eventDTO.getOrganizerId())
                .orElseThrow(()-> new RuntimeException("Not Found User With ID : "+eventDTO.getOrganizerId()));
        event.setOrganizer(user);
//        Lưu Organizer Profile
        organizerProfile.setUser(user);
        organizerProfile.setTaxCode(profileDto.getTaxCode());
        organizerProfile.setCompanyName(profileDto.getCompanyName());
        organizerProfile.setLegalAddress(profileDto.getLegalAddress());
        organizerProfile.setBankAccountName(profileDto.getBankAccountName());
        organizerProfile.setBankName(profileDto.getBankName());
        organizerProfile.setBankBranch(profileDto.getBankBranch());
        organizerProfile.setBusinessType(profileDto.getBusinessType());
        organizerProfile.setLegalName(profileDto.getLegalName());
        organizerProfileRepository.save(organizerProfile);
//        Lưu Event
        EventCategory eventCategory = eventCategoryRepository.findById(eventDTO.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Not Found Category With ID :"+eventDTO.getCategoryId()));
        event.setCategory(eventCategory);
        event.setVenueName(eventDTO.getVenueName());
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        String urlBanner =  cloudinaryService.uploadFile(eventDTO.getBanner(),"Banner");
        event.setThumbnailUrl(urlBanner);
//        Địa chỉ và thời gian và ảnh
        Address address = new Address();
        address.setSpecificAddress(eventDTO.getAddress().getSpecieladdress());
        Ward ward = wardRepository.findById(eventDTO.getAddress().getWard().getWardId())
                .orElseThrow(()->new RuntimeException("Not Found Ward "));
        City city = cityRepository.getCityById(eventDTO.getAddress().getWard().getCity().getId());
        ward.setCity(city);
        address.setWard(ward);
        event.setAddress(address);
        event.setDate(eventDTO.getEventDate());
        event.setStartTime(LocalDateTime.of(eventDTO.getEventDate(),eventDTO.getStartTime()));
        event.setEndTime(LocalDateTime.of(eventDTO.getEventDate(),eventDTO.getEndTime()));
        event.setStatus(EventStatus.ACTIVE);
        List<EventImage> urlImages = new ArrayList<>();
        if(eventDTO.getImageFiles() != null && !eventDTO.getImageFiles().isEmpty()){
            for (MultipartFile Image: eventDTO.getImageFiles()){
                if (Image == null || Image.isEmpty()) {
                    continue;
                }

                EventImage image = new EventImage();
                String urlImage =  cloudinaryService.uploadFile(Image,"Image");
                image.setImageUrl(urlImage);
                image.setEvent(event);
                urlImages.add(image);
            }
        }
        event.setImages(urlImages);
        List<TimeLineEvent> timeLines  = new ArrayList<>();
        if(eventDTO.getTimeLine()!=null){
            for (timeLineDTO dto: eventDTO.getTimeLine()){
                TimeLineEvent timeLineEvent = new TimeLineEvent();
                timeLineEvent.setTime(dto.getTime());
                timeLineEvent.setDescription(dto.getActive());
                timeLineEvent.setEvent(event);
                timeLines.add(timeLineEvent);
            }

        }event.setTimeLine(timeLines);
//        Loại vé
        List<TicketType> ticketTypes = new ArrayList<>();
        if(eventDTO.getTicketTypes()!=null){
            for(TicketTypeRequestDTO ticketTypeDto:  eventDTO.getTicketTypes()){
                TicketType ticketType = new TicketType();
                ticketType.setDescription(ticketTypeDto.getDescription());
                ticketType.setEvent(event);
                ticketType.setZoneName(ticketTypeDto.getZoneName());
                ticketType.setPrice(ticketTypeDto.getPrice());
                ticketType.setTotalQuantity(ticketTypeDto.getStock().intValue());
                ticketType.setSoldQuantity(0);
                ticketType.setDisplayOrder(ticketTypeDto.getDisplayOrder());
                List<Seat> seats = new ArrayList<>();
                Seat seat = new Seat();
                seat.setRowLabel(ticketTypeDto.getSeat().getRow());
                seat.setSeatNumber(ticketTypeDto.getSeat().getSeatNumber());
                seat.setTicketType(ticketType);
                seats.add(seat);
                ticketType.setSeats(seats);
                ticketTypes.add(ticketType);
            }
        }
        event.setTicketTypes(ticketTypes);
        eventRepository.save(event);
//        Cấp quyền cho organizer
        List<Long> permissions =  permissionRepository.getALLIdPermission();
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO(user.getEmail(),5L,permissions);
        staffService.assignMember(memberRequestDTO,event.getEventId());
    }

    @Override
    public long countHostedEvents() {
        return this.eventRepository.countHostedEvents(List.of(EventStatus.ACTIVE, EventStatus.ENDED));
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
    public Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), EventStatus.ACTIVE));
            predicates.add(cb.greaterThan(root.get("endTime"), LocalDateTime.now()));

            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getKeyword().toLowerCase() + "%"));
            }

            if (criteria.getCategory() != null && !criteria.getCategory().trim().isEmpty()) {
                Join<Event, EventCategory> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("categoryName")), criteria.getCategory().toLowerCase()));
            }

            if (criteria.getCity() != null && !criteria.getCity().trim().isEmpty() && !criteria.getCity().equals("all")) {

                Join<Event, Address> addressJoin = root.join("address", JoinType.INNER);
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

    @Override
    public Event getEventById(Long id) {
        return this.eventRepository.findById(id).orElse(null);
    }

    @Override
    public EventSummaryProjection findEventDetailById(Long id) {
        return this.eventRepository.findEventDetailById(id);
    }




    @Override
    public Page<EventCardDTO> getEventCards(Long organizerId, String[] statuses, String keyword, int page) {

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
        Page<Event> entityPage = this.eventRepository
                .findByMultiStatusAndKeyword(organizerId, statusList, keyword, pageable);
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
        dto.setVenueName(event.getVenueName());

        List<TicketType> ticketTypes = event.getTicketTypes();
        int stock = 0;
        int numSelled = 0;
        for (TicketType ticketType : ticketTypes) {
            stock =  stock + ticketType.getTotalQuantity();
            numSelled = numSelled +  ticketType.getSoldQuantity();
        }

        dto.setStock(stock);
        dto.setTicketSelled(numSelled);
        dto.setPercent(stock == 0 ? 0 : numSelled * 100 / stock);

        return dto;
    }

    public List<EventSummaryDto> findTop10Events() {
        return eventRepository.findTop10Events()
                .stream()
                .map(EventSummaryDto::new)
                .toList();
    }

    public long countAllEvent(){
        return eventRepository.countAllEvent();
    }

    public long countAllUseActive(){
        return eventRepository.countAllUseActive();
    }

    public long countAllSoldTicket(){
        return eventRepository.countAllSoldTicket();
    }

    public List<CountEventByMonthDTO> countEventByMonth(){
        return eventRepository.countEventByMonth();
    }

    public List<SumRevenueByMonthProjection> sumRevenueByMonth(){
        return eventRepository.sumRevenueByMonth();
    }

    public  List<EventSummaryDto> findTop5EventsBySoldCount(){
        return eventRepository.findTop5EventsBySoldCount()
                .stream()
                .map(EventSummaryDto::new)
                .toList();
    }

public  long countUpcomingEvent(@Param("userId") Long userId){
        return eventRepository.countUpcomingEvent(userId);
}

    public long countAttendedEvent(@Param("userId") Long userId){
        return eventRepository.countAttendedEvent(userId);
    }
}