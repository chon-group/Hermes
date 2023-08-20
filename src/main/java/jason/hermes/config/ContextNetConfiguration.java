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

}
