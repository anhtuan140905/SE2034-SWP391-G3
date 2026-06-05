package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;
import vn.edu.fpt.modelview.request.organizer.*;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.EventService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("EventService")
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private EventCategoryRepository eventCategoryRepository;
    private VenueRepository venueRepository;
    private VenueZoneRepository venueZoneRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CloudinaryService cloudinaryService;

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
                                    .orElseThrow(()->new RuntimeException("Venue Not Found with: "+venueID));
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
        event.setStartTime(LocalDateTime.of(eventDTO.getEventDate(),eventDTO.getStartTime()));
        event.setEndTime(LocalDateTime.of(eventDTO.getEventDate(),eventDTO.getEndTime()));
        event.setCreatedBy(organizer.getLastName()+organizer.getMiddleName()+organizer.getFirstName());
        event.setStatus(EventStatus.PENDING);

        if (eventDTO.getThumbnailFile() != null
                && !eventDTO.getThumbnailFile().isEmpty()) {
            String thumbnailUrl = cloudinaryService.uploadFile(eventDTO.getThumbnailFile(),"EventBanner");
            event.setThumbnailUrl(thumbnailUrl);
        }
        List<EventImage> eventImages = new ArrayList<>();
        if(eventDTO.getImageFiles()!=null){
            for (MultipartFile file : eventDTO.getImageFiles()){
                if (file.isEmpty()) {
                    continue;
                }
                String imageurl = cloudinaryService.uploadFile(file,"EventImg");
                EventImage image = new EventImage();
                image.setEvent(event);
                image.setCreatedBy(organizer.getLastName()+organizer.getMiddleName()+organizer.getFirstName());
                image.setImageUrl(imageurl);
                eventImages.add(image);
            }
            event.setImages(eventImages);

        }
//        List<TicketType> ticketTypes = new ArrayList<>();
//        if(eventDTO.getTicketTypes()!=null){
//            for(TicketTypeRequestDTO ticketTypeDto:  eventDTO.getTicketTypes()){
//                TicketType type = new TicketType();
//                type.setEvent(event);
//                type.setTypeName(ticketTypeDto.getTypeName());
//                type.setPrice(ticketTypeDto.getPrice());
//                type.setDescription(ticketTypeDto.getDescription());
//                ticketTypes.add(type);
//            }
//        }
//        event.setTicketTypes(ticketTypes);
        eventRepository.save(event);
    }

    @Override
    public EventDetailModeratorDTO getEventDetailById(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->new RuntimeException("Không tìm thấy sự kiện"));

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
        if(event.getStartTime() != null && event.getEndTime() != null) {
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
}



