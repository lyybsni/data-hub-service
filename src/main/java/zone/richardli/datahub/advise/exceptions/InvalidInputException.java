package zone.richardli.datahub.advise.exceptions;

public class InvalidInputException extends RuntimeException {

    private final String message;

    public InvalidInputException(String message) {
        this.message = message;
    }

    public InvalidInputException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String toString() {
        return "InvalidInputException(message=" + this.message + ")";
    }

}
