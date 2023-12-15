package jason;

import jason.architecture.AgArch;
import jason.asSemantics.Message;
import jason.asSyntax.Literal;
import jason.hermes.InComingMessages;
import jason.hermes.capabilities.autoLocalization.AutoLocalizationProcessor;
import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredData;
import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredProcessor;
import jason.hermes.capabilities.cryogenate.CryogenicProcessor;
import jason.hermes.capabilities.manageConnections.autoconnection.AutoConnectionProcessor;
import jason.hermes.capabilities.manageConnections.configuration.Configuration;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.capabilities.manageConnections.middlewares.identifier.CommunicationMiddlewareIdentifier;
import jason.hermes.capabilities.manageTrophicLevel.TrophicLevelEnum;
import jason.hermes.capabilities.manageTrophicLevel.TrophicLevelProcessor;
import jason.hermes.capabilities.socialSkillsWithOutside.SendOutProcessor;
import jason.hermes.utils.BeliefUtils;

import java.util.*;

public class Hermes extends AgArch implements Observer {

    private final HashMap<String, CommunicationMiddleware> communicationMiddlewareHashMap;
    private BioinspiredData bioinspiredData;
    private boolean moved;


    public Hermes() {
        super();
        this.communicationMiddlewareHashMap = new HashMap<>();
        this.bioinspiredData = new BioinspiredData(TrophicLevelEnum.PRODUCER);
        this.moved = false;
    }

    @Override
    public void init() throws Exception {
        super.init();

        TrophicLevelProcessor.initTrophicLevel(this.getTS().getAg(), this.bioinspiredData);

        AutoLocalizationProcessor.initAutoLocalization(this.getTS().getAg());

        AutoConnectionProcessor.initAutoConnection(this);
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
            List<Literal> beliefsByStartWith = BeliefUtils.getBeliefsByFunction(this.getTS().getAg().getBB(), prefix);
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

        AutoLocalizationProcessor.updateOtherAgentsLocalization(this);

        InComingMessages inComingMessages = new InComingMessages(this.bioinspiredData, this.communicationMiddlewareHashMap);

        Map<String, List<Message>> allReceivedMessages = inComingMessages.getMessages();

        SendOutProcessor.processMessages(allReceivedMessages, this);

        BioinspiredProcessor.processMessages(this, inComingMessages);

        CryogenicProcessor.checkIfMASMustBeCryogenated(this.getTS());
    }

    @Override
    public void stop() {
        for (CommunicationMiddleware communicationMiddleware : this.getCommunicationMiddlewareHashMap().values()) {
            if (communicationMiddleware.isConnected()) {
                communicationMiddleware.disconnect();
            }
        }

        super.stop();
    }
}
