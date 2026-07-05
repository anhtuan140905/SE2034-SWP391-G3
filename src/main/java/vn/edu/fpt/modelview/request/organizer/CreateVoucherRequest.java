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
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Mã chỉ gồm ký tự chữ cái và số")
    private String code;

    @NotBlank(message = "Tiêu đề voucher không được để trống")
    @Size(min = 5, max = 255, message = "Tiêu đề vouche tối đa 5-255 kỹ tự")
    private String title;

    private String description;

    @NotNull(message = "Vui lòng chọn loại giảm giá")
    private DiscountType  discountType;

    @NotNull(message = "Vui lòng nhập mức giảm giá")
    @DecimalMin(value = "0.01", message = "Mức giảm phải lớn hơn 0")
    @Digits(integer = 13, fraction = 2, message = "Giá trị không hợp lệ")
    private BigDecimal discountValue;

    @NotNull(message = "Số lượt dùng không đươc để trống")
    @Min(value = 1, message = "Số lượng voucher ít nhất là 1")
    private Integer maxUsage;

    @NotNull(message = "Ngày bắt đầu voucher không được để trống")
    @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime validFrom;

    @NotNull(message = "Ngày kết thúc voucher không được để trống")
    @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime validTo;

}
