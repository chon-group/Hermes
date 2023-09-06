package jason.stdlib;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;
import jason.hermes.config.ContextNetConfiguration;
import jason.hermes.sec.CommunicationSecurity;
import jason.hermes.sec.NoSecurity;

import java.util.logging.Level;

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
        String configurationIdentifier = HermesUtils.getParameterInString(args[0]);
        String gatewayIP = HermesUtils.getParameterInString(args[1]);
        int gatewayPort = Integer.parseInt(HermesUtils.getParameterInString(args[2]));
        String myUUID = HermesUtils.getParameterInString(args[3]);
        CommunicationSecurity securityImplementation = new NoSecurity();
        if (args.length == this.getMaxArgs()) {
            String securityParam = HermesUtils.getParameterInString(args[4]);
            securityImplementation = HermesUtils.getSecurityImplementation(securityParam);
        }

        ContextNetConfiguration contextNetConfiguration = new ContextNetConfiguration(configurationIdentifier,
                securityImplementation, gatewayIP, gatewayPort, myUUID);

        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        hermes.addConnectionConfiguration(contextNetConfiguration);

        BeliefUtils.addBelief(contextNetConfiguration.toBelief(), hermes.getTS().getAg());

        return true;
    }
}
