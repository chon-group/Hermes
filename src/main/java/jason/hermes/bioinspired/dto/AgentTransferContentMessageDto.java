package jason.hermes.bioinspired.dto;

import jason.hermes.bioinspired.AslTransferenceModel;

import java.io.Serializable;
import java.util.HashMap;

public class AgentTransferContentMessageDto extends AgentTransferMessageDto implements Serializable {

    private HashMap<String, AslTransferenceModel> agentsSourceCode;

    public AgentTransferContentMessageDto(HashMap<String, AslTransferenceModel> agentsSourceCode) {
        this.agentsSourceCode = agentsSourceCode;
    }

    public HashMap<String, AslTransferenceModel> getAgentsSourceCode() {
        return agentsSourceCode;
    }

}
