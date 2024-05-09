package group.chon.agent.hermes.core.capabilities.cryogenate;

import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import group.chon.agent.hermes.core.exception.ErrorCryogeningMASException;
import group.chon.agent.hermes.core.exception.ErrorReadingFileException;
import group.chon.agent.hermes.core.utils.FileUtils;
import jason.infra.local.RunLocalMAS;
import jason.mas2j.ClassParameters;
import jason.runtime.RuntimeServicesFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class CryogenicProcessor {

    private static final Logger LOGGER = Logger.getLogger("HERMES_CRYOGENIC_PROCESSOR");

    public static void checkIfMASMustBeCryogenated(TransitionSystem transitionSystem) {
        String aslSrc = transitionSystem.getAg().getASLSrc();
        if (CryogenicProcessor.checkCryogenicFileExist(aslSrc)) {
            try {
                CryogenicProcessor.cryogenate(aslSrc);
                File masPath = FileUtils.getMasPath(aslSrc);
                File cryogenicFile = new File(masPath.getPath() + File.separator + FileUtils.CRYOGENIC_FILE);
                FileUtils.deleteFile(cryogenicFile);
            } catch (ErrorCryogeningMASException e) {
                CryogenicProcessor.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    public static boolean checkCryogenicFileExist(String aslSrc) {
        boolean hasCryogenicFile = false;
        File masPath = FileUtils.getMasPath(aslSrc);
        if (masPath != null && masPath.exists()) {
            File[] files = masPath.listFiles();
            if (files != null && files.length > 0) {
                File cryogenicFile = new File(masPath.getPath(), FileUtils.CRYOGENIC_FILE);
                List<File> list = Arrays.stream(files).toList();
                hasCryogenicFile = list.contains(cryogenicFile);
            }
        }
        return hasCryogenicFile;
    }

    public static File cryogenate(String aslSrc) throws ErrorCryogeningMASException {
        String masName = RunLocalMAS.getRunner().getProject().getSocName();
        List<Agent> allMasAgents = RunLocalMAS.getRunner().getAgs().values().stream()
                .map(localAgArch -> localAgArch.getTS().getAg()).collect(Collectors.toList());
        ClassParameters infrastructure = RunLocalMAS.getRunner().getProject().getInfrastructure();

        Mas2jBuilderStructure mas2jBuilderStructure = new Mas2jBuilderStructure(
                masName, allMasAgents, infrastructure.getClassName());
        File masPath = FileUtils.getMasPath(aslSrc);
        File masFile = null;
        if (masPath != null) {
            try {
                masFile = Mas2jBuilderFile.buildMas(mas2jBuilderStructure, masPath.getPath());
            } catch (Exception e) {
                throw new ErrorCryogeningMASException(masPath.getPath(), e);
            }
        } else {
            throw new ErrorReadingFileException(aslSrc);
        }
        try {
            RuntimeServicesFactory.get().stopMAS();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return masFile;
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception | Error e) {
            //ignore
        }
    }

}
