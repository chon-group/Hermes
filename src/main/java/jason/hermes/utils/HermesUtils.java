package jason.hermes.utils;

import jason.Hermes;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Message;
import jason.asSyntax.Term;
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
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HermesUtils {

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

    public static CommunicationSecurity getSecurityImplementation(String securityClassName) {
        // TODO: Verificar o que deve ser feito se não for possivel identificar a implementação de segurança passada.
        SecurityImplementations[] securityImplementations = SecurityImplementations.values();
        for (SecurityImplementations securityImplementation : securityImplementations) {
            if (securityImplementation.getSecurityClassName().equalsIgnoreCase(securityClassName)) {
                return securityImplementation.getSecurityImplementation();
            }
        }
        System.out.println("Was not possible to identify the security implementation with the name \""
                + securityClassName + "\"");
        return new NoSecurity();
    }

    public static Message formatMessage(String sender, String receiver, String force, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setIlForce(force);
        message.setPropCont(content);

        return message;
    }

    public static String getParameterInString(Term term) {
        return term.toString().trim().replace("\"", "");
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
            BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao deserializar o Objeto: " + message.toString());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao fechar a instancia: " + out.toString());
                }
            }
            if (gzipOut != null) {
                try {
                    gzipOut.close();
                } catch (IOException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao fechar a instancia: " + gzipOut.toString());
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao fechar a instancia: " + bos.toString());
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
            BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao deserializar o Objeto: " + message);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao fechar a instancia: " + in.toString());
                }
            }
            if (gzipIn != null) {
                try {
                    gzipIn.close();
                } catch (IOException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao fechar a instancia: " + gzipIn.toString());
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE,"Erro ao fechar a instancia: " + bis.toString());
                }
            }
        }
        return deserializedObject;
    }
}