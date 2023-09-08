package jason.hermes.sec;

public interface CommunicationSecurity extends Cloneable{

    Object decrypt(String messageReceived);

    String encrypt(Object message);

    CommunicationSecurity clone();

}
