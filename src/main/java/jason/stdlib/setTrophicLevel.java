package jason.stdlib;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;
import jason.hermes.bioinspired.TrophicLevelEnum;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.HermesUtils;

public class setTrophicLevel extends DefaultInternalAction {

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

        String trophicLevelParam = HermesUtils.getParameterInString(args[0]);
        TrophicLevelEnum trophicLevelEnum = TrophicLevelEnum.get(trophicLevelParam);
        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());
        hermes.getBioinspiredData().setMyTrophicLevel(trophicLevelEnum);

        BeliefUtils.replaceAllBelief(BeliefUtils.MY_TROPHIC_LEVEL_PREFIX, BeliefUtils.MY_TROPHIC_LEVEL_VALUE,
                BeliefBase.ASelf, trophicLevelEnum.name(), hermes.getTS().getAg());

        return true;
    }

}
