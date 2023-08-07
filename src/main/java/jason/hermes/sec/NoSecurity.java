package jason.hermes.sec;

import jason.asSemantics.Message;
import jason.asSyntax.parser.ParseException;

public class NoSecurity implements CommunicationSecurity {

    @Override
    public Message decrypt(String messageReceived) {
        try {
            return Message.parseMsg(messageReceived);
        } catch (ParseException e) {
            throw new RuntimeException("Was not possible to convert the receivedMessage to a JasonMessage!");
        }
    }

    @Override
    public String encrypt(Message message) {
        return message.toString();
    }
}
