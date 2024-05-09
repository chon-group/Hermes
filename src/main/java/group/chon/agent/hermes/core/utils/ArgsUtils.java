package group.chon.agent.hermes.core.utils;

import jason.JasonException;
import jason.asSemantics.InternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.*;
import jason.asSyntax.parser.ParseException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ArgsUtils {

    private static final Logger LOGGER = Logger.getLogger("HERMES_ARGS_UTILS");

    public static String getInString(Term arg) {
        return arg.toString().trim().replace("\"", "");
    }

    public static ListTerm getInListTerm(Term arg, InternalAction internalAction) throws JasonException {
        ListTerm listTerm;
        try {
            listTerm = ASSyntax.parseList(arg.toString());
        } catch (ParseException e) {
            String msgError = "Error: When converting the list received by argument ('" + arg.toString()
                    + "') to the internal action ('" + internalAction.getClass().getName() + "').";
            ArgsUtils.log(Level.SEVERE, msgError);
            throw JasonException.createWrongArgument(internalAction, msgError);
        }
        return listTerm;
    }

    public static ListTerm getInListTerm(Term term) throws JasonException {
        ListTerm listTerm;
        try {
            listTerm = ASSyntax.parseList(term.toString());
        } catch (ParseException e) {
            String msgError = "Error: When converting the list ('" + term.toString() + "').";
            ArgsUtils.log(Level.SEVERE, msgError);
            throw new JasonException(msgError, JasonException.WRONG_ARGS);
        }
        return listTerm;
    }

    public static Term getAsPlanTerm(Term term, TransitionSystem ts) throws ParseException {
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
                        treatedTerm = HermesUtils.convertPlanToTerm(plan);
                    }
                }
            } else {
                termString = HermesUtils.treatPlanStringFormat(termString);
                treatedTerm = ASSyntax.parseTerm(termString);
            }
        }


        return treatedTerm;
    }

    public static Term getAsTriggerTerm(Term term) throws ParseException {
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

    public static Term getAsPlanTermForUntellHow(Term term, TransitionSystem ts, String myIdentification) throws ParseException {
        Term treatedTerm = term.clone();
        if (term.isStructure()) {
            Plan plan = (Plan) term;
            Pred pred = new Pred(plan.getLabel().getFunctor());
            Pred pred1 = new Pred("source");
            pred1.addTerm(new Atom("\"" + myIdentification+ "\""));
            pred.addAnnot(pred1);
            plan.setLabel(pred);
            String planStringTest = HermesUtils.treatPlanStringFormat(plan.toASString());
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
                        String planStringTest = HermesUtils.treatPlanStringFormat(clone.toASString());
                        treatedTerm = ASSyntax.parseTerm(planStringTest);
                    }
                }
            } else {
                termString = HermesUtils.treatPlanStringFormat(termString);
                Term termOfString = ASSyntax.parseTerm(termString);
                Plan plan = (Plan) termOfString;
                Pred pred = new Pred("p__1");
                Pred pred1 = new Pred("source");
                pred1.addTerm(new Atom("\"" + myIdentification+ "\""));
                pred.addAnnot(pred1);
                plan.setLabel(pred);
                String planStringTest = HermesUtils.treatPlanStringFormat(plan.toASString());
                treatedTerm = ASSyntax.parseTerm(planStringTest);
            }
        }


        return treatedTerm;
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception | Error e) {
            //ignore
        }
    }

}
