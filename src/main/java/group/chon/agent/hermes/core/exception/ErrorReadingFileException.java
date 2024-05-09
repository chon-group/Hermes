package group.chon.agent.hermes.core.exception;

public class ErrorReadingFileException extends RuntimeException{

    public ErrorReadingFileException(String fileName) {
        super("Error reading file content: " + fileName);
    }

    public ErrorReadingFileException(String fileName, Throwable throwable) {
        super("Error reading file content: " + fileName, throwable);
    }

}
