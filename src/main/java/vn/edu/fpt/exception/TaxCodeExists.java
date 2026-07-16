package vn.edu.fpt.exception;

public class TaxCodeExists extends RuntimeException {
    public TaxCodeExists(String message) {
        super(message);
    }
}
