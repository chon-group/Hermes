package jason.hermes.bioinspired;

import java.util.List;

public class BioinspiredData {

    private List<String> nameOfAgentsToBeTransferred;

    private BioinspiredProtocolsEnum bioinspiredProtocol;

    private List<AslTransferenceModel> transferredAgentsAslModel;

    private String senderIdentification;

    private String receiverIdentification;

    private boolean hasHermesAgentTransferred;

    private String connectionIdentifier;

    private BioinspiredRoleEnum bioinspiredRole;

    private BioinspiredStageEnum bioinspiredStage;

    public BioinspiredData(List<String> nameOfAgentsToBeTransferred, BioinspiredProtocolsEnum bioinspiredProtocol,
                           boolean hasHermesAgentTransferred, String connectionIdentifier,
                           BioinspiredRoleEnum bioinspiredRole, BioinspiredStageEnum bioinspiredStage) {
        this.nameOfAgentsToBeTransferred = nameOfAgentsToBeTransferred;
        this.bioinspiredProtocol = bioinspiredProtocol;
        this.hasHermesAgentTransferred = hasHermesAgentTransferred;
        this.connectionIdentifier = connectionIdentifier;
        this.bioinspiredRole = bioinspiredRole;
        this.bioinspiredStage = bioinspiredStage;
    }

    public BioinspiredData(List<String> nameOfAgentsToBeTransferred, BioinspiredProtocolsEnum bioinspiredProtocol,
                           String receiverIdentification, boolean hasHermesAgentTransferred, BioinspiredRoleEnum bioinspiredRole,
                           BioinspiredStageEnum bioinspiredStage) {
        this.nameOfAgentsToBeTransferred = nameOfAgentsToBeTransferred;
        this.bioinspiredProtocol = bioinspiredProtocol;
        this.receiverIdentification = receiverIdentification;
        this.hasHermesAgentTransferred = hasHermesAgentTransferred;
        this.bioinspiredRole = bioinspiredRole;
        this.bioinspiredStage = bioinspiredStage;
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
}