package jason.hermes.capabilities.manageConnections.sec;

public class NoSecurity implements CommunicationSecurity {

    @Override
    public Object decrypt(String messageReceived) {
        return messageReceived;
    }

    @Override
    public String encrypt(Object message) {
        return message.toString();
    }

    @Override
    public CommunicationSecurity clone() {
        return new NoSecurity();
    }
}
