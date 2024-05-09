package jason.hermes.exception;

public class ErrorClosingFileWriterException extends RuntimeException {

    public ErrorClosingFileWriterException(String fileName, Throwable throwable) {
        super("Error closing file writing: " + fileName, throwable);
    }

}
