package jason.hermes.capabilities.bioinspiredProtocols.dto;

import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import jason.hermes.capabilities.manageTrophicLevel.TrophicLevelEnum;

import java.io.Serializable;
import java.util.List;

public class AgentTransferRequestMessageDto extends AgentTransferMessageDto implements Serializable {

    private String senderIdentification;

    private List<String> nameOfAgentsToBeTransferred;

    private boolean hasHermesAgentTransferred;

    private BioinspiredProtocolsEnum bioinspiredProtocol;

    private TrophicLevelEnum trophicLevelEnum;

    private boolean entireMAS;

    public AgentTransferRequestMessageDto(String senderIdentification,
                                          List<String> nameOfAgentsToBeTransferred,
                                          boolean hasHermesAgentTransferred,
                                          BioinspiredProtocolsEnum bioinspiredProtocol,
                                          TrophicLevelEnum trophicLevelEnum, boolean entireMAS) {
        this.senderIdentification = senderIdentification;
        this.nameOfAgentsToBeTransferred = nameOfAgentsToBeTransferred;
        this.hasHermesAgentTransferred = hasHermesAgentTransferred;
        this.bioinspiredProtocol = bioinspiredProtocol;
        this.trophicLevelEnum = trophicLevelEnum;
        this.entireMAS = entireMAS;
    }

    public String getSenderIdentification() {
        return senderIdentification;
    }

    public List<String> getNameOfAgentsToBeTransferred() {
        return nameOfAgentsToBeTransferred;
    }

    public boolean isHasHermesAgentTransferred() {
        return hasHermesAgentTransferred;
    }

    public BioinspiredProtocolsEnum getBioinspiredProtocol() {
        return bioinspiredProtocol;
    }

    public TrophicLevelEnum getTrophicLevelEnum() {
        return trophicLevelEnum;
    }

    public boolean isEntireMAS() {
        return entireMAS;
    }

}
