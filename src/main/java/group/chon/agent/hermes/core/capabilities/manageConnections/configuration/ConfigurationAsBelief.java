package group.chon.agent.hermes.core.capabilities.manageConnections.configuration;

import jason.asSyntax.Literal;

public interface ConfigurationAsBelief {

    Literal toBelief();

    Configuration getByBelief(Literal belief);

}
