package jason.hermes.config;

import jason.hermes.sec.CommunicationSecurity;

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
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String className = this.getClass().getSimpleName();
        char firstChar = Character.toLowerCase(className.charAt(0));
        String classNameFirstCharacterLowerCase = firstChar + className.substring(1);
        stringBuilder.append(classNameFirstCharacterLowerCase);
        stringBuilder.append("(");
        stringBuilder.append("\"").append(getConnectionIdentifier()).append("\"").append(",");
        stringBuilder.append("\"").append(getGatewayIP()).append("\"").append(",");
        stringBuilder.append("\"").append(getGatewayPort()).append("\"").append(",");
        stringBuilder.append("\"").append(getMyUUIDString()).append("\"");
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
