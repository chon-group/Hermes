package jason.hermes.capabilities.manageConnections.sec;

public enum CommunicationSecurityEnum {

    NO_SECURITY(NoSecurity.class.getName(), new NoSecurity());

    private String securityClassName;

    private CommunicationSecurity communicationSecurity;

    CommunicationSecurityEnum(String securityClassName, CommunicationSecurity communicationSecurity) {
        this.securityClassName = securityClassName;
        this.communicationSecurity = communicationSecurity;
    }

    public String getSecurityClassName() {
        return this.securityClassName;
    }

    public CommunicationSecurity getCommunicationSecurity() {
        return this.communicationSecurity.clone();
    }

}
