package group.chon.agent.hermes.core.exception;

public class ErrorWritingFileContentException extends RuntimeException {

    public ErrorWritingFileContentException(String fileName) {
        super("Error writing file content: " + fileName);
    }

    public ErrorWritingFileContentException(String fileName, Throwable throwable) {
        super("Error writing file content: " + fileName, throwable);
    }
}
