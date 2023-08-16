package jason.stdlib;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.hermes.utils.HermesUtils;
import jason.hermes.middlewares.CommunicationMiddleware;

public class disconnect extends DefaultInternalAction {

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

        String configurationIdentifier = HermesUtils.getParameterInString(args[0]);
        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        CommunicationMiddleware communicationMiddleware = hermes.getCommunicationMiddleware(configurationIdentifier);
        communicationMiddleware.disconnect();

        return true;
    }

}
