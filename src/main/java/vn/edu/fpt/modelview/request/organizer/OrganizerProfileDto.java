package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizerProfileDto {

    @NotBlank(message = "Mã số thuế không được để trống")
    @Size(min = 10 ,max = 20, message = "Mã số thuế tối đa 20 ký tự")
    @Pattern(
            regexp = "^[0-9]{10,13}$",
            message = "Mã số thuế phải gồm 10–13 chữ số"
    )
    private String taxCode;

    @Size(max = 500, message = "Tên công ty tối đa 500 ký tự")
    private String companyName;

    @Size(max = 200, message = "Tên pháp lý tối đa 200 ký tự")
    private String legalName;

    @Size(max = 300, message = "Địa chỉ pháp lý tối đa 300 ký tự")
    private String legalAddress;

    @Size(max = 100, message = "Tên chủ tài khoản tối đa 100 ký tự")
    private String bankAccountName;

    @Size(max = 30, message = "Số tài khoản tối đa 30 ký tự")
    @Pattern(
            regexp = "^[0-9]*$",
            message = "Số tài khoản chỉ được chứa chữ số"
    )
    private String bankAccountNumber;

    @Size(max = 100, message = "Tên ngân hàng tối đa 100 ký tự")
    private String bankName;

    @Size(max = 100, message = "Tên chi nhánh tối đa 100 ký tự")
    private String bankBranch;

    @Size(max = 50, message = "Loại hình kinh doanh tối đa 50 ký tự")
    private String businessType;
}