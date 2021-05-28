package jwt;

public class OverTimeException extends RuntimeException {

    private static final String MESSAGE = "만료시간이 끝남";

    public OverTimeException() {
        super(MESSAGE);
    }

}
