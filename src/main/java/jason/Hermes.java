package jason;

import jason.architecture.AgArch;
import jason.asSemantics.Message;
import jason.hermes.InComingMessages;
import jason.hermes.bioinspired.BioinspiredData;
import jason.hermes.bioinspired.BioinspiredProcessor;
import jason.hermes.bioinspired.BioinspiredStageEnum;
import jason.hermes.config.Configuration;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.middlewares.ContextNetMiddleware;
import jason.hermes.utils.BioInspiredUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class Hermes extends AgArch {

    private HashMap<String, CommunicationMiddleware> communicationMiddlewareHashMap = new HashMap<>();

    private BioinspiredData bioinspiredData;

    public CommunicationMiddleware getCommunicationMiddleware(String connectionIdentifier) {
        // TODO: Adicionar uma validação para quando não existe a conexão com o identificador passado.
        return this.communicationMiddlewareHashMap.get(connectionIdentifier);
    }

    public void addConnectionConfiguration(Configuration configuration) {
        // TODO: criar uma abstração para identificar a instancia correta.
        ContextNetMiddleware contextNetMiddleware = new ContextNetMiddleware(configuration);
        this.communicationMiddlewareHashMap.put(configuration.getConnectionIdentifier(), contextNetMiddleware);
    }

    public String getFirstConnectionAvailable() {
        // TODO: Adicionar uma validação para quando não existe nenhuma conexão configurada.
        for (String connectionIdentifier : this.communicationMiddlewareHashMap.keySet()) {
            if (this.communicationMiddlewareHashMap.get(connectionIdentifier).isConnected()) {
                return connectionIdentifier;
            }
        }
        return "";
    }

    public BioinspiredData getBioinspiredData() {
        return bioinspiredData;
    }

    public void setBioinspiredData(BioinspiredData bioinspiredData) {
        this.bioinspiredData = bioinspiredData;
    }

    @Override
    public void checkMail() {
        super.checkMail();

        InComingMessages inComingMessages = new InComingMessages(this.bioinspiredData, this.communicationMiddlewareHashMap);

        List<Message> allReceivedMessages = inComingMessages.getMessages();

        for (Message message : allReceivedMessages) {
            getTS().getC().addMsg(message);
        }

        if (this.bioinspiredData == null) {
            this.bioinspiredData = BioinspiredProcessor.getBioinspiredRole(
                    inComingMessages.getAgentTransferRequestMessageDto());
        }

        if (this.bioinspiredData != null) {
            if (this.bioinspiredData.getSenderIdentification() == null) {
                this.bioinspiredData.setConnectionIdentifier(inComingMessages.getBioinspiredConnectionIdentifier());
                String myIdentification = this.communicationMiddlewareHashMap.get(inComingMessages.getBioinspiredConnectionIdentifier()).getAgentIdentification();
                this.bioinspiredData.setSenderIdentification(myIdentification);
            }
            BioinspiredProcessor.updateBioinspiredStage(this.bioinspiredData);
            if (!BioinspiredStageEnum.FINISHED.equals(this.bioinspiredData.getBioinspiredStage())) {
                BioinspiredProcessor.receiveBioinspiredMessage(this.getTS(), this.bioinspiredData,
                        this.getCommunicationMiddleware(this.bioinspiredData.getConnectionIdentifier()),
                        inComingMessages.getAgentTransferRequestMessageDto(),
                        inComingMessages.getAgentTransferResponseMessageDto(),
                        inComingMessages.getAgentTransferContentMessageDto(),
                        inComingMessages.getAgentTransferConfirmationMessageDto());
            } else {
                this.bioinspiredData = null;
                BioInspiredUtils.LOGGER.info("The execution of the protocol ended at "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));
            }
        }

    }
}
