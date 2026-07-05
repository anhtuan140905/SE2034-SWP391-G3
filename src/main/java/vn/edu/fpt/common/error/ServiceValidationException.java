package vn.edu.fpt.common.error;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ServiceValidationException extends RuntimeException{
    public ServiceValidationException() {
        super("Service validate");
    }

    private final List<FieldError> errors = new ArrayList<>();

    public ServiceValidationException add(String field, String message) {
        errors.add(new FieldError(field, message));
        return this;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Getter
    @Setter
    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }


}
