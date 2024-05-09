package group.chon.agent.hermes.core.capabilities.manageConnections.middlewares;

import group.chon.agent.hermes.core.capabilities.manageConnections.configuration.Configuration;
import group.chon.agent.hermes.core.capabilities.manageConnections.configuration.ContextNetConfiguration;

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
        return this.configuration.clone();
    }

    public CommunicationMiddleware getCommunicationMiddleware() {
        return this.communicationMiddleware.clone();
    }

}
