package jason.hermes.utils;

import jason.Hermes;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.InternalAction;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Plan;
import jason.asSyntax.Term;
import jason.asSyntax.parser.ParseException;
import jason.hermes.capabilities.manageConnections.sec.CommunicationSecurity;
import jason.hermes.capabilities.manageConnections.sec.CommunicationSecurityEnum;
import jason.hermes.capabilities.manageConnections.sec.NoSecurity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HermesUtils {

    private static final Logger LOGGER = Logger.getLogger("HERMES_UTILS");

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

    public static boolean verifyAgentExist(String agentName) {
        List<String> allAgentsName = BioInspiredUtils.getAllAgentsName();
        return  allAgentsName.contains(agentName);
    }

    public static boolean verifyConnectionIdentifier(String connectionIdentifier, Hermes hermes) {
        if (connectionIdentifier == null || connectionIdentifier.trim().isEmpty()) {
            return false;
        }

        return hermes.getCommunicationMiddlewareHashMap().containsKey(connectionIdentifier);
    }

    public static List<String> getAgentNamesInList(ListTerm listTerm) {
        List<String> agentNamesInList = new ArrayList<>();
        for (Term term : listTerm) {
            agentNamesInList.add(HermesUtils.getTermInString(term));
        }

        return agentNamesInList;
    }

    public static void verifyAgentNameParameterList(Term term, ListTerm agentNameList, InternalAction internalAction)
            throws JasonException {
        for (Term thirdArgTerm : agentNameList) {
            String thirdArg = HermesUtils.getTermInString(thirdArgTerm);
            if (!HermesUtils.verifyAgentExist(thirdArg)) {
                String msgError = "Error: The argument is a list of agent names ('" + term.toString()
                        + "'), and agent ('" + thirdArg + "') does not exist in MAS.";
                HermesUtils.log(Level.SEVERE, msgError);
                throw JasonException.createWrongArgument(internalAction, msgError);
            }
        }
    }

    public static CommunicationSecurity getSecurityImplementation(String securityClassName) {
        // TODO: Verificar o que deve ser feito se não for possivel identificar a implementação de segurança passada.
        CommunicationSecurityEnum[] communicationSecurityEnumValuesArray = CommunicationSecurityEnum.values();
        for (CommunicationSecurityEnum communicationSecurityEnum : communicationSecurityEnumValuesArray) {
            if (communicationSecurityEnum.getSecurityClassName().equalsIgnoreCase(securityClassName)
                    || communicationSecurityEnum.getCommunicationSecurity().getClass().getSimpleName().equalsIgnoreCase(securityClassName)) {
                return communicationSecurityEnum.getCommunicationSecurity();
            }
        }
        String warningMessage = "Was not possible to identify the security implementation with the name ('"
                + securityClassName + "')";
        HermesUtils.log(Level.WARNING, warningMessage);
        return new NoSecurity();
    }

    public static String getTermInString(Term term) {
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

    public static String treatString(String value) {
        return value.trim().replace("\"", "");
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception | Error e) {
            //ignore
        }
    }

}
