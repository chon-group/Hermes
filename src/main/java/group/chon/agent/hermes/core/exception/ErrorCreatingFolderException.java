package group.chon.agent.hermes.core.exception;

public class ErrorCreatingFolderException extends RuntimeException{

    public ErrorCreatingFolderException(String folderName) {
        super("Error creating folder: " + folderName);
    }

}
