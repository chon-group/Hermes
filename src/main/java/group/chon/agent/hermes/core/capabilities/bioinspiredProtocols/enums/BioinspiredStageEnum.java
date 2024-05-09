package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums;

public enum BioinspiredStageEnum {

    FINISHED(null, null),
    CONFIRMATION_TRANSFER(FINISHED, FINISHED),
    RECEIVE_CONFIRMATION_TRANSFER(FINISHED, FINISHED),
    CONTENT_TRANSFER(RECEIVE_CONFIRMATION_TRANSFER, FINISHED),
    RECEIVE_RESPONSE_TO_TRANSFER(CONTENT_TRANSFER, FINISHED),
    RECEIVE_CONTENT_TRANSFER(CONFIRMATION_TRANSFER, FINISHED),
    RESPONSE_TO_TRANSFER(RECEIVE_CONTENT_TRANSFER, FINISHED),
    RECEIVE_TRANSFER_REQUEST(RESPONSE_TO_TRANSFER, FINISHED),
    TRANSFER_REQUEST(RECEIVE_RESPONSE_TO_TRANSFER, FINISHED);

    private final BioinspiredStageEnum nextStage;

    private final BioinspiredStageEnum nextStageIfHasAnError;

    BioinspiredStageEnum(BioinspiredStageEnum nextStage, BioinspiredStageEnum nextStageIfHasAnError) {
        this.nextStage = nextStage;
        this.nextStageIfHasAnError = nextStageIfHasAnError;
    }

    public BioinspiredStageEnum getNextStage() {
        return nextStage;
    }

    public BioinspiredStageEnum getNextStageIfHasAnError() {
        return nextStageIfHasAnError;
    }

}
