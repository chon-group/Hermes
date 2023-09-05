package jason.stdlib;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;
import jason.hermes.bioinspired.DominanceDegrees;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.HermesUtils;

public class dominanceDegree extends DefaultInternalAction {

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args);
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        this.checkArguments(args);

        String dominanceDegree = HermesUtils.getParameterInString(args[0]);
        DominanceDegrees dominanceDegrees = DominanceDegrees.get(dominanceDegree);
        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());
        hermes.getBioinspiredData().setMyDominanceDegree(dominanceDegrees);

        BeliefUtils.replaceBelief(BeliefUtils.MY_DOMINANCE_DEGREE_PREFIX, BeliefUtils.MY_DOMINANCE_DEGREE_VALUE, BeliefBase.ASelf,
                dominanceDegrees.name(), hermes.getTS().getAg());

        return true;
    }

}
