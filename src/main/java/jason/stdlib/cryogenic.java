package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.hermes.bioinspired.BioinspiredProcessor;

import java.io.File;

public class cryogenic extends DefaultInternalAction {

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args);
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        this.checkArguments(args);

        File masFileCriogenated = BioinspiredProcessor.cryogenate(ts.getAg().getASLSrc());

        return masFileCriogenated != null && masFileCriogenated.exists();
    }

}
