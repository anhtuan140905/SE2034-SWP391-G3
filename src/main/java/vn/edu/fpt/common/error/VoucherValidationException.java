package vn.edu.fpt.common.error;

import lombok.Getter;

@Getter
public class VoucherValidationException extends RuntimeException{
    private final String errorCode;

    public VoucherValidationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
