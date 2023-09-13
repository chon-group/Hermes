package jason.hermes.exception;

public class ErrorCryogeningMASException extends Exception {

    public ErrorCryogeningMASException(String fileName, Throwable throwable) {
        super("Error cryogening MAS: " + fileName, throwable);
    }

}
