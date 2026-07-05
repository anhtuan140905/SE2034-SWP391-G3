package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.edu.fpt.model.constant.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateVoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 5, max = 20, message = "Đọo dài mã từ 5-20 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mã chỉ gồm chữ cái và số")
    private String code;

    @NotBlank(message = "Tiêu đề voucher không được để trống")
    @Size(min = 5, max = 255, message = "Tiêu đề voucher tối đa 5-255 kỹ tự")
    private String title;

    @Size(max = 3000, message = "Mô tả quá dài")
    private String description;

    @NotNull(message = "Vui lòng chọn loại giảm giá")
    private DiscountType  discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm phải lớn hơn 0")
    @Digits(integer = 13, fraction = 2, message = "Giá trị không hợp lệ")
    private BigDecimal discountValue;

    @NotNull(message = "Số lượt sử dụng tối đa không được để trống")
    @Min(value = 1, message = "Số lượng voucher ít nhất là 1")
    private Integer maxUsage;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime validFrom;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime validTo;

}
