package jason;

import jason.architecture.AgArch;
import jason.asSemantics.Message;
import jason.bb.BeliefBase;
import jason.hermes.InComingMessages;
import jason.hermes.bioinspired.BioinspiredData;
import jason.hermes.bioinspired.BioinspiredProcessor;
import jason.hermes.bioinspired.BioinspiredStageEnum;
import jason.hermes.bioinspired.DominanceDegrees;
import jason.hermes.config.Configuration;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.middlewares.CommunicationMiddlewareIdentifier;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;
import jason.infra.local.RunLocalMAS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class Hermes extends AgArch {

    private final HashMap<String, CommunicationMiddleware> communicationMiddlewareHashMap;

    private BioinspiredData bioinspiredData;


    public Hermes() {
        super();
        this.communicationMiddlewareHashMap = new HashMap<>();
        this.bioinspiredData = new BioinspiredData(DominanceDegrees.LOW_RANK);
    }

    @Override
    public void init() throws Exception {
        super.init();

        BeliefBase beliefBase = this.getTS().getAg().getBB();

        List<String> beliefByStartWithList = BeliefUtils.getBeliefByStartWith(beliefBase,
                BeliefUtils.MY_DOMINANCE_DEGREE_PREFIX);

        if (beliefByStartWithList.isEmpty()) {
            BeliefUtils.addBelief(BeliefUtils.MY_DOMINANCE_DEGREE_VALUE, BeliefBase.ASelf,
                    this.bioinspiredData.getMyDominanceDegree().name(), this.getTS().getAg());
        } else {
            String source = HermesUtils.getParameterInString(BeliefBase.ASelf);
            List<String> beliefValue = BeliefUtils.getBeliefValue(beliefByStartWithList, source);
            String value = HermesUtils.treatString(beliefValue.get(0));
            DominanceDegrees dominanceDegrees = DominanceDegrees.get(value);
            this.bioinspiredData.setMyDominanceDegree(dominanceDegrees);
        }

        BeliefUtils.replaceBelief(BeliefUtils.MY_MAS_BELIEF_PREFIX, BeliefUtils.MY_MAS_BELIEF_VALUE, BeliefBase.ASelf,
                RunLocalMAS.getRunner().getProject().getSocName(), this.getTS().getAg());
    }

    public CommunicationMiddleware getCommunicationMiddleware(String connectionIdentifier) {
        // TODO: Adicionar uma validação para quando não existe a conexão com o identificador passado.
        return this.communicationMiddlewareHashMap.get(connectionIdentifier);
    }

    public void addConnectionConfiguration(Configuration configuration) {
        CommunicationMiddleware communicationMiddleware = CommunicationMiddlewareIdentifier.identify(configuration);
        this.communicationMiddlewareHashMap.put(configuration.getConnectionIdentifier(), communicationMiddleware);
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

        if (!this.bioinspiredData.bioinspiredTransferenceActive()) {
            this.bioinspiredData = BioinspiredProcessor.getBioinspiredRole(
                    inComingMessages.getAgentTransferRequestMessageDto(),
                    this.bioinspiredData);
        }

        if (this.bioinspiredData.bioinspiredTransferenceActive()) {
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
                this.bioinspiredData.clean();
                String logMessage = "The execution of the protocol ended at "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
                BioInspiredUtils.log(Level.INFO, logMessage);

            }
        }
    }
}
