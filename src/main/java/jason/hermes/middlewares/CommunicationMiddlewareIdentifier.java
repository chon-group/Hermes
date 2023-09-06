package jason.hermes.middlewares;

import jason.asSyntax.Literal;
import jason.hermes.config.Configuration;

public class CommunicationMiddlewareIdentifier {

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

    public static CommunicationMiddleware identify(Literal configurationBelief) {
        String configuration = configurationBelief.toString();
        if (configuration != null && !configuration.trim().isEmpty()) {
            String configurationClassName = "";
            int initialIndex = configuration.indexOf("(");
            if (initialIndex != -1) {
                configurationClassName = configuration.substring(0, initialIndex);
            }
            if (!configurationClassName.isEmpty()) {
                for (CommunicationMiddlewareEnum communicationMiddlewareEnum : CommunicationMiddlewareEnum.values()) {
                    if (communicationMiddlewareEnum.getConfiguration().getClass().getSimpleName().equalsIgnoreCase(
                            configurationClassName)) {
                        Configuration configurationByBelief = communicationMiddlewareEnum.getConfiguration().getByBelief(configurationBelief);
                        CommunicationMiddleware communicationMiddleware = communicationMiddlewareEnum.getCommunicationMiddleware();
                        communicationMiddleware.setConfiguration(configurationByBelief);
                        return communicationMiddleware;
                    }
                }
            }
        }
        return null;
    }

}
