package jason.hermes.bioinspired.dto;

import jason.hermes.bioinspired.BioinspiredProtocolsEnum;
import jason.hermes.bioinspired.TrophicLevelEnum;

import java.io.Serializable;
import java.util.List;

public class AgentTransferRequestMessageDto extends AgentTransferMessageDto implements Serializable {

    private String senderIdentification;

    private List<String> nameOfAgentsToBeTransferred;

    private boolean hasHermesAgentTransferred;

    private BioinspiredProtocolsEnum bioinspiredProtocol;

    private TrophicLevelEnum trophicLevelEnum;

    public AgentTransferRequestMessageDto(String senderIdentification,
                                          List<String> nameOfAgentsToBeTransferred,
                                          boolean hasHermesAgentTransferred,
                                          BioinspiredProtocolsEnum bioinspiredProtocol,
                                          TrophicLevelEnum trophicLevelEnum) {
        this.senderIdentification = senderIdentification;
        this.nameOfAgentsToBeTransferred = nameOfAgentsToBeTransferred;
        this.hasHermesAgentTransferred = hasHermesAgentTransferred;
        this.bioinspiredProtocol = bioinspiredProtocol;
        this.trophicLevelEnum = trophicLevelEnum;
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

    public TrophicLevelEnum getTrophicLevel() {
        return trophicLevelEnum;
    }

}
