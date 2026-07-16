package vn.edu.fpt.service.impl;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.fpt.exception.TaxCodeExists;
import vn.edu.fpt.exception.ResourceNotFoundException;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.SettlementResult;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.admin.CountEventByMonthDTO;

import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;


import vn.edu.fpt.modelview.response.homepage.EventHomeDTO;
import vn.edu.fpt.modelview.response.homepage.EventSearchResultDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import vn.edu.fpt.model.*;
import vn.edu.fpt.modelview.request.organizer.*;
import vn.edu.fpt.modelview.response.organizer.*;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.service.StaffService;
import vn.edu.fpt.service.TicketService;
import vn.edu.fpt.service.TicketTypeService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("EventService")
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventCategoryRepository eventCategoryRepository;
    private final CityRepository cityRepository;
    private final WardRepository wardRepository;
    private final EventRepository eventRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final StaffService staffService;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final TicketService ticketService;
    private final TicketTypeService ticketTypeService;
    private final  EventImageRepository eventImageRepository;
    private final SettlementServiceImpl settlementServiceImpl;


    private final OrganizerMemberRepository organizerMemberRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final BankRepository bankRepository;
    @Override
    public EventEditDTO getEventUpdateById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(()->new RuntimeException("Không tìm thấy sự kiện này"));
        EventEditDTO dto = new EventEditDTO();
//        set trường đơn
        dto.setEventId(event.getEventId());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setCategoryId(event.getCategory().getCategoryId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getDate());
        dto.setVenueName(event.getVenueName());
        dto.setExistBannerUrl(event.getThumbnailUrl());
        dto.setStartTime(event.getStartTime().toLocalTime());
        dto.setEndTime(event.getEndTime().toLocalTime());
//        set address
        addressEditDTO addressDto = new addressEditDTO();
        addressDto.setAddressId(event.getAddress().getId());
        addressDto.setSpecieladdress(event.getAddress().getSpecificAddress());

        wardEditDTO wardDto = new wardEditDTO();
        wardDto.setWardId(event.getAddress().getWard().getId());
        wardDto.setName(event.getAddress().getWard().getName());

        cityEditDto cityDto = new cityEditDto();
        cityDto.setId(event.getAddress().getWard().getCity().getId());
        cityDto.setName(event.getAddress().getWard().getCity().getName());
        wardDto.setCity(cityDto);

        addressDto.setWard(wardDto);
        dto.setAddressEdit(addressDto);
