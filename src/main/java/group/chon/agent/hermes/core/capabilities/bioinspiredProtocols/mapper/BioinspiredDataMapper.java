package jason.hermes.capabilities.bioinspiredProtocols.mapper;

import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredData;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredRoleEnum;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredStageEnum;
import jason.hermes.capabilities.manageTrophicLevel.TrophicLevelEnum;

public abstract class BioinspiredDataMapper {

    public static AgentTransferRequestMessageDto mapperToAgentTransferRequestMessageDto(BioinspiredData bioinspiredData) {
        return new AgentTransferRequestMessageDto(
                bioinspiredData.getSenderIdentification(),
                bioinspiredData.getNameOfAgentsToBeTransferred(),
                bioinspiredData.isHasHermesAgentTransferred(),
                bioinspiredData.getBioinspiredProtocol(),
                bioinspiredData.getMyTrophicLevel(),
                bioinspiredData.isEntireMAS());
    }

    public static BioinspiredData mapperByAgentTransferRequestMessageDto(AgentTransferRequestMessageDto agentTransferRequestMessageDto,
                                                                         TrophicLevelEnum trophicLevelEnum) {
        return new BioinspiredData(agentTransferRequestMessageDto.getNameOfAgentsToBeTransferred(),
                agentTransferRequestMessageDto.getBioinspiredProtocol(),
                agentTransferRequestMessageDto.getSenderIdentification(),
                agentTransferRequestMessageDto.isHasHermesAgentTransferred(),
                BioinspiredRoleEnum.RECEIVED, BioinspiredStageEnum.RECEIVE_TRANSFER_REQUEST, trophicLevelEnum,
                agentTransferRequestMessageDto.getTrophicLevelEnum(), agentTransferRequestMessageDto.isEntireMAS());
    }

}
