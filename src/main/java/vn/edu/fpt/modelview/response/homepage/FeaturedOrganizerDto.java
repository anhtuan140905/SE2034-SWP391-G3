package vn.edu.fpt.modelview.response.homepage;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedOrganizerDto {
    private String fullName;
    private String companyName;
    private long eventCreated;
}