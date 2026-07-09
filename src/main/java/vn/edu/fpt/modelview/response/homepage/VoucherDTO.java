package vn.edu.fpt.modelview.response.homepage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.edu.fpt.model.Voucher;
import vn.edu.fpt.model.constant.DiscountType;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class VoucherDTO {

    private Long voucherId;
    private String title;
    private DiscountType discountType;
    private BigDecimal discountValue;

    public static VoucherDTO from(Voucher v) {
        return new VoucherDTO(
                v.getVoucherId(),
                v.getTitle(),
                v.getDiscountType(),
                v.getDiscountValue()
        );
    }
}