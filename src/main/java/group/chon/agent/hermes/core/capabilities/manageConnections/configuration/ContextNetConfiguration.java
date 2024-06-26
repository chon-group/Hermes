package group.chon.agent.hermes.core.capabilities.manageConnections.configuration;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import group.chon.agent.hermes.core.capabilities.manageConnections.sec.CommunicationSecurity;
import group.chon.agent.hermes.core.capabilities.manageConnections.sec.NoSecurity;
import group.chon.agent.hermes.core.utils.ArgsUtils;
import group.chon.agent.hermes.core.utils.BeliefUtils;
import group.chon.agent.hermes.core.utils.HermesUtils;

public class ContextNetConfiguration extends Configuration {

    public static final String BELIEF_PREFIX = BeliefUtils.getPrefix(ContextNetConfiguration.class);
    private String gatewayIP;
    private int gatewayPort;
    private String myUUIDString;

    public ContextNetConfiguration() {
    }

    public ContextNetConfiguration(String connectionIdentifier, CommunicationSecurity communicationSecurity,
                                   String gatewayIP, int gatewayPort, String myUUIDString) {
        super.setConnectionIdentifier(connectionIdentifier);
        super.setSecurity(communicationSecurity);
        this.gatewayIP = gatewayIP;
        this.gatewayPort = gatewayPort;
        this.myUUIDString = myUUIDString;
    }

    public ContextNetConfiguration(String connectionIdentifier, CommunicationSecurity communicationSecurity,
                                   String gatewayIP, int gatewayPort, String myUUIDString, boolean connected) {
        super.setConnectionIdentifier(connectionIdentifier);
        super.setSecurity(communicationSecurity);
        super.setConnected(connected);
        this.gatewayIP = gatewayIP;
        this.gatewayPort = gatewayPort;
        this.myUUIDString = myUUIDString;
    }

    public String getGatewayIP() {
        return gatewayIP;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public String getMyUUIDString() {
        return myUUIDString;
    }

    public void setMyUUIDString(String myUUIDString) {
        this.myUUIDString = myUUIDString;
    }

    @Override
    public ContextNetConfiguration get(Term[] args, int minArgs, int maxArgs) {
        String configurationIdentifier = ArgsUtils.getInString(args[0]);
        String gatewayIP = ArgsUtils.getInString(args[1]);
        int gatewayPort = Integer.parseInt(ArgsUtils.getInString(args[2]));
        String myUUID = ArgsUtils.getInString(args[3]);
        CommunicationSecurity securityImplementation = new NoSecurity();
        if (args.length == maxArgs) {
            String securityParam = ArgsUtils.getInString(args[4]);
            securityImplementation = HermesUtils.getSecurityImplementation(securityParam);
        }

        return new ContextNetConfiguration(configurationIdentifier,
                securityImplementation, gatewayIP, gatewayPort, myUUID);
    }

    @Override
    public ContextNetConfiguration clone() {
        return new ContextNetConfiguration(getConnectionIdentifier(), getSecurity(), getGatewayIP(), getGatewayPort(),
                getMyUUIDString(), isConnected());
    }

    @Override
    public Literal toBelief() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BeliefUtils.HERMES_NAMESPACE);
        stringBuilder.append(BeliefUtils.NAMESPACE_SEPARATOR);
        stringBuilder.append(BELIEF_PREFIX);
        stringBuilder.append("(");
        stringBuilder.append("\"").append(getConnectionIdentifier()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getGatewayIP()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getGatewayPort()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getMyUUIDString()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(isConnected()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getSecurity().getClass().getSimpleName()).append("\"");
        stringBuilder.append(")");
        return BeliefUtils.parseLiteralWithNamespace(stringBuilder.toString());
    }

    @Override
    public Configuration getByBelief(Literal belief) {
        String beliefString = belief.toString();

        int initialIndex = beliefString.indexOf("(");
        int finalIndex = beliefString.indexOf(")");
        if (initialIndex != -1 && finalIndex != -1) {
            String value = beliefString.substring(initialIndex + 1, finalIndex);
            String[] configurationAtributes = value.split(BeliefUtils.BELIEF_SEPARATOR);
            String configurationIdentifier = HermesUtils.treatString(configurationAtributes[0]);
            String gatewayIP = HermesUtils.treatString(configurationAtributes[1]);
            int gatewayPort = Integer.parseInt(HermesUtils.treatString(configurationAtributes[2]));
            String myUUIDString = HermesUtils.treatString(configurationAtributes[3]);
            boolean connected = Boolean.parseBoolean(HermesUtils.treatString(configurationAtributes[4]));
            String securityImplementationClassName = HermesUtils.treatString(configurationAtributes[5]);
            CommunicationSecurity securityImplementation = HermesUtils.getSecurityImplementation(securityImplementationClassName);
            ContextNetConfiguration contextNetConfiguration = new ContextNetConfiguration(configurationIdentifier,
                    securityImplementation, gatewayIP, gatewayPort, myUUIDString);
            contextNetConfiguration.setConnected(connected);
            return contextNetConfiguration;
        }

        return null;
    }

}
