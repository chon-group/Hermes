package group.chon.agent.hermes.core.exception;

public class ErrorZippingFileException extends RuntimeException{

    public ErrorZippingFileException(String fileName, Throwable throwable) {
        super("Error when zipping the file: " + fileName, throwable);
    }
}
