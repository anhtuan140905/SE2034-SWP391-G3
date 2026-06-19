package vn.edu.fpt.modelview.request.homepage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchCriteria {
    private String keyword;
    private String category;
    private String city;
    private String month;
    private String price;
}