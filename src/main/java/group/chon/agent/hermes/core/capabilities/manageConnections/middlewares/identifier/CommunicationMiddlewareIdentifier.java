package group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.identifier;

import group.chon.agent.hermes.core.capabilities.manageConnections.configuration.Configuration;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddlewareEnum;

public abstract class CommunicationMiddlewareIdentifier {

    public static CommunicationMiddleware identify(Configuration configuration) {
        // TODO: verificar oq deve acontecer se não foi possivel identificar o middleware de comunicação.
        for (CommunicationMiddlewareEnum communicationMiddlewareEnum : CommunicationMiddlewareEnum.values()) {
            if (communicationMiddlewareEnum.getConfiguration().getClass().getName().equals(
                    configuration.getClass().getName())) {
                CommunicationMiddleware communicationMiddleware = communicationMiddlewareEnum.getCommunicationMiddleware();
                communicationMiddleware.setConfiguration(configuration);
                return communicationMiddleware;
            }
        }

        return null;
    }

}
