package vn.edu.fpt.common.error;

public class TaxCodeExists extends RuntimeException {
    public TaxCodeExists(String message) {
        super(message);
    }
}
