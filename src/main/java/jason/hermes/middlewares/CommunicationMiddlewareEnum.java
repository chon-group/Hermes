package jason.hermes.middlewares;

import jason.hermes.config.Configuration;
import jason.hermes.config.ContextNetConfiguration;

public enum CommunicationMiddlewareEnum {

    CONTEXT_NET("ContextNet", new ContextNetConfiguration(), new ContextNetMiddleware());

    private String name;

    private Configuration configuration;

    private CommunicationMiddleware communicationMiddleware;

    CommunicationMiddlewareEnum(String name, Configuration configuration,
                                CommunicationMiddleware communicationMiddleware) {
        this.name = name;
        this.configuration = configuration;
        this.communicationMiddleware = communicationMiddleware;
    }

    public String getName() {
        return name;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public CommunicationMiddleware getCommunicationMiddleware() {
        return communicationMiddleware;
    }

}
