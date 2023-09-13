package jason.hermes.exception;

public class ErrorDeletingFileException extends RuntimeException{

    public ErrorDeletingFileException(String fileName) {
        super("Error when deleting file: " + fileName);
    }

}
