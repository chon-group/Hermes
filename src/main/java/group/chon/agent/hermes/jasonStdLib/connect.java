package group.chon.agent.hermes.jasonStdLib;

import group.chon.agent.hermes.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.utils.ArgsUtils;
import jason.hermes.utils.HermesUtils;

import java.util.logging.Level;

public class connect extends DefaultInternalAction {

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

    private void checkArguments(Term[] args, TransitionSystem ts) throws JasonException {
        this.checkArguments(args);

        String configurationIdentifier = ArgsUtils.getInString(args[0]);
        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        if (!HermesUtils.verifyConnectionIdentifier(configurationIdentifier, hermes)) {
            String msgError = "Error: Does not exists an connection identifier ('" + configurationIdentifier + "') in the Hermes connections configured!";
            HermesUtils.log(Level.SEVERE, msgError);
            throw JasonException.createWrongArgument(this, msgError);
        }

    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        this.checkArguments(args, ts);

        String configurationIdentifier = ArgsUtils.getInString(args[0]);
        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        CommunicationMiddleware communicationMiddleware = hermes.getCommunicationMiddleware(configurationIdentifier);
        communicationMiddleware.connect();

        return communicationMiddleware.isConnected();
    }
}
