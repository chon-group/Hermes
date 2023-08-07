package jason.hermes;

import jason.Hermes;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Message;
import jason.asSyntax.Term;
import jason.hermes.sec.CommunicationSecurity;
import jason.hermes.sec.NoSecurity;
import jason.hermes.sec.SecurityImplementations;

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

}
