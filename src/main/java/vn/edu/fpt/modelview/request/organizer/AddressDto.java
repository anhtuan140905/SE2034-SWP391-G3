package vn.edu.fpt.modelview.request.organizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.Ward;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private wardDTO ward;
    private String specificAddress;
}

