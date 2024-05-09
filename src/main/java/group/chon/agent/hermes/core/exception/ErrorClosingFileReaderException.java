package group.chon.agent.hermes.core.exception;

public class ErrorClosingFileReaderException extends RuntimeException{

    public ErrorClosingFileReaderException(String fileName, Throwable throwable) {
        super("Error closing file reading: " + fileName, throwable);
    }

}
