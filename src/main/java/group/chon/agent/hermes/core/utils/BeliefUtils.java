package jason.hermes.utils;

import group.chon.agent.hermes.Hermes;
import jason.RevisionFailedException;
import jason.asSemantics.Agent;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.asSyntax.parser.ParseException;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BeliefUtils {

    private static final Logger LOGGER = Logger.getLogger("HERMES_BELIEF_UTILS");

    public static final String MY_MAS_BELIEF_PREFIX = "myMAS";

    public static final String MY_TROPHIC_LEVEL_PREFIX = "myTrophicLevel";

    public static final String VALUE_REPLACEMENT = "#";

    private static final String BELIEF_VALUE = "(\""+VALUE_REPLACEMENT+"\")";

    public static String MY_MAS_BELIEF_VALUE = MY_MAS_BELIEF_PREFIX + BELIEF_VALUE;

    public static String MY_TROPHIC_LEVEL_VALUE = MY_TROPHIC_LEVEL_PREFIX + BELIEF_VALUE;

    public static final String BELIEF_SEPARATOR = ",";

    public static final String HERMES_NAMESPACE = Hermes.class.getSimpleName();

    public static final String NAMESPACE_SEPARATOR = "::";

    public static List<String> getBeliefsInStringByFunction(BeliefBase beliefBase, String function) {
        List<String> beliefs = new ArrayList<>();
        Iterator<Literal> beliefsIterator = beliefBase.iterator();
        while (beliefsIterator.hasNext()) {
            Literal literal = beliefsIterator.next();
            if (literal.getFunctor().equals(function)){
                beliefs.add(literal.toString());
            }
        }
        return beliefs;
    }

    public static List<Literal> getBeliefsByFunction(BeliefBase beliefBase, String function) {
        List<Literal> beliefs = new ArrayList<>();
        Iterator<Literal> beliefsIterator = beliefBase.iterator();
        while (beliefsIterator.hasNext()) {
            Literal literal = beliefsIterator.next();
            if (literal.getFunctor().equals(function)){
                beliefs.add(literal);
            }
        }
        return beliefs;
    }

    public static List<String> getBeliefValue(List<String> beliefList, String source) {
        List<String> beliefValuesList = new ArrayList<>();
        if (beliefList != null && !beliefList.isEmpty()) {
            for (String belief : beliefList) {
                String correctBelief = "";
                if (belief.contains(source)) {
                    correctBelief = belief;
                }
                if (!correctBelief.isEmpty()) {
                    int initialIndex = correctBelief.indexOf("(");
                    int finalIndex = correctBelief.indexOf(")");
                    if (initialIndex != -1 && finalIndex != -1) {
                        correctBelief = correctBelief.substring(initialIndex + 1, finalIndex);
                        beliefValuesList.add(HermesUtils.treatString(correctBelief));
                    }
                }
            }
        }
        return beliefValuesList;
    }

    public static Literal getBelief(String beliefValueConstantName, Term beliefSource, String value) {
        Literal belief = Literal.parseLiteral(beliefValueConstantName.replace(
                BeliefUtils.VALUE_REPLACEMENT,
                value));
        belief = ASSyntax.createLiteral(new Atom(HERMES_NAMESPACE), belief.getFunctor(), belief.getTermsArray());
        belief.addSource(beliefSource);

        return Literal.parseLiteral(belief.toString());
    }

    public static Literal parseLiteralWithNamespace(String belief) {
        try {
            return ASSyntax.parseLiteral(belief);
        } catch (ParseException e) {
            String errorMessage = "Error: When parsing the belief ('" + belief + "') from string to literal.";
            BeliefUtils.log(Level.SEVERE, errorMessage);
        }

        return Literal.parseLiteral(belief);
    }

    public static boolean addBelief(Literal belief, Agent agent) {
        try {
            agent.addBel(belief);
            return true;
        } catch (RevisionFailedException e) {
            BeliefUtils.log(Level.SEVERE,
                    "Error: Tt was not possible to add the belief: '" + belief + "'.\nCause: " + e);
            return false;
        }
    }
    public static boolean addBelief(String beliefValueConstantName, Term beliefSource, String value, Agent agent) {
        Literal belief = getBelief(beliefValueConstantName, beliefSource, value);
        return addBelief(belief, agent);
    }

    public static void replaceAllBeliefBySource(String beliefConstantPrefix, String beliefValueConstantName, Term beliefSource,
                                                String value, Agent agent) {
        List<String> beliefWithPrefixList = BeliefUtils.getBeliefsInStringByFunction(agent.getBB(), beliefConstantPrefix);
        String source = HermesUtils.getTermInString(beliefSource);

        List<String> beliefValueList = BeliefUtils.getBeliefValue(beliefWithPrefixList, source);

        if (!beliefValueList.isEmpty() && !beliefValueList.contains(value)) {
            for (String beliefValueString : beliefWithPrefixList) {
                Literal beliefValueLiteral = BeliefUtils.parseLiteralWithNamespace(beliefValueString);
                try {
                    agent.delBel(beliefValueLiteral);
                } catch (RevisionFailedException e) {
                    BeliefUtils.log(Level.SEVERE,
                            "Error: Tt was not possible to remove the belief: '" + beliefValueString + "'.\nCause: " + e);
                }
            }
        }

        addBelief(beliefValueConstantName, beliefSource, value, agent);
    }

    public static boolean replaceAllBelief(String beliefConstantPrefix, String beliefValueConstantName, Term beliefSource,
                                        String value, Agent agent) {
        List<String> beliefWithPrefixList = BeliefUtils.getBeliefsInStringByFunction(agent.getBB(), beliefConstantPrefix);

        if (!beliefWithPrefixList.isEmpty()) {
            for (String beliefValueString : beliefWithPrefixList) {
                if (!beliefValueString.contains(value)) {
                    Literal beliefValueLiteral = BeliefUtils.parseLiteralWithNamespace(beliefValueString);
                    try {
                        agent.delBel(beliefValueLiteral);
                    } catch (RevisionFailedException e) {
                        BeliefUtils.log(Level.SEVERE,
                                "Error: Tt was not possible to remove the belief: '" + beliefValueString + "'.\nCause: " + e);
                    }
                }
            }
        }

        return addBelief(beliefValueConstantName, beliefSource, value, agent);
    }

    public static void addBeliefIfAbsent(String beliefConstantPrefix, String beliefValueConstantName, Term beliefSource,
                                         String value, Agent agent) {
        List<String> beliefWithPrefixList = BeliefUtils.getBeliefsInStringByFunction(agent.getBB(), beliefConstantPrefix);
        if (beliefWithPrefixList.isEmpty()) {
            addBelief(beliefValueConstantName, beliefSource, value, agent);
        }

    }

    public static void replaceBelief(Literal newBelief, Literal oldBelief, Agent agent) {
        try {
            agent.delBel(oldBelief);
        } catch (RevisionFailedException e) {
            BeliefUtils.log(Level.SEVERE,
                    "Error: Tt was not possible to remove the belief: '" + oldBelief + "'.\nCause: " + e);
        }

        addBelief(newBelief, agent);
    }

    public static String getPrefix(Class aClass) {
        String className = aClass.getSimpleName();
        char firstChar = Character.toLowerCase(className.charAt(0));
        return firstChar + className.substring(1);
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception | Error e) {
            //ignore
        }
    }

}
