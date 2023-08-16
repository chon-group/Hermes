package jason.hermes.sec;

public interface CommunicationSecurity {

    Object decrypt(String messageReceived);

    String encrypt(Object message);

}
