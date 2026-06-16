package vn.edu.fpt.common.error;

public class TimeRangeException extends RuntimeException{
    private final String field;

    public TimeRangeException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
