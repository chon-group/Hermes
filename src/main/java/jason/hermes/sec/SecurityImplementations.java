package jason.hermes.sec;

public enum SecurityImplementations {

    NO_SECURITY(NoSecurity.class.getName(), new NoSecurity());

    private String securityClassName;

    private CommunicationSecurity securityImplementation;

    SecurityImplementations(String securityClassName, CommunicationSecurity securityImplementation) {
        this.securityClassName = securityClassName;
        this.securityImplementation = securityImplementation;
    }

    public String getSecurityClassName() {
        return securityClassName;
    }

    public CommunicationSecurity getSecurityImplementation() {
        return securityImplementation;
    }

}
