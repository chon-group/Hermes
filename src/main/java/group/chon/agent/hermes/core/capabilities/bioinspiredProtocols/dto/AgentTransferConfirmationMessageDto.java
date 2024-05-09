package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto;

import java.io.Serializable;

public class AgentTransferConfirmationMessageDto extends AgentTransferMessageDto implements Serializable {

    private boolean agentTransferSuccess;

    private boolean canKill;

    public AgentTransferConfirmationMessageDto(boolean agentTransferSuccess, boolean canKill) {
        this.agentTransferSuccess = agentTransferSuccess;
        this.canKill = canKill;
    }

    public boolean isAgentTransferSuccess() {
        return agentTransferSuccess;
    }

    public boolean isCanKill() {
        return canKill;
    }

}
