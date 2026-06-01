package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    @NotBlank(message = "Tên sự kiện không được để trống")
    private  String eventName;
    @NotEmpty(message = "Sự kiên phải có Ảnh Banner")
    private String bannerImg;
    private List<MultipartFile> imgs;
    @NotNull(message = "Loại sự kiện không được để trống")
    private String eventCatagory;
    private String eventDescription;
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Future(message = "Thời gian bắt đầu phải là một ngày trong tương lai")
    private LocalDateTime date;
    @NotBlank(message = "Địa điểm diễn ra không được để trống")
    private String venue;
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Future(message = "Thời gian bắt đầu phải là một ngày trong tương lai")
    private LocalDateTime startTime;
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endTime;
    @NotEmpty(message = "Sự kiện phải có ít nhất một loại vé")
    @NotNull(message = "Danh sách loại vé không được để trống")
    private List<TicketTypeRequestDTO> ticketTypes;
}
