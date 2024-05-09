package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.mapper;

import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredData;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredRoleEnum;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredStageEnum;
import group.chon.agent.hermes.core.capabilities.manageTrophicLevel.TrophicLevelEnum;

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
