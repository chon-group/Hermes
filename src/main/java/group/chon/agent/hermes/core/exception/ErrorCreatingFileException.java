package group.chon.agent.hermes.core.exception;

public class ErrorCreatingFileException extends RuntimeException{

    public ErrorCreatingFileException(String fileName) {
        super("Erro na criação do arquivo: " + fileName);
    }
    public ErrorCreatingFileException(String fileName, Throwable throwable) {
        super("Error creating file: " + fileName, throwable);
    }
}
