package group.chon.agent.hermes.core.exception;

public class ErrorDeletingFileException extends RuntimeException{

    public ErrorDeletingFileException(String fileName) {
        super("Error when deleting file: " + fileName);
    }

}
