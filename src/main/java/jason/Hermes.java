package jason;

import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.Message;
import jason.asSyntax.Literal;
import jason.bb.BeliefBase;
import jason.hermes.InComingMessages;
import jason.hermes.bioinspired.*;
import jason.hermes.config.Configuration;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.middlewares.CommunicationMiddlewareEnum;
import jason.hermes.middlewares.CommunicationMiddlewareIdentifier;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;
import jason.infra.local.RunLocalMAS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Hermes extends AgArch implements Observer {

    private final HashMap<String, CommunicationMiddleware> communicationMiddlewareHashMap;

    private BioinspiredData bioinspiredData;

    private boolean autoLocalization;

    private boolean moved;


    public Hermes() {
        super();
        this.communicationMiddlewareHashMap = new HashMap<>();
        this.bioinspiredData = new BioinspiredData(DominanceDegrees.LOW_RANK);
        this.autoLocalization = false;
        this.moved = false;
    }

    @Override
    public void init() throws Exception {
        super.init();

        BeliefBase beliefBase = this.getTS().getAg().getBB();

        List<String> beliefByStartWithList = BeliefUtils.getBeliefsInStringByStartWith(beliefBase,
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

        BeliefUtils.replaceAllBelief(BeliefUtils.MY_MAS_BELIEF_PREFIX, BeliefUtils.MY_MAS_BELIEF_VALUE, BeliefBase.ASelf,
                RunLocalMAS.getRunner().getProject().getSocName(), this.getTS().getAg());

        for (CommunicationMiddlewareEnum communicationMiddlewareEnum : CommunicationMiddlewareEnum.values()) {
            String classNameFirstCharacterLowerCase = BeliefUtils.getPrefix(communicationMiddlewareEnum.getConfiguration()
                    .getClass());
            List<String> communicationMiddlewareList = BeliefUtils.getBeliefsInStringByStartWith(beliefBase,
                    classNameFirstCharacterLowerCase);
            if (!communicationMiddlewareList.isEmpty()) {
                boolean wasConnected = false;
                for (String communicationMiddlewareEnumValue : communicationMiddlewareList) {
                    Configuration configurationByBelief = communicationMiddlewareEnum.getConfiguration().getByBelief(
                            Literal.parseLiteral(communicationMiddlewareEnumValue));
                    this.addConnectionConfiguration(configurationByBelief);
                    if (configurationByBelief.isConnected()) {
                        wasConnected = true;
                    }
                }
                if (wasConnected) {
                    BioinspiredProcessor.autoConnection(this);
                }
            }
        }

    }

    public CommunicationMiddleware getCommunicationMiddleware(String connectionIdentifier) {
        // TODO: Adicionar uma validação para quando não existe a conexão com o identificador passado.
        return this.communicationMiddlewareHashMap.get(connectionIdentifier);
    }

    public HashMap<String, CommunicationMiddleware> getCommunicationMiddlewareHashMap() {
        return communicationMiddlewareHashMap;
    }

    public void addConnectionConfiguration(Configuration configuration) {
        configuration.addObserver(this);
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

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean wasMoved() {
        return moved;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Configuration) {
            Configuration configuration = (Configuration) o;
            String prefix = BeliefUtils.getPrefix(configuration.getClass());
            List<Literal> beliefsByStartWith = BeliefUtils.getBeliefsByStartWith(this.getTS().getAg().getBB(), prefix);
            for (Literal literal : beliefsByStartWith) {
                Configuration byBelief = configuration.getByBelief(literal);
                if (byBelief.getConnectionIdentifier().equals(configuration.getConnectionIdentifier())) {
                    BeliefUtils.replaceBelief(configuration.toBelief(), literal, this.getTS().getAg());
                }
            }
        }
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

        if (!this.autoLocalization) {
            List<Agent> agentsOfTheMAS = RunLocalMAS.getRunner().getAgs().values().stream()
                    .map(localAgArch -> localAgArch.getTS().getAg()).collect(Collectors.toList());
            BioinspiredProcessor.autoLocalization(this.getAgName(), agentsOfTheMAS, false);
        }

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
                if (BioinspiredRoleEnum.RECEIVED.equals(this.bioinspiredData.getBioinspiredRole())
                        && this.bioinspiredData.isHasHermesAgentTransferred()) {
                    BioinspiredProcessor.autoConnection(this.bioinspiredData.getHermesAgentsTransferred());
                }
                this.bioinspiredData.clean();
                String logMessage = "The execution of the protocol ended at "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
                BioInspiredUtils.log(Level.INFO, logMessage);

            }
        }
    }
}
