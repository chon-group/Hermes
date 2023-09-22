package jason.hermes.sendOut;

import jason.Hermes;
import jason.asSemantics.Message;
import jason.asSyntax.*;
import jason.hermes.OutGoingMessage;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.utils.BioInspiredUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SendOutProcessor {

    public static void process(Map<String, List<Message>> messageMap, Hermes hermes) {
        for (String connectionIdentifier : messageMap.keySet()) {
            List<Message> messageList = messageMap.get(connectionIdentifier);
            for (Message message : messageList) {
                SendParserForceEnum sendParserForceEnum = SendParserForceEnum.get(message.getIlForce());
                if (sendParserForceEnum != null) {
                    if (sendParserForceEnum.isHasToRespond()) {
                        Message messageToRespond = messageToRespond(message, sendParserForceEnum, hermes);
                        CommunicationMiddleware communicationMiddleware = hermes
                                .getCommunicationMiddleware(connectionIdentifier);
                        OutGoingMessage.sendMessage(messageToRespond, communicationMiddleware);
                    } else {
                        if (SendParserForceEnum.tellHow.name().equals(message.getIlForce())) {
                            // TODO: verificar se isso não acontece com crenca tambem, ou seja, fazer um teste de
                            //  mandar um tell via sendOut, criogenar o MAS e ver se o source da crenca ficou correto.
                            message.setSender("\"" + message.getSender() + "\"");
                        }
                        hermes.getTS().getC().addMsg(message);
                    }
                } else {
                    BioInspiredUtils.log(Level.SEVERE, "Error Identifying the force '" + message.getIlForce()
                            + "' of message received ");
                }

            }
        }
    }

    public static Message messageToRespond(Message receivedMessage, SendParserForceEnum sendParserForceEnum,
                                           Hermes hermes) {
        Term propCont = (Term) receivedMessage.getPropCont();
        Literal content = null;
        if (propCont.isLiteral()) {
            content = Literal.parseLiteral(propCont.toString());
        }
        PredicateIndicator predicateIndicator = new PredicateIndicator(propCont.toString(), 1);
        if (content != null) {
            predicateIndicator = new PredicateIndicator(content.getFunctor(), content.getArity());
        }
        Iterator<Literal> candidateBeliefs = hermes.getTS().getAg().getBB().getCandidateBeliefs(predicateIndicator);
        Message messageToRespond = new Message(sendParserForceEnum.getForceToRespond(), receivedMessage.getReceiver(), receivedMessage.getSender(), null);
        if (SendParserForceEnum.askOne.name().equals(receivedMessage.getIlForce())) {
            Literal belief = candidateBeliefs.next();
            belief.setAnnots(null);
            messageToRespond.setPropCont(belief);
        } else if (SendParserForceEnum.askAll.name().equals(receivedMessage.getIlForce())) {
            ListTerm tail = new ListTermImpl();
            while (candidateBeliefs.hasNext()) {
                Literal next = candidateBeliefs.next();
                next.setAnnots(null);
                Term t = next;
                tail.append(t);
            }
            messageToRespond.setPropCont(tail);
        } else if (SendParserForceEnum.askHow.name().equals(receivedMessage.getIlForce())) {
            // TODO: fazer o askHow.
        } else {
            BioInspiredUtils.log(Level.SEVERE, "Error identifying the force '" + receivedMessage.getIlForce()
                    + "' to respond the message received");
        }
        messageToRespond.setInReplyTo(receivedMessage.getMsgId());

        return messageToRespond;
    }

}
