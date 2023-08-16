package jason.hermes.bioinspired.dto;

import java.io.Serializable;

public class AgentTransferResponseMessageDto extends AgentTransferMessageDto implements Serializable {

    private boolean canBeTransferred;

    public AgentTransferResponseMessageDto(boolean canBeTransferred) {
        this.canBeTransferred = canBeTransferred;
    }

    public boolean isCanBeTransferred() {
        return canBeTransferred;
    }

}
