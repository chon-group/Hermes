package jason.hermes.capabilities.bioinspiredProtocols;

import jason.asSemantics.Agent;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredRoleEnum;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredStageEnum;
import jason.hermes.capabilities.manageTrophicLevel.TrophicLevelEnum;
import jason.hermes.utils.aslFiles.AslTransferenceModel;

import java.util.List;

public class BioinspiredData {

    private List<String> nameOfAgentsToBeTransferred;

    private BioinspiredProtocolsEnum bioinspiredProtocol;

    private List<AslTransferenceModel> transferredAgentsAslModel;

    private String senderIdentification;

    private String receiverIdentification;

    private boolean hasHermesAgentTransferred;

    private List<Agent> hermesAgentsTransferred;

    private String connectionIdentifier;

    private BioinspiredRoleEnum bioinspiredRole;

    private BioinspiredStageEnum bioinspiredStage;
    private TrophicLevelEnum myTrophicLevelEnum;
    private TrophicLevelEnum otherMASTrophicLevelEnum;

    private List<String> nameOfAgentsInstantiated;

    private boolean entireMAS;

    public BioinspiredData(TrophicLevelEnum myTrophicLevelEnum) {
        this.myTrophicLevelEnum = myTrophicLevelEnum;
    }

    public BioinspiredData(List<String> nameOfAgentsToBeTransferred, BioinspiredProtocolsEnum bioinspiredProtocol,
                           boolean hasHermesAgentTransferred, String connectionIdentifier,
                           BioinspiredRoleEnum bioinspiredRole, BioinspiredStageEnum bioinspiredStage,
                           TrophicLevelEnum myTrophicLevelEnum, boolean entireMAS) {
        this.nameOfAgentsToBeTransferred = nameOfAgentsToBeTransferred;
        this.bioinspiredProtocol = bioinspiredProtocol;
        this.hasHermesAgentTransferred = hasHermesAgentTransferred;
        this.connectionIdentifier = connectionIdentifier;
        this.bioinspiredRole = bioinspiredRole;
        this.bioinspiredStage = bioinspiredStage;
        this.myTrophicLevelEnum = myTrophicLevelEnum;
        this.entireMAS = entireMAS;
    }

    public BioinspiredData(List<String> nameOfAgentsToBeTransferred, BioinspiredProtocolsEnum bioinspiredProtocol,
                           String receiverIdentification, boolean hasHermesAgentTransferred,
                           BioinspiredRoleEnum bioinspiredRole, BioinspiredStageEnum bioinspiredStage,
                           TrophicLevelEnum myTrophicLevelEnum, TrophicLevelEnum otherMASTrophicLevelEnum,
                           boolean entireMAS) {
        this.nameOfAgentsToBeTransferred = nameOfAgentsToBeTransferred;
        this.bioinspiredProtocol = bioinspiredProtocol;
        this.receiverIdentification = receiverIdentification;
        this.hasHermesAgentTransferred = hasHermesAgentTransferred;
        this.bioinspiredRole = bioinspiredRole;
        this.bioinspiredStage = bioinspiredStage;
        this.myTrophicLevelEnum = myTrophicLevelEnum;
        this.otherMASTrophicLevelEnum = otherMASTrophicLevelEnum;
        this.entireMAS = entireMAS;
    }

    public void clean() {
        this.nameOfAgentsToBeTransferred = null;
        this.bioinspiredProtocol = null;
        this.transferredAgentsAslModel = null;
        this.senderIdentification = null;
        this.receiverIdentification = null;
        this.hasHermesAgentTransferred = false;
        this.hermesAgentsTransferred = null;
        this.connectionIdentifier = null;
        this.bioinspiredRole = null;
        this.bioinspiredStage = null;
        this.otherMASTrophicLevelEnum = null;
        this.nameOfAgentsInstantiated = null;
        this.entireMAS = false;
    }

    public boolean bioinspiredTransferenceActive() {
        return this.bioinspiredProtocol != null && this.bioinspiredRole != null && this.bioinspiredStage != null;
    }

    public List<String> getNameOfAgentsToBeTransferred() {
        return nameOfAgentsToBeTransferred;
    }

    public void setNameOfAgentsToBeTransferred(List<String> nameOfAgentsToBeTransferred) {
        this.nameOfAgentsToBeTransferred = nameOfAgentsToBeTransferred;
    }

    public BioinspiredProtocolsEnum getBioinspiredProtocol() {
        return bioinspiredProtocol;
    }

    public void setBioinspiredProtocol(BioinspiredProtocolsEnum bioinspiredProtocol) {
        this.bioinspiredProtocol = bioinspiredProtocol;
    }

    public List<AslTransferenceModel> getTransferredAgentsAslModel() {
        return transferredAgentsAslModel;
    }

    public void setTransferredAgentsAslModel(List<AslTransferenceModel> transferredAgentsAslModel) {
        this.transferredAgentsAslModel = transferredAgentsAslModel;
    }

    public String getSenderIdentification() {
        return senderIdentification;
    }

    public void setSenderIdentification(String senderIdentification) {
        this.senderIdentification = senderIdentification;
    }

    public String getReceiverIdentification() {
        return receiverIdentification;
    }

    public void setReceiverIdentification(String receiverIdentification) {
        this.receiverIdentification = receiverIdentification;
    }

    public boolean isHasHermesAgentTransferred() {
        return hasHermesAgentTransferred;
    }

    public void setHasHermesAgentTransferred(boolean hasHermesAgentTransferred) {
        this.hasHermesAgentTransferred = hasHermesAgentTransferred;
    }

    public List<Agent> getHermesAgentsTransferred() {
        return hermesAgentsTransferred;
    }

    public void setHermesAgentsTransferred(List<Agent> hermesAgentsTransferred) {
        this.hermesAgentsTransferred = hermesAgentsTransferred;
    }

    public String getConnectionIdentifier() {
        return connectionIdentifier;
    }

    public void setConnectionIdentifier(String connectionIdentifier) {
        this.connectionIdentifier = connectionIdentifier;
    }

    public BioinspiredRoleEnum getBioinspiredRole() {
        return bioinspiredRole;
    }

    public void setBioinspiredRole(BioinspiredRoleEnum bioinspiredRole) {
        this.bioinspiredRole = bioinspiredRole;
    }

    public BioinspiredStageEnum getBioinspiredStage() {
        return bioinspiredStage;
    }

    public void setBioinspiredStage(BioinspiredStageEnum bioinspiredStage) {
        this.bioinspiredStage = bioinspiredStage;
    }

    public TrophicLevelEnum getMyTrophicLevel() {
        return myTrophicLevelEnum;
    }

    public void setMyTrophicLevel(TrophicLevelEnum myTrophicLevelEnum) {
        this.myTrophicLevelEnum = myTrophicLevelEnum;
    }

    public TrophicLevelEnum getOtherMASTrophicLevel() {
        return otherMASTrophicLevelEnum;
    }

    public void setOtherMASTrophicLevel(TrophicLevelEnum otherMASTrophicLevelEnum) {
        this.otherMASTrophicLevelEnum = otherMASTrophicLevelEnum;
    }

    public List<String> getNameOfAgentsInstantiated() {
        return nameOfAgentsInstantiated;
    }

    public void setNameOfAgentsInstantiated(List<String> nameOfAgentsInstantiated) {
        this.nameOfAgentsInstantiated = nameOfAgentsInstantiated;
    }

    public boolean isEntireMAS() {
        return entireMAS;
    }

}
