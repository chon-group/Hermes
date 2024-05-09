package jason.hermes.utils;

import jason.asSemantics.Message;
import jason.asSyntax.parser.ParseException;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferConfirmationMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferContentMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferResponseMessageDto;

import java.io.*;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class MessageUtils {

    private static final Logger LOGGER = Logger.getLogger("HERMES_MESSAGE_UTILS");

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
            } catch (NullPointerException | ParseException e) {
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
                agentTransferRequestMessageDtoObject = MessageUtils.deserializeMessage(messageInString);
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
                agentTransferResponseMessageDtoObject = MessageUtils.deserializeMessage(messageInString);
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
                agentTransferContentMessageDtoObject = MessageUtils.deserializeMessage(messageInString);
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
                agentTransferConfirmationMessageDtoObject = MessageUtils.deserializeMessage(messageInString);
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
            MessageUtils.log(Level.SEVERE,"Erro ao deserializar o Objeto: " + message.toString());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    MessageUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + out.toString());
                }
            }
            if (gzipOut != null) {
                try {
                    gzipOut.close();
                } catch (IOException e) {
                    MessageUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + gzipOut.toString());
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    MessageUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + bos.toString());
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
            MessageUtils.log(Level.SEVERE,"Erro ao deserializar o Objeto: " + message);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    MessageUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + in.toString());
                }
            }
            if (gzipIn != null) {
                try {
                    gzipIn.close();
                } catch (IOException e) {
                    MessageUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + gzipIn.toString());
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    MessageUtils.log(Level.SEVERE,"Erro ao fechar a instancia: " + bis.toString());
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