//        Set timeline
        List<timeLineEditDTO> timeLineDTOs = new ArrayList<>();
        if (event.getTimeLine() != null) {
            for (TimeLineEvent tl : event.getTimeLine()) {
                timeLineEditDTO tlDto = new timeLineEditDTO();
                tlDto.setTimeLineId(tl.getTimeId());
                tlDto.setTime(tl.getTime());
                tlDto.setActive(tl.getDescription());
                timeLineDTOs.add(tlDto);
            }
        }
        dto.setTimeLineEdit(timeLineDTOs);
        List<TicketTypeResponseDTO> ticketypeResponseDTOS = new ArrayList<>();
        if(event.getTicketTypes()!=null){
            for(TicketType tt:event.getTicketTypes()){
//               single field;
                TicketTypeResponseDTO t = new TicketTypeResponseDTO();
                t.setTicketTypeId(tt.getTicketTypeId());
                t.setDisplayOrder(tt.getDisplayOrder());
                t.setZoneName(tt.getZoneName());
                t.setPrice(tt.getPrice());
                t.setStock(tt.getTotalQuantity().longValue());
                t.setDescription(tt.getDescription());
//                seat
                Set<String> rowLabels = new HashSet<>();
                Set<Integer> seatNumbers = new HashSet<>();
                if (tt.getSeats() != null) {
                    for (Seat s : tt.getSeats()) {
                        if (s.getRowLabel() != null) {
                            rowLabels.add(s.getRowLabel());
                        }
                        if (s.getSeatNumber() != null) {
                            seatNumbers.add(s.getSeatNumber());
                        }
                    }
                }
                seatEditDTO seatDTO = new seatEditDTO();
                seatDTO.setRow(Math.max(1, rowLabels.size()));
                seatDTO.setSeatNumber(Math.max(1, seatNumbers.size()));
                t.setSeat(seatDTO);
                ticketypeResponseDTOS.add(t);
            }

        }
        dto.setTicketTypesEdit(ticketypeResponseDTOS);
        List<ExistImageDTO> existingImages = new ArrayList<>();
        if (event.getImages() != null) {
            for (EventImage img : event.getImages()) {
                existingImages.add(new ExistImageDTO(img.getImageId(), img.getImageUrl()));
            }
        }
        dto.setExistImages(existingImages);
       return dto;
    }


    @Override
    public void publishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sự kiện với id: " + eventId));

        if (event.getStatus() != EventStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể đăng sự kiện đang ở trạng thái PENDING. "
                    + "Trạng thái hiện tại: " + event.getStatus());
        }

        event.setStatus(EventStatus.ACTIVE);
         eventRepository.save(event);
    }

    @Override
    @Transactional
    public void updateEvent(EventEditDTO eventDTO) {
        Event event = eventRepository.findById(eventDTO.getEventId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện với ID: " + eventDTO.getEventId()));
        // Field đơn giản
        EventCategory eventCategory = eventCategoryRepository.findById(eventDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại sự kiện với ID: " + eventDTO.getCategoryId()));
        event.setCategory(eventCategory);
        event.setVenueName(eventDTO.getVenueName());
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setDate(eventDTO.getEventDate());
        event.setStartTime(LocalDateTime.of(eventDTO.getEventDate(), eventDTO.getStartTime()));
        event.setEndTime(LocalDateTime.of(eventDTO.getEventDate(), eventDTO.getEndTime()));

        // Banner: chỉ update nếu có file mới
        if (eventDTO.getBanner() != null && !eventDTO.getBanner().isEmpty()) {
            String urlBanner = cloudinaryService.uploadFile(eventDTO.getBanner(), "Banner");
            event.setThumbnailUrl(urlBanner);
        }

        // Địa chỉ
        Address address = event.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setSpecificAddress(eventDTO.getAddressEdit().getSpecieladdress());

        Ward ward = wardRepository.findById(eventDTO.getAddressEdit().getWard().getWardId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phường/xã với ID: " + eventDTO.getAddressEdit().getWard().getWardId()));
        City city = cityRepository.getCityById(eventDTO.getAddressEdit().getWard().getCity().getId());
        ward.setCity(city);
        address.setWard(ward);
        event.setAddress(address);

        // Ảnh phụ: xoá ảnh bị gỡ
        if (eventDTO.getRemovedImageIds() != null && !eventDTO.getRemovedImageIds().isBlank()) {
            String[] idParts = eventDTO.getRemovedImageIds().split(",");
            List<Long> removedIds = new ArrayList<>();
            for (String idStr : idParts) {
                if (idStr == null || idStr.trim().isEmpty()) {
                    continue;
                }
                removedIds.add(Long.parseLong(idStr.trim()));
            }

            List<EventImage> remainImages = new ArrayList<>();
            for (EventImage img : event.getImages()) {
                boolean isRemoved = false;
                for (Long removedId : removedIds) {
                    if (img.getImageId().equals(removedId)) {
                        isRemoved = true;
                        break;
                    }
                }
                if (!isRemoved) {
                    remainImages.add(img);
                }
            }
            event.getImages().clear();
            event.getImages().addAll(remainImages);

            if (!removedIds.isEmpty()) {
                eventImageRepository.deleteAllByIdInBatch(removedIds);
            }
        }

        // Ảnh phụ: thêm ảnh mới
        if (eventDTO.getImageFiles() != null && !eventDTO.getImageFiles().isEmpty()) {
            for (MultipartFile img : eventDTO.getImageFiles()) {
                if (img == null || img.isEmpty()) {
                    continue;
                }
                EventImage image = new EventImage();
                String urlImage = cloudinaryService.uploadFile(img, "Image");
                image.setImageUrl(urlImage);
                image.setEvent(event);
                event.getImages().add(image);
            }
        }

        // Lịch trình: xoá hết & tạo lại
        event.getTimeLine().clear();
        if (eventDTO.getTimeLineEdit() != null) {
            for (timeLineEditDTO dtoTl : eventDTO.getTimeLineEdit()) {
                TimeLineEvent timeLineEvent = new TimeLineEvent();
                timeLineEvent.setTime(dtoTl.getTime());
                timeLineEvent.setDescription(dtoTl.getActive());
                timeLineEvent.setEvent(event);
                event.getTimeLine().add(timeLineEvent);
            }
        }

        // Hạng vé + ghế: xoá hết & tạo lại
        event.getTicketTypes().clear();

        if (eventDTO.getTicketTypesEdit() != null) {
            Map<Integer, String> rowLabelMap = new HashMap<>();
            for (int i = 1; i <= 26; i++) {
                rowLabelMap.put(i, String.valueOf((char) ('A' + i - 1)));
            }

            for (TicketTypeResponseDTO ticketTypeDto : eventDTO.getTicketTypesEdit()) {
                TicketType ticketType = new TicketType();
                ticketType.setEvent(event);
                ticketType.setDescription(ticketTypeDto.getDescription());
                ticketType.setZoneName(ticketTypeDto.getZoneName());
                ticketType.setPrice(ticketTypeDto.getPrice());
                ticketType.setTotalQuantity(ticketTypeDto.getStock().intValue());
                ticketType.setSoldQuantity(0);
                ticketType.setDisplayOrder(ticketTypeDto.getDisplayOrder());

                List<Seat> seats = new ArrayList<>();
                for (Integer i = 1; i <= ticketTypeDto.getSeat().getSeatNumber(); i++) {
                    for (Integer j = 1; j <= ticketTypeDto.getSeat().getRow(); j++) {
                        Seat seat = new Seat();
                        seat.setSeatNumber(i);
                        seat.setRowLabel(rowLabelMap.get(j));
                        seat.setTicketType(ticketType);
                        seats.add(seat);
                    }
                }
                ticketType.setSeats(seats);
                event.getTicketTypes().add(ticketType);
            }
        }

        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void SetStatusEvent() {
        List<Event> eventSetStatus = eventRepository.findEndedEvents(EventStatus.INACTIVE,LocalDateTime.now());
        if (eventSetStatus.isEmpty()) {
            return;
        }
        for (Event event : eventSetStatus) {
            // Đổi trạng thái sự kiện Là Kết thúc
            if (event.getStartTime().isBefore(LocalDateTime.now()) && event.getEndTime().isAfter(LocalDateTime.now()) ){
                event.setStatus(EventStatus.INACTIVE);

            }else {
                event.setStatus(EventStatus.ENDED);
                // Thêm các logic khác (nếu có) vào đây...
                processSettlement(event.getEventId());
                List<OrganizerMember> organizerMemberList = organizerMemberRepository.findByEventId(event.getEventId());
                for (OrganizerMember organizerMember:organizerMemberList){
                    if(organizerMember.getUserRole().getRole().getRoleName().toString().equals(RoleName.ROLE_ORGANIZER.toString())){
                        OrganizerProfile organizerProfile = organizerMember.getUserRole().getUser().getOrganizerProfile();
                        organizerProfile.setIsActive(false);
                        organizerProfileRepository.save(organizerProfile);
                        continue;
                    }
                    staffService.deleteStaffByStaffId(organizerMember.getId(),event.getEventId(),organizerMember.getUserRole().getUser().getId());
                }
           
            }
          eventRepository.saveAll(eventSetStatus);
        }

    }

    private void processSettlement(Long eventId) {
        try {
            SettlementResult result = settlementServiceImpl.autoCreateSettlement(eventId);
            switch (result) {
                case CREATED -> log.info("Đã tự động tạo settlement cho event {}", eventId);
                case ALREADY_EXISTS -> log.debug("Event {} đã có settlement, bỏ qua", eventId);
                case NO_REVENUE -> log.debug("Event {} không có doanh thu, bỏ qua settlement", eventId);
            }
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tự động tạo settlement cho event {}", eventId, e);
        }
        
    }

    @Override
        public Boolean GetOrganizerProfileByUserId(Long userId) {
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserId(userId).orElse(null);
        if(organizerProfile!=null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<BankDto> getListBank() {
        List<Bank> banks = bankRepository.findAll();
        List<BankDto> bankDtos = new ArrayList<>();
        for (Bank bank:banks){
            BankDto dto = new BankDto(bank.getId(),bank.getShortName());
            bankDtos.add(dto);
        }
        return bankDtos;
    }

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
    public void saveEvent(EventDTO eventDTO,OrganizerProfileDto organizerProfileDto) {
        if (eventDTO.getBanner() == null || eventDTO.getBanner().isEmpty()) {
            throw new RuntimeException("Banner không được để trống");
        }
        Event event =  new Event();
        User user = userRepository.getReferenceById(eventDTO.getOrganizerId());
        event.setOrganizer(user);
//        Lưu Organizer Profile
        if (organizerProfileDto!=null){
        OrganizerProfileDto profileDto  = organizerProfileDto ;
        if(organizerProfileRepository.existsByTaxCode(profileDto.getTaxCode())){
            throw new TaxCodeExists("Mã số thuế đã tồn tại");
        }
        OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setUser(user);
        organizerProfile.setTaxCode(profileDto.getTaxCode());
        organizerProfile.setCompanyName(profileDto.getCompanyName());
        organizerProfile.setLegalAddress(profileDto.getLegalAddress());
        organizerProfile.setBankAccountName(profileDto.getBankAccountName());
        organizerProfile.setBankAccountNumber(profileDto.getBankAccountNumber());
        Bank bank =  bankRepository.getReferenceById(organizerProfileDto.getBankId());
        organizerProfile.setBank(bank);
        organizerProfile.setBankBranch(profileDto.getBankBranch());
        organizerProfile.setBusinessType(profileDto.getBusinessType());
        organizerProfile.setLegalName(profileDto.getLegalName());
        organizerProfile.setIsActive(true);
        organizerProfileRepository.save(organizerProfile);
            }
//        Lưu Event
        EventCategory eventCategory = eventCategoryRepository.findById(eventDTO.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Không tìm thấy loại sự kiện với ID: " + eventDTO.getCategoryId()));
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
                .orElseThrow(()->new RuntimeException("Không tìm thấy phường/xã với ID: " + eventDTO.getAddress().getWard().getWardId()));
        City city = cityRepository.getCityById(eventDTO.getAddress().getWard().getCity().getId());
        ward.setCity(city);
        address.setWard(ward);
        event.setAddress(address);
        event.setDate(eventDTO.getEventDate());
        event.setStartTime(LocalDateTime.of(eventDTO.getEventDate(),eventDTO.getStartTime()));
        event.setEndTime(LocalDateTime.of(eventDTO.getEventDate(),eventDTO.getEndTime()));
        event.setStatus(EventStatus.PENDING);
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
                Map<Integer, String> map = new HashMap<>();
                for (int i = 1; i <= 26; i++) {
                    map.put(i, String.valueOf((char)('A' + i - 1)));
                }
                List<Seat> seats = new ArrayList<>();
                for(Integer i = 1;i<=ticketTypeDto.getSeat().getSeatNumber();i++){
                    for(Integer j =1;j <= ticketTypeDto.getSeat().getRow(); j++){
                        Seat seat = new Seat();
                        seat.setSeatNumber(i);
                        seat.setRowLabel(map.get(j));
                        seat.setTicketType(ticketType);
                        seats.add(seat);
                    }
                }


                ticketType.setSeats(seats);
                ticketTypes.add(ticketType);
            }
        }
        event.setTicketTypes(ticketTypes);
        eventRepository.save(event);
//        Cấp quyền cho organizer
        List<Long> permissions =  permissionRepository.getPermissionsOfOrganizer();
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
    public Page<EventSearchResultDTO> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), EventStatus.ACTIVE));
            predicates.add(cb.greaterThan(root.get("startTime"), LocalDateTime.now()));

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
        Page<Event> eventPage = this.eventRepository.findAll(spec, pageable);
        List<Long> eventIds = eventPage.getContent().stream().map(Event::getEventId).toList();
        Map<Long, Long> soldCountMap = eventIds.isEmpty()
                ? Collections.emptyMap()
                : this.ticketService.countSoldTicketsByEventIds(eventIds).stream()
                .collect(Collectors.toMap(
                           row -> ((Number) row[0]).longValue(),
                           row -> ((Number) row[1]).longValue()
                ));
       Map<Long, Double> minPriceMap = this.ticketTypeService.getMinPriceByEventIds(eventIds);

       List<EventSearchResultDTO> content = eventPage.getContent().stream()
               .map(event -> EventSearchResultDTO.builder()
                       .id(event.getEventId())
                       .title(event.getTitle())
                       .description(event.getDescription())
                       .thumbnailUrl(event.getThumbnailUrl())
                       .categoryName(event.getCategory() != null ? event.getCategory().getCategoryName() : null)
                       .venueName(event.getVenueName())
                       .city(event.getAddress() != null
                               ? event.getAddress().getWard().getCity().getName()
                               : null)
                       .date(event.getDate())
                       .startTime(event.getStartTime())
                       .endTime(event.getEndTime())
                       .organizerName(event.getOrganizer() != null && event.getOrganizer().getOrganizerProfile() != null
                               ? event.getOrganizer().getOrganizerProfile().getCompanyName()
                               : null)
                       .minPrice(minPriceMap.getOrDefault(event.getEventId(), null))
                       .soldTickets(soldCountMap.getOrDefault(event.getEventId(), 0L))
                       .build())
               .toList();
            Page<EventSearchResultDTO> resultPage = new PageImpl<>(content, pageable, eventPage.getTotalElements());
        return resultPage;
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
    public Page<EventCardDTO> getEventCards(Long organizerId, String status, String keyword, int page) {
        EventStatus normalizedStatus = null;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            try {
                normalizedStatus = EventStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                // status không hợp lệ -> coi như "All", không lọc
                normalizedStatus = null;
            }
        }
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), 9);
        Page<Event> entityPage = this.eventRepository.findByStatusAndKeyword(organizerId, normalizedStatus, keyword, pageable);
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

    @Override
    public EventHomeDTO getFavouriteEvent(Long eventId) {
        return this.eventRepository.findEventsWithMinPrice(eventId);
    }

public List<SettlementSummaryProjection> findEventsWithSettlementStatus(String tab){
    if (!List.of("all", "pending", "completed").contains(tab)) {
        throw new IllegalArgumentException("Trạng thái lọc không hợp lệ.");
    }
        List<SettlementSummaryProjection> list = eventRepository.findEventsWithSettlementStatus();
        return switch (tab == null ? "all" : tab) {
            case "pending" -> list.stream()
                    .filter(e -> e.getSettlementId() == null)
                    .toList();

            case "completed" -> list.stream()
                    .filter(e -> e.getSettlementId() != null)
                    .toList();

            default -> list;
        };
}

public long countEndedEvent(){
        return eventRepository.countEndedEvent();
}

public long countUnsettledEvents(){
        return eventRepository.countUnsettledEvents();
}

public Long sumTotalRevenue(){
        return eventRepository.sumTotalRevenue();
}

public  List<SettlementSummaryProjection> searchEndedEvents(@Param("keyword") String keyword){
        return eventRepository.searchEndedEvents(keyword);
}
    @Override
    public List<Event> findCandidateEventsByCategories(List<Long> targetCatIds, EventStatus eventStatus, OrderStatus orderStatus, LocalDate today, Long userId, PageRequest page) {
        return this.eventRepository.findCandidatesEventByCategories(targetCatIds, eventStatus, orderStatus, today, userId, page);
    }

    @Override
    public List<Event> findUpcomingEvent(EventStatus status, LocalDate today, PageRequest page) {
        return this.eventRepository.findUpcomingEvents(EventStatus.ACTIVE, today, page);
    }

public EventSummaryProjection getEventDetail(@Param("settlementId") Long settlementId){
        return eventRepository.getEventDetail(settlementId);
}
}