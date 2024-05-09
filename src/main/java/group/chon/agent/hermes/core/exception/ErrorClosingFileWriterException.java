package group.chon.agent.hermes.core.exception;

public class ErrorClosingFileWriterException extends RuntimeException {

    public ErrorClosingFileWriterException(String fileName, Throwable throwable) {
        super("Error closing file writing: " + fileName, throwable);
    }

}
