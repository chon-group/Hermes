package group.chon.agent.hermes.jasonStdLib;

import group.chon.agent.hermes.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;
import group.chon.agent.hermes.core.capabilities.manageTrophicLevel.TrophicLevelEnum;
import group.chon.agent.hermes.core.utils.ArgsUtils;
import group.chon.agent.hermes.core.utils.BeliefUtils;
import group.chon.agent.hermes.core.utils.HermesUtils;

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

        String trophicLevelParam = ArgsUtils.getInString(args[0]);
        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        TrophicLevelEnum trophicLevelEnum = TrophicLevelEnum.get(trophicLevelParam);
        hermes.getBioinspiredData().setMyTrophicLevel(trophicLevelEnum);

        return BeliefUtils.replaceAllBelief(BeliefUtils.MY_TROPHIC_LEVEL_PREFIX, BeliefUtils.MY_TROPHIC_LEVEL_VALUE,
                BeliefBase.ASelf, trophicLevelEnum.name(), hermes.getTS().getAg());
    }

}
