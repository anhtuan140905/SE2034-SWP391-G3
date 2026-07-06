package vn.edu.fpt.modelview.response.organizer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class EventEditDTO {
    private Long eventId;
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
    private Long organizerId;
    private MultipartFile banner;
    @NotBlank(message = "Tên sự kiện không được để trống")
    @Size(min = 5, max = 120, message = "Tên sự kiện phải từ 5 đến 120 ký tự")
    private String title;
    @Size(max = 5000, message = "Mô tả không quá đến 5000 ký tự")
    private String description;
    @NotNull(message = "Ngày diễn ra không được để trống")
    @Future(message = "Ngày diễn ra phải ở tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    @NotBlank(message = "Tên địa điểm không được để trống")
    @Size(max = 255, message = "Tên địa điểm không được vượt quá 255 ký tự")
    private String venueName;
    @NotNull(message = "Địa chỉ không được để trống")
    @Valid
    private addressEditDTO addressEdit;
    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startTime;
    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime endTime;
    private List<timeLineEditDTO> timeLineEdit;
    @Valid
    @NotEmpty(message = "Phải có ít nhất một loại vé")
    private List<TicketTypeResponseDTO> ticketTypesEdit ;
    @Size(max = 10, message = "Chỉ được tải lên tối đa 10 ảnh")
    private List<MultipartFile> imageFiles;

    private String existBannerUrl;
    private String removedImageIds;
    private List<ExistImageDTO> existImages;

}