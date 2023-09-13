package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.hermes.bioinspired.criogenic.MasBuilderContent;
import jason.hermes.bioinspired.criogenic.MasBuilderStructure;
import jason.hermes.exception.ErrorCryogeningMASException;
import jason.hermes.exception.ErrorReadingFileException;
import jason.hermes.utils.FileUtils;
import jason.infra.local.RunLocalMAS;
import jason.runtime.RuntimeServicesFactory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

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

        String masName = RunLocalMAS.getRunner().getProject().getSocName();
        List<Agent> allMasAgents = RunLocalMAS.getRunner().getAgs().values().stream()
                .map(localAgArch -> localAgArch.getTS().getAg()).collect(Collectors.toList());

        MasBuilderStructure mas = new MasBuilderStructure(masName, allMasAgents);
        String aslSrc = ts.getAg().getASLSrc();
        File masPath = FileUtils.getMasPath(aslSrc);
        File masFile = null;
        if (masPath != null) {
            try {
                masFile = MasBuilderContent.buildMas(mas, masPath.getPath());
            } catch (Exception e) {
                throw new ErrorCryogeningMASException(masPath.getPath(), e);
            }
        } else {
            throw new ErrorReadingFileException(aslSrc);
        }
        RuntimeServicesFactory.get().stopMAS();

        return masFile != null && masFile.exists();
    }

}
