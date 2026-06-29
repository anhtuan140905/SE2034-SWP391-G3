package vn.edu.fpt.modelview.response.homepage;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserRecommendationProfile {
    private List<String> preferredCategories;
    private List<AttendedEventSummary> attendedEvents;
    private String userCity;

    @Builder @Getter
    public static class AttendedEventSummary {
        private String title;
        private String category;
        private String city;
    }
}
