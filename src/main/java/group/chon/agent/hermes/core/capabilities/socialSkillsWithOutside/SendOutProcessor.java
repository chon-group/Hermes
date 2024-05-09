package group.chon.agent.hermes.core.capabilities.socialSkillsWithOutside;

import group.chon.agent.hermes.Hermes;
import jason.asSemantics.Message;
import jason.asSyntax.*;
import jason.asSyntax.parser.ParseException;
import group.chon.agent.hermes.core.OutGoingMessage;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.utils.HermesUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class SendOutProcessor {

    private static final Logger LOGGER = Logger.getLogger("HERMES_SEND_OUT_PROCESSOR");

    public static void processMessages(Map<String, List<Message>> messageMap, Hermes hermes) {
        for (String connectionIdentifier : messageMap.keySet()) {
            List<Message> messageList = messageMap.get(connectionIdentifier);
            for (Message message : messageList) {
                SendParserForceEnum sendParserForceEnum = SendParserForceEnum.get(message.getIlForce());
                if (sendParserForceEnum != null) {
                    if (sendParserForceEnum.isHasToRespond()) {
                        Message messageToRespond = SendOutProcessor.messageToRespond(message, sendParserForceEnum, hermes);
                        CommunicationMiddleware communicationMiddleware = hermes
                                .getCommunicationMiddleware(connectionIdentifier);
                        OutGoingMessage.sendMessage(messageToRespond, communicationMiddleware);
                    } else {
                        SendOutProcessor.treatPlanForUnTellHow(message, hermes);
                        SendOutProcessor.formatSenderOfMessage(message);

                        hermes.getTS().getC().addMsg(message);
                    }
                } else {
                    SendOutProcessor.log(Level.SEVERE, "Error Identifying the force '" + message.getIlForce()
                            + "' of message received ");
                }

            }
        }
    }

    private static Iterator<Literal> getCandidateBeliefs(Object messageContent, Hermes hermes) {
        Term propCont = (Term) messageContent;
        Literal content = null;
        if (propCont.isLiteral()) {
            content = Literal.parseLiteral(propCont.toString());
        }
        PredicateIndicator predicateIndicator = new PredicateIndicator(propCont.toString(), 1);
        if (content != null) {
            predicateIndicator = new PredicateIndicator(content.getFunctor(), content.getArity());
        }
        Iterator<Literal> candidateBeliefs = hermes.getTS().getAg().getBB().getCandidateBeliefs(predicateIndicator);
        return candidateBeliefs;
    }

    private static Message messageToRespond(Message receivedMessage, SendParserForceEnum sendParserForceEnum,
                                           Hermes hermes) {
        Message messageToRespond = new Message(sendParserForceEnum.getForceToRespond(), receivedMessage.getReceiver(), receivedMessage.getSender(), null);
        if (SendParserForceEnum.askOne.name().equals(receivedMessage.getIlForce())) {
            Iterator<Literal> candidateBeliefs = getCandidateBeliefs(receivedMessage.getPropCont(), hermes);
            Literal belief = candidateBeliefs.next();
            belief.setAnnots(null);
            messageToRespond.setPropCont(belief);
        } else if (SendParserForceEnum.askAll.name().equals(receivedMessage.getIlForce())) {
            Iterator<Literal> candidateBeliefs = getCandidateBeliefs(receivedMessage.getPropCont(), hermes);
            ListTerm tail = new ListTermImpl();
            while (candidateBeliefs.hasNext()) {
                Literal next = candidateBeliefs.next();
                next.setAnnots(null);
                Term t = next;
                tail.append(t);
            }
            messageToRespond.setPropCont(tail);
        } else if (SendParserForceEnum.askHow.name().equals(receivedMessage.getIlForce())) {
            final Trigger trigger = (Trigger) receivedMessage.getPropCont();
            PlanLibrary pl = hermes.getTS().getAg().getPL();
            List<Plan> plans = pl.getPlans();
            List<Plan> plansListTrigger = plans.stream().filter(plan -> plan.getTrigger().equals(trigger)).collect(Collectors.toList());
            ListTerm tail = new ListTermImpl();
            for (Plan plan : plansListTrigger) {
                try {
                    tail.append(HermesUtils.convertPlanToTerm(plan));
                } catch (ParseException e) {
                    SendOutProcessor.log(Level.SEVERE, "Error converting the plan '"
                            + plan.getTrigger().toString() + "' to Term to respond a AskHow request.");
                }
            }
            messageToRespond.setPropCont(tail);
        } else {
            SendOutProcessor.log(Level.SEVERE, "Error identifying the force '" + receivedMessage.getIlForce()
                    + "' to respond the message received");
        }
        messageToRespond.setInReplyTo(receivedMessage.getMsgId());

        return messageToRespond;
    }

    private static void treatPlanForUnTellHow(Message message, Hermes hermes) {
        if (SendParserForceEnum.untellHow.name().equals(message.getIlForce())) {
            Plan planToBeForgotten = (Plan) message.getPropCont();
            Term sourceTerm = null;
            if (planToBeForgotten.getLabel().hasSource()) {
                sourceTerm = planToBeForgotten.getLabel().getSources().get(0);
            }
            if (sourceTerm != null) {
                Atom source = new Atom(sourceTerm.toString());
                Plan plan1 = hermes.getTS().getAg().getPL().getPlans().stream().filter(plan ->
                                plan.getTrigger().equals(planToBeForgotten.getTrigger())
                                        && plan.getLabel().getSources().contains(source)).findFirst()
                        .orElse(null);
                if (plan1 != null) {
                    String planInString = HermesUtils.treatPlanStringFormat(plan1.toASString());
                    try {
                        message.setPropCont(ASSyntax.parseTerm(planInString));
                    } catch (ParseException e) {
                        SendOutProcessor.log(Level.SEVERE, "Error receiving a plan to be forgotten '"
                                + planToBeForgotten.toASString() +"'\n\n: " + e);
                    }
                }
            } else {
                SendOutProcessor.log(Level.SEVERE, "Error identifying the source of the plan '"
                        + planToBeForgotten.toASString() + "' to be forgotten.");
            }
        }
    }

    private static void formatSenderOfMessage(Message message) {
        if (!message.getSender().startsWith("\"") && !message.getSender().endsWith("\"")) {
            message.setSender("\"" + message.getSender() + "\"");
        }
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception | Error e) {
            //ignore
        }
    }

}
