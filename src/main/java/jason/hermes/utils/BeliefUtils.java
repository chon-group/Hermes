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

    public static List<String> getBeliefByStartWith(BeliefBase beliefBase, String startAt) {
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

    public static void replaceBelief(String beliefConstantPrefix, String beliefValueConstantName,Term beliefSource, String value, Agent agent) {
        List<String> beliefWithPrefixList = BeliefUtils.getBeliefByStartWith(agent.getBB(), beliefConstantPrefix);
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
    
}
