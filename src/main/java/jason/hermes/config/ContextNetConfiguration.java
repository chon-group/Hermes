package jason.hermes.config;

import jason.asSyntax.Literal;
import jason.hermes.sec.CommunicationSecurity;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.HermesUtils;

public class ContextNetConfiguration extends Configuration{

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

    public String getGatewayIP() {
        return gatewayIP;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public String getMyUUIDString() {
        return myUUIDString;
    }

    @Override
    public Literal toBelief() {
        StringBuilder stringBuilder = new StringBuilder();
        String className = this.getClass().getSimpleName();
        char firstChar = Character.toLowerCase(className.charAt(0));
        String classNameFirstCharacterLowerCase = firstChar + className.substring(1);
        stringBuilder.append(classNameFirstCharacterLowerCase);
        stringBuilder.append("(");
        stringBuilder.append("\"").append(getConnectionIdentifier()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getGatewayIP()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getGatewayPort()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getMyUUIDString()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(isConnected()).append("\"").append(BeliefUtils.BELIEF_SEPARATOR);
        stringBuilder.append("\"").append(getSecurity().getClass().getSimpleName()).append("\"");
        stringBuilder.append(")");
        return Literal.parseLiteral(stringBuilder.toString());
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
            boolean connected = Boolean.getBoolean(HermesUtils.treatString(configurationAtributes[4]));
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
