package jason.hermes.capabilities.manageConnections.middlewares.identifier;

import jason.hermes.capabilities.manageConnections.configuration.Configuration;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddlewareEnum;

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
