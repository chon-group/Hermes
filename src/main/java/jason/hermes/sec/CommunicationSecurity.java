package jason.hermes.sec;

import jason.asSemantics.Message;

public interface CommunicationSecurity {

    Message decrypt(String messageReceived);

    String encrypt(Message message);

}
