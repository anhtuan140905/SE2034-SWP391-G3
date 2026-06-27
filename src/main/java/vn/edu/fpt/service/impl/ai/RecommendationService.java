package vn.edu.fpt.service.impl.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.TicketType;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.UserLevel;
import vn.edu.fpt.modelview.response.homepage.RecommendationDTO;
import vn.edu.fpt.modelview.response.homepage.UserRecommendationProfile;
import vn.edu.fpt.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private static final int CANDIDATE_POOL = 10;
    private static final int MAX_RESULTS = 5;

    private final EventService eventService;
    private final OrderService orderService;
    private final FavouriteEventService favouriteEventService;
    private final EventCategoryService eventCategoryService;
    private final GeminiService geminiService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<RecommendationDTO> getRecommendations(long userId) {
        LocalDate today = LocalDate.now();

        List<Event> purchasedEvents = this.orderService.findPurchasedEventsByUserId(userId);

        List<Long> purchasedIds = purchasedEvents.stream()
                .map(e -> e.getCategory().getCategoryId())
                .distinct()
                .collect(Collectors.toList());

        List<Long> favouriteCatIds = this.favouriteEventService.findFavouriteCategoryByUserId(userId);

        UserLevel level;
        List<Long> targetCatIds;

        if (!purchasedIds.isEmpty()) {
            level = UserLevel.WARM_PURCHASED;
            targetCatIds = purchasedIds;
        } else if (!favouriteCatIds.isEmpty()) {
            level = UserLevel.WARM_FAVOURITE;
            targetCatIds = favouriteCatIds;
        } else {
            level = UserLevel.COLD;
            targetCatIds = List.of();
        }
        log.debug("User {} -> level {}", userId, level);

        UserRecommendationProfile profile = null;

        if (level == UserLevel.WARM_PURCHASED) {
            String userCity = this.userService.findCityNameByUserId(userId);
            List<String> preferredCategories = purchasedEvents.stream()
                    .map(e -> e.getCategory().getCategoryName())
                    .distinct()
                    .collect(Collectors.toList());

            List<UserRecommendationProfile.AttendedEventSummary> attendedEvents = purchasedEvents.stream()
                    .map(e -> UserRecommendationProfile.AttendedEventSummary.builder()
                            .title(e.getTitle())
                            .category(e.getCategory().getCategoryName())
                            .city(e.getAddress().getWard().getCity().getName())
                            .build()
                    ).collect(Collectors.toList());

            profile = UserRecommendationProfile.builder()
                    .userCity(userCity)
                    .preferredCategories(preferredCategories)
                    .attendedEvents(attendedEvents).build();
        }

        List<Event> candidateEvents;
        PageRequest page = PageRequest.of(0, CANDIDATE_POOL);

        if (level == UserLevel.COLD) {
            candidateEvents = this.eventService.findUpcomingEvent(EventStatus.ACTIVE, today, page);
        } else {
            candidateEvents = this.eventService.findCandidateEventsByCategories(
                    targetCatIds,
                    EventStatus.ACTIVE,
                    OrderStatus.PAID,
                    today,
                    userId,
                    page);
        }
        if (candidateEvents.isEmpty()) {
            return List.of();
        }

        List<RecommendationDTO> dtos = candidateEvents.stream()
                .map(event -> RecommendationDTO.builder()
                        .eventId(event.getEventId())
                        .title(event.getTitle())
                        .thumbnailUrl(event.getThumbnailUrl())
                        .date(event.getDate())
                        .startTime(event.getStartTime())
                        .categoryName(event.getCategory().getCategoryName())
                        .cityName(event.getAddress().getWard().getCity().getName())
                        .minPrice(event.getTicketTypes().stream()
                                .map(TicketType::getPrice)
                                .min(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO))
                        .reason(null)
                        .build())
                .collect(Collectors.toList());

        if(level == UserLevel.WARM_PURCHASED && profile != null) {
            Map<Long, String> reasons = this.geminiService.generateReasons(dtos, profile);
            if(!reasons.isEmpty()) {
                List<RecommendationDTO> finalDtos = dtos;
                dtos = reasons.keySet().stream().map(eventId -> finalDtos.stream()
                        .filter(d -> d.getEventId().equals(eventId))
                        .findFirst().orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                dtos.forEach(dto -> dto.setReason(reasons.get(dto.getEventId())));
            }
        }
        return dtos;
    }
}
