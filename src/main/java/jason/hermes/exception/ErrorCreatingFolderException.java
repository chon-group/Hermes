package jason.hermes.exception;

public class ErrorCreatingFolderException extends RuntimeException{

    public ErrorCreatingFolderException(String folderName) {
        super("Error creating folder: " + folderName);
    }

}
