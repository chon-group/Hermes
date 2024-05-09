package group.chon.agent.hermes.core.exception;

public class ErrorCryogeningMASException extends Exception {

    public ErrorCryogeningMASException(String fileName, Throwable throwable) {
        super("Error cryogening MAS: " + fileName, throwable);
    }

}
