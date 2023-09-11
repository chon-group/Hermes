package jason.hermes.utils;

import jason.RevisionFailedException;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class BeliefUtils {

    public static final String MY_MAS_BELIEF_PREFIX = "myMAS";

    public static final String MY_DOMINANCE_DEGREE_PREFIX = "myDominanceDegree";

    public static final String VALUE_REPLACEMENT = "#";

    private static final String BELIEF_VALUE = "(\""+VALUE_REPLACEMENT+"\")";

    public static String MY_MAS_BELIEF_VALUE = MY_MAS_BELIEF_PREFIX + BELIEF_VALUE;

    public static String MY_DOMINANCE_DEGREE_VALUE = MY_DOMINANCE_DEGREE_PREFIX + BELIEF_VALUE;

    public static final String BELIEF_SEPARATOR = ",";

    public static List<String> getBeliefsInStringByStartWith(BeliefBase beliefBase, String startAt) {
        List<String> beliefs = new ArrayList<>();
        Iterator<Literal> beliefsIterator = beliefBase.iterator();
        while (beliefsIterator.hasNext()) {
            Literal literal = beliefsIterator.next();
            String belief = literal.toString();
            if (belief.startsWith(startAt)){
                beliefs.add(belief);
            }
        }
        return beliefs;
    }

    public static List<Literal> getBeliefsByStartWith(BeliefBase beliefBase, String startAt) {
        List<Literal> beliefs = new ArrayList<>();
        Iterator<Literal> beliefsIterator = beliefBase.iterator();
        while (beliefsIterator.hasNext()) {
            Literal literal = beliefsIterator.next();
            String belief = literal.toString();
            if (belief.startsWith(startAt)){
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
        belief.addSource(beliefSource);

        return belief;
    }

    public static void addBelief(Literal belief, Agent agent) {
        try {
            agent.addBel(belief);
        } catch (RevisionFailedException e) {
            BioInspiredUtils.log(Level.SEVERE,
                    "Error: Tt was not possible to add the belief: '" + belief + "'.\nCause: " + e);
        }
    }
    public static void addBelief(String beliefValueConstantName, Term beliefSource, String value, Agent agent) {
        Literal belief = getBelief(beliefValueConstantName, beliefSource, value);
        addBelief(belief, agent);
    }

    public static void replaceAllBeliefBySource(String beliefConstantPrefix, String beliefValueConstantName, Term beliefSource,
                                                String value, Agent agent) {
        List<String> beliefWithPrefixList = BeliefUtils.getBeliefsInStringByStartWith(agent.getBB(), beliefConstantPrefix);
        String source = HermesUtils.getParameterInString(beliefSource);

        List<String> beliefValueList = BeliefUtils.getBeliefValue(beliefWithPrefixList, source);

        if (!beliefValueList.isEmpty() && !beliefValueList.contains(value)) {
            for (String beliefValueString : beliefWithPrefixList) {
                Literal beliefValueLiteral = Literal.parseLiteral(beliefValueString);
                try {
                    agent.delBel(beliefValueLiteral);
                } catch (RevisionFailedException e) {
                    BioInspiredUtils.log(Level.SEVERE,
                            "Error: Tt was not possible to remove the belief: '" + beliefValueString + "'.\nCause: " + e);
                }
            }
        }

        addBelief(beliefValueConstantName, beliefSource, value, agent);
    }

    public static void replaceAllBelief(String beliefConstantPrefix, String beliefValueConstantName, Term beliefSource,
                                        String value, Agent agent) {
        List<String> beliefWithPrefixList = BeliefUtils.getBeliefsInStringByStartWith(agent.getBB(), beliefConstantPrefix);

        if (!beliefWithPrefixList.isEmpty()) {
            for (String beliefValueString : beliefWithPrefixList) {
                if (!beliefValueString.contains(value)) {
                    Literal beliefValueLiteral = Literal.parseLiteral(beliefValueString);
                    try {
                        agent.delBel(beliefValueLiteral);
                    } catch (RevisionFailedException e) {
                        BioInspiredUtils.log(Level.SEVERE,
                                "Error: Tt was not possible to remove the belief: '" + beliefValueString + "'.\nCause: " + e);
                    }
                }
            }
        }

        addBelief(beliefValueConstantName, beliefSource, value, agent);
    }

    public static void replaceAllBelief(Literal belief, Term source, Agent agent) {
        String beliefString = belief.toString();
        String sourceString = HermesUtils.getParameterInString(source);
        int initialIndex = beliefString.indexOf("(");
        String beliefPrefix = "";
        if(initialIndex != 1) {
            beliefPrefix = beliefString.substring(0, initialIndex);
        }
        if (!beliefPrefix.isEmpty()) {
            List<String> beliefWithPrefixList = BeliefUtils.getBeliefsInStringByStartWith(agent.getBB(), beliefPrefix);

            List<String> beliefValueList = BeliefUtils.getBeliefValue(beliefWithPrefixList, sourceString);

            if (!beliefValueList.isEmpty()) {
                for (String beliefValueString : beliefWithPrefixList) {
                    if (!beliefValueString.equals(beliefString)) {
                        Literal beliefValueLiteral = Literal.parseLiteral(beliefValueString);
                        try {
                            agent.delBel(beliefValueLiteral);
                        } catch (RevisionFailedException e) {
                            BioInspiredUtils.log(Level.SEVERE,
                                    "Error: Tt was not possible to remove the belief: '" + beliefValueString + "'.\nCause: " + e);
                        }
                    }
                }
            }

            if (!beliefWithPrefixList.contains(beliefString)) {
                addBelief(belief, agent);
            }
        }

    }

    public static void addBeliefIfAbsent(String beliefConstantPrefix, String beliefValueConstantName, Term beliefSource,
                                         String value, Agent agent) {
        List<String> beliefWithPrefixList = BeliefUtils.getBeliefsInStringByStartWith(agent.getBB(), beliefConstantPrefix);
        if (beliefWithPrefixList.isEmpty()) {
            addBelief(beliefValueConstantName, beliefSource, value, agent);
        }

    }

    public static void replaceBelief(Literal newBelief, Literal oldBelief, Agent agent) {
        try {
            agent.delBel(oldBelief);
        } catch (RevisionFailedException e) {
            BioInspiredUtils.log(Level.SEVERE,
                    "Error: Tt was not possible to remove the belief: '" + oldBelief + "'.\nCause: " + e);
        }

        addBelief(newBelief, agent);
    }

    public static String getPrefix(Class aClass) {
        String className = aClass.getSimpleName();
        char firstChar = Character.toLowerCase(className.charAt(0));
        return firstChar + className.substring(1);
    }

}
