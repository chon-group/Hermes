package group.chon.agent.hermes.core.capabilities.manageConnections.sec;

public interface CommunicationSecurity extends Cloneable{

    Object decrypt(String messageReceived);

    String encrypt(Object message);

    CommunicationSecurity clone();

}
