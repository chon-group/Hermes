package group.chon.agent.hermes.jasonStdLib;

import group.chon.agent.hermes.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.hermes.capabilities.manageConnections.configuration.ContextNetConfiguration;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.HermesUtils;

public class configureContextNetConnection extends DefaultInternalAction {

    @Override
    public int getMinArgs() {
        return 4;
    }

    @Override
    public int getMaxArgs() {
        return 5;
    }

    @Override
    protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args);
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        this.checkArguments(args);
        ContextNetConfiguration contextNetConfiguration = new ContextNetConfiguration();
        contextNetConfiguration = contextNetConfiguration.get(args, this.getMinArgs(), this.getMaxArgs());

        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        hermes.addConnectionConfiguration(contextNetConfiguration);

        return BeliefUtils.addBelief(contextNetConfiguration.toBelief(), hermes.getTS().getAg());
    }
}
