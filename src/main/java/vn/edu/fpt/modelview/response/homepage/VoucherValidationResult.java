package vn.edu.fpt.modelview.response.homepage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.edu.fpt.model.Voucher;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class VoucherValidationResult {

    private boolean valid;
    private String errorCode;
    private Voucher voucher;
    private BigDecimal discount;

    public static VoucherValidationResult valid(Voucher voucher, BigDecimal discount) {
        return new VoucherValidationResult(true, null, voucher, discount);
    }

    public static VoucherValidationResult invalid(String errorCode) {
        return new VoucherValidationResult(false, errorCode, null, BigDecimal.ZERO);
    }

    public boolean isValid() {
        return valid;
    }
}