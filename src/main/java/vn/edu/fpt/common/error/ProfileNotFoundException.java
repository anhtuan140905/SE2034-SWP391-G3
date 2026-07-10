package vn.edu.fpt.common.error;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
