package jason.hermes.capabilities.bioinspiredProtocols.mapper;

import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredData;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;

public abstract class BioinspiredDataToAgentTransferMessageDto {

    public static AgentTransferRequestMessageDto mapperToAgentTransferRequestMessageDto(BioinspiredData bioinspiredData) {
        return new AgentTransferRequestMessageDto(
                bioinspiredData.getSenderIdentification(),
                bioinspiredData.getNameOfAgentsToBeTransferred(),
                bioinspiredData.isHasHermesAgentTransferred(),
                bioinspiredData.getBioinspiredProtocol(),
                bioinspiredData.getMyTrophicLevel(),
                bioinspiredData.isEntireMAS());
    }

}
