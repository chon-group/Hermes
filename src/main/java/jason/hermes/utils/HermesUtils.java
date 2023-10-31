package jason.hermes.utils;

import jason.Hermes;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.*;
import jason.asSyntax.parser.ParseException;
import jason.hermes.bioinspired.dto.AgentTransferConfirmationMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferContentMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferRequestMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferResponseMessageDto;
import jason.hermes.sec.CommunicationSecurity;
import jason.hermes.sec.NoSecurity;
import jason.hermes.sec.SecurityImplementations;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HermesUtils {

        private static final Logger LOGGER = Logger.getLogger("HERMES UTILS");

    public static Hermes checkArchClass(AgArch agArch, String internalActionName) throws JasonException {
        Hermes hermes = null;
        if(agArch instanceof Hermes) {
            hermes = (Hermes) agArch;
        } else {
            throw new JasonException(
                    "Was not possible to call " + internalActionName + "internal action because this AgArch is not a Hermes arch.");
        }
        return hermes;
    }

    public static Hermes checkArchClass(AgArch agArch) throws JasonException {
        Hermes hermes = null;
        if(agArch instanceof Hermes) {
            hermes = (Hermes) agArch;
        } else {
            throw new JasonException(
                    "Was not possible to cast the agArch '" + agArch.getClass().getSimpleName() + "' to a Hermes arch.");
        }
        return hermes;
    }

    public static CommunicationSecurity getSecurityImplementation(String securityClassName) {
        // TODO: Verificar o que deve ser feito se não for possivel identificar a implementação de segurança passada.
        SecurityImplementations[] securityImplementations = SecurityImplementations.values();
        for (SecurityImplementations securityImplementation : securityImplementations) {
            if (securityImplementation.getSecurityClassName().equalsIgnoreCase(securityClassName)
                    || securityImplementation.getSecurityImplementation().getClass().getSimpleName().equalsIgnoreCase(securityClassName)) {
                return securityImplementation.getSecurityImplementation();
            }
        }
        System.out.println("Was not possible to identify the security implementation with the name \""
                + securityClassName + "\"");
        return new NoSecurity();
    }

    public static String getParameterInString(Term term) {
        return term.toString().trim().replace("\"", "");
    }

    public static String treatPlanStringFormat(String planInString) {
        if (planInString.endsWith(".")) {
            planInString = planInString.substring(0, planInString.length() - 1);
        }
        if (!planInString.startsWith("{") && !planInString.endsWith("}")) {
            planInString = "{" + planInString + "}";
        }
        return planInString;
    }

    public static Term convertPlanToTerm(Plan plan) throws ParseException {
        Plan plan1 = (Plan) plan.clone();
        plan1.setLabel(null);
        String planInString = treatPlanStringFormat(plan1.toASString());
        return ASSyntax.parseTerm(planInString);
    }

    public static Term treatContentTerm(Term term, TransitionSystem ts) throws ParseException {
        Term treatedTerm = term.clone();
        if (term.isStructure()) {
            Plan plan = (Plan) term;
            treatedTerm = ASSyntax.parseTerm(plan.toASString());
        } else {
            String termString = term.toString();
            if (termString.startsWith("\"") && termString.endsWith("\"")) {
                termString = termString.substring(1, termString.length() - 1);
            }
            if (termString.startsWith("@p__")) {
                final String string = termString.replace("@", "");
                PlanLibrary pl = ts.getAg().getPL();
                List<Plan> plans = pl.getPlans();
                if (plans != null && !plans.isEmpty()) {
                    Plan plan = plans.stream().filter(plan1 -> plan1.getLabel().getFunctor().equals(string))
                            .findFirst().orElse(null);
                    if (plan != null) {
                        treatedTerm = convertPlanToTerm(plan);
                    }
                }
            } else {
                termString = treatPlanStringFormat(termString);
                treatedTerm = ASSyntax.parseTerm(termString);
            }
        }


        return treatedTerm;
    }

    public static Term treatContentTermAsTrigger(Term term) throws ParseException {
        Term treatedTerm = term.clone();

        if (treatedTerm.isStructure()) {
            Trigger trigger = (Trigger) treatedTerm;
            treatedTerm = ASSyntax.parseTerm(trigger.toString());
        } else {
            String termString = treatedTerm.toString();
            if (termString.startsWith("\"") && termString.endsWith("\"")) {
                termString = termString.substring(1, termString.length() - 1);
            }
            if (!termString.startsWith("{") && !termString.endsWith("}")) {
                termString = "{" + termString + "}";
            }
            treatedTerm = ASSyntax.parseTerm(termString);
        }


        return treatedTerm;
    }

    public static Term treatContentTermForUntellHow(Term term, TransitionSystem ts, String myIdentification) throws ParseException {
        Term treatedTerm = term.clone();
        if (term.isStructure()) {
            Plan plan = (Plan) term;
            Pred pred = new Pred(plan.getLabel().getFunctor());
            Pred pred1 = new Pred("source");
            pred1.addTerm(new Atom("\"" + myIdentification+ "\""));
            pred.addAnnot(pred1);
            plan.setLabel(pred);
            String planStringTest = treatPlanStringFormat(plan.toASString());
            treatedTerm = ASSyntax.parseTerm(planStringTest);
        } else {
            String termString = term.toString();
            if (termString.startsWith("\"") && termString.endsWith("\"")) {
                termString = termString.substring(1, termString.length() - 1);
            }
            if (termString.startsWith("@p__")) {
                final String string = termString.replace("@", "");
                PlanLibrary pl = ts.getAg().getPL();
                List<Plan> plans = pl.getPlans();
                if (plans != null && !plans.isEmpty()) {
                    Plan plan = plans.stream().filter(plan1 -> plan1.getLabel().getFunctor().equals(string))
                            .findFirst().orElse(null);
                    if (plan != null) {
                        Plan clone = (Plan) plan.clone();
                        Pred pred = new Pred(plan.getLabel().getFunctor());
                        Pred pred1 = new Pred("source");
                        pred1.addTerm(new Atom("\"" + myIdentification+ "\""));
                        pred.addAnnot(pred1);
                        clone.setLabel(pred);
                        String planStringTest = treatPlanStringFormat(clone.toASString());
                        treatedTerm = ASSyntax.parseTerm(planStringTest);
                    }
                }
            } else {
                termString = treatPlanStringFormat(termString);
                Term termOfString = ASSyntax.parseTerm(termString);
                Plan plan = (Plan) termOfString;
                Pred pred = new Pred("p__1");
                Pred pred1 = new Pred("source");
                pred1.addTerm(new Atom("\"" + myIdentification+ "\""));
                pred.addAnnot(pred1);
                plan.setLabel(pred);
                String planStringTest = treatPlanStringFormat(plan.toASString());
                treatedTerm = ASSyntax.parseTerm(planStringTest);
            }
        }


        return treatedTerm;
    }

    public static String treatString(String value) {
        return value.trim().replace("\"", "");
    }

    public static Message getJasonMessage(Object message) {
        // TODO: Verificar como vamos fazer o tratamento das exceções.
        if (message instanceof Message) {
            return (Message) message;
        } else {
            try {
                Message jasonMessage = Message.parseMsg(message.toString());
                //msgId+irt+","+sender+","+ilForce+","+receiver+","+propCont
                if (jasonMessage.getMsgId() == null && jasonMessage.getSender() == null
                        && jasonMessage.getIlForce() == null && jasonMessage.getReceiver() == null
                        && jasonMessage.getPropCont() == null) {
                    return null;
                } else {
                    return jasonMessage;
                }
            } catch (NullPointerException|ParseException e) {
                return null;
            }
        }
    }

    public static AgentTransferRequestMessageDto getAgentTransferRequestMessage(Object message) {
        // TODO: Fazer um metodo só com o generics.
        if (message instanceof AgentTransferRequestMessageDto) {
            return (AgentTransferRequestMessageDto) message;
        } else if (message instanceof String) {
            String messageInString = (String) message;
            Object agentTransferRequestMessageDtoObject = null;
            try {
                agentTransferRequestMessageDtoObject = HermesUtils.deserializeMessage(messageInString);
            } catch (Exception e) {
                return null;
            }
            if (agentTransferRequestMessageDtoObject != null &&
                    agentTransferRequestMessageDtoObject instanceof AgentTransferRequestMessageDto) {
                return (AgentTransferRequestMessageDto) agentTransferRequestMessageDtoObject;
            }
        }

        return null;
    }

    public static AgentTransferResponseMessageDto getAgentTransferResponseMessage(Object message) {
        if (message instanceof AgentTransferResponseMessageDto) {
            return (AgentTransferResponseMessageDto) message;
        } else if (message instanceof String) {
            String messageInString = (String) message;
            Object agentTransferResponseMessageDtoObject = null;
            try {
                agentTransferResponseMessageDtoObject = HermesUtils.deserializeMessage(messageInString);
            } catch (Exception e) {
                return null;
            }
            if (agentTransferResponseMessageDtoObject != null &&
                    agentTransferResponseMessageDtoObject instanceof AgentTransferResponseMessageDto) {
                return (AgentTransferResponseMessageDto) agentTransferResponseMessageDtoObject;
            }
        }

        return null;
    }

    public static AgentTransferContentMessageDto getAgentTransferContentMessage(Object message) {
        if (message instanceof AgentTransferContentMessageDto) {
            return (AgentTransferContentMessageDto) message;
        } else if (message instanceof String) {
            String messageInString = (String) message;
            Object agentTransferContentMessageDtoObject = null;
            try {
                agentTransferContentMessageDtoObject = HermesUtils.deserializeMessage(messageInString);
            } catch (Exception e) {
                return null;
            }
            if (agentTransferContentMessageDtoObject != null &&
                    agentTransferContentMessageDtoObject instanceof AgentTransferContentMessageDto) {
                return (AgentTransferContentMessageDto) agentTransferContentMessageDtoObject;
            }
        }

        return null;
    }

    public static AgentTransferConfirmationMessageDto getAgentTransferConfirmationMessage(Object message) {
        if (message instanceof AgentTransferConfirmationMessageDto) {
            return (AgentTransferConfirmationMessageDto) message;
        } else if (message instanceof String) {
            String messageInString = (String) message;
            Object agentTransferConfirmationMessageDtoObject = null;
            try {
                agentTransferConfirmationMessageDtoObject = HermesUtils.deserializeMessage(messageInString);
            } catch (Exception e) {
                return null;
            }
            if (agentTransferConfirmationMessageDtoObject != null &&
                    agentTransferConfirmationMessageDtoObject instanceof AgentTransferConfirmationMessageDto) {
                return (AgentTransferConfirmationMessageDto) agentTransferConfirmationMessageDtoObject;
            }
        }

        return null;
    }

    public static String serializeMessage(Object message) {
        // TODO: Verificar como vamos fazer o tratamento das exceções.
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gzipOut = null;
        ObjectOutputStream out = null;
        String serializedMessage = null;
        try {
            bos = new ByteArrayOutputStream();
            gzipOut = new GZIPOutputStream(bos);
            out = new ObjectOutputStream(gzipOut);
            out.writeObject(message);
            out.close();

            byte[] compressedBytes = bos.toByteArray();
            serializedMessage = Base64.getEncoder().encodeToString(compressedBytes);
        } catch (IOException e) {
            BioInspiredUtils.log(Level.SEVERE,"Erro ao deserializar o Objeto: " + message.toString());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    BioInspiredUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + out.toString());
                }
            }
            if (gzipOut != null) {
                try {
                    gzipOut.close();
                } catch (IOException e) {
                    BioInspiredUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + gzipOut.toString());
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    BioInspiredUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + bos.toString());
                }
            }
        }
        return serializedMessage;
    }

    public static Object deserializeMessage(String message) {
        // TODO: Verificar como vai fazer o tratamento das exceções.
        ByteArrayInputStream bis = null;
        GZIPInputStream gzipIn = null;
        ObjectInputStream in = null;
        Object deserializedObject = null;
        try {
            byte[] compressedBytes = Base64.getDecoder().decode(message);
            bis = new ByteArrayInputStream(compressedBytes);
            gzipIn = new GZIPInputStream(bis);
            in = new ObjectInputStream(gzipIn);
            deserializedObject = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            BioInspiredUtils.log(Level.SEVERE,"Erro ao deserializar o Objeto: " + message);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    BioInspiredUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + in.toString());
                }
            }
            if (gzipIn != null) {
                try {
                    gzipIn.close();
                } catch (IOException e) {
                    BioInspiredUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + gzipIn.toString());
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    BioInspiredUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + bis.toString());
                }
            }
        }
        return deserializedObject;
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception | Error e) {
            //ignore
        }
    }

}
