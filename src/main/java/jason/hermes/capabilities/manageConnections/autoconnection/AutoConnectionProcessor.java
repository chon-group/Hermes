package jason.hermes.capabilities.manageConnections.autoconnection;

import jason.Hermes;
import jason.asSemantics.Agent;
import jason.bb.BeliefBase;
import jason.hermes.capabilities.manageConnections.configuration.Configuration;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddlewareEnum;
import jason.hermes.utils.BeliefUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AutoConnectionProcessor {

    private static final Logger LOGGER = Logger.getLogger("HERMES_AUTO_CONNECTION_PROCESSOR");

    public static void initAutoConnection(Hermes hermes) {
        BeliefBase beliefBase = hermes.getTS().getAg().getBB();
        for (CommunicationMiddlewareEnum communicationMiddlewareEnum : CommunicationMiddlewareEnum.values()) {
            String classNameFirstCharacterLowerCase = BeliefUtils.getPrefix(communicationMiddlewareEnum.getConfiguration()
                    .getClass());
            List<String> communicationMiddlewareList = BeliefUtils.getBeliefsInStringByFunction(beliefBase,
                    classNameFirstCharacterLowerCase);
            if (!communicationMiddlewareList.isEmpty()) {
                boolean wasConnected = false;
                for (String communicationMiddlewareEnumValue : communicationMiddlewareList) {
                    Configuration configurationByBelief = communicationMiddlewareEnum.getConfiguration().getByBelief(
                            BeliefUtils.parseLiteralWithNamespace(communicationMiddlewareEnumValue));
                    hermes.addConnectionConfiguration(configurationByBelief);
                    if (configurationByBelief.isConnected()) {
                        wasConnected = true;
                    }
                }
                if (wasConnected) {
                    AutoConnectionProcessor.autoConnection(hermes);
                }
            }
        }
    }

    public static void autoConnection(Hermes hermes) {
        Timer timer = new Timer();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                for (String connectionIdentifier : hermes.getCommunicationMiddlewareHashMap().keySet()) {
                    if (!hermes.wasMoved()) {
                        CommunicationMiddleware communicationMiddleware = hermes
                                .getCommunicationMiddleware(connectionIdentifier);
                        if (communicationMiddleware.isConnected()) {
                            communicationMiddleware.connect();
                            AutoConnectionProcessor.log(Level.INFO, "The Hermes was not moved '"
                                    + hermes.getAgName() + "' and automatically connected successfully");
                        } else {
                            AutoConnectionProcessor.log(Level.INFO, "The Hermes was not moved '"
                                    + hermes.getAgName() + "' but it believes it is not connected");
                        }
                    } else {
                        AutoConnectionProcessor.log(Level.INFO, "The Hermes was moved '"
                                + hermes.getAgName() + "'.");
                    }
                }
            }
        };

        timer.schedule(tarefa, 1000);
    }

    public static void autoConnection(List<Agent> hermesAgentsTransferredList) {
        Timer timer = new Timer();
        final List<Agent> hermesAgentsTransferredListClone = new ArrayList<>(hermesAgentsTransferredList);
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                for (Agent hermesAgentTransferred : hermesAgentsTransferredListClone) {
                    Hermes agArch = (Hermes) hermesAgentTransferred.getTS().getAgArch();
                    for (String connectionIdentifier : agArch.getCommunicationMiddlewareHashMap().keySet()) {
                        CommunicationMiddleware communicationMiddleware = agArch
                                .getCommunicationMiddleware(connectionIdentifier);
                        if (communicationMiddleware.isConnected()) {
                            communicationMiddleware.connect();
                            AutoConnectionProcessor.log(Level.INFO, "The Hermes transferred agent '"
                                    + agArch.getAgName() + "' automatically connected successfully");
                        } else {
                            AutoConnectionProcessor.log(Level.INFO, "The Hermes transferred agent '"
                                    + agArch.getAgName() + "' was not connected before the transfer");
                        }
                    }
                }
            }
        };

        timer.schedule(tarefa, 5000);
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception | Error e) {
            //ignore
        }
    }

}
