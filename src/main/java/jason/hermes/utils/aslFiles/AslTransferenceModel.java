package jason.hermes.utils.aslFiles;

import java.io.Serializable;

/**
 * Modelo de transferência de agente.
 */
public class AslTransferenceModel implements Serializable {

    /** Nome do agente. */
    private String name;

    /** Conteúdo do arquivo. */
    private byte[] fileContent;

    /** Arquitetura do agente. */
    private String agentArchClass;

    /**
     * Construtor.
     *
     * @param name {@link #name}
     * @param fileContent {@link #fileContent}
     */
    public AslTransferenceModel(String name, byte[] fileContent, String agentArchClass) {
        this.name = name;
        this.fileContent = fileContent;
        this.agentArchClass = agentArchClass;
    }

    public String getName() {
        return name;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public String getAgentArchClass() {
        return agentArchClass;
    }
}
