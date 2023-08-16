package jason.hermes.utils;

import jason.Hermes;
import jason.architecture.AgArch;
import jason.asSemantics.TransitionSystem;
import jason.hermes.bioinspired.AslFileGenerator;
import jason.infra.local.LocalAgArch;
import jason.infra.local.RunLocalMAS;
import jason.mas2j.ClassParameters;
import jason.runtime.RuntimeServices;
import jason.runtime.RuntimeServicesFactory;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BioInspiredUtils {

    public static final Logger LOGGER = Logger.getLogger("BIOINSPIRED PROTOCOL");
    public static List<String> getAllAgentsName() {
        Map<String, LocalAgArch> agentsOfTheSMA = RunLocalMAS.getRunner().getAgs();
        List<String> nameAgents = new ArrayList<String>();

        for (LocalAgArch agArch: agentsOfTheSMA.values()) {
            nameAgents.add(agArch.getFirstAgArch().getAgName());
        }
        return nameAgents;
    }

    public static List<String> getAgentsNameExceptCommunicatorAgentName() {
        Map<String, LocalAgArch> agentsOfTheSMA = RunLocalMAS.getRunner().getAgs();
        List<String> nameAgents = new ArrayList<String>();

        for (LocalAgArch agArch: agentsOfTheSMA.values()) {
            AgArch userAgArch = agArch.getFirstAgArch();
            String arch = userAgArch.getClass().getName();
            if (!arch.equals(Hermes.class.getName())) {
                nameAgents.add(agArch.getFirstAgArch().getAgName());
            }
        }
        return nameAgents;
    }

    public static boolean verifyAgentExist(String agentName) {
        List<String> allAgentsName = getAllAgentsName();
        return  allAgentsName.contains(agentName);
    }

    public static String getPath(String agentName) {
        String path = "";
        for (LocalAgArch localAgArch : RunLocalMAS.getRunner().getAgs().values()) {
            path = localAgArch.getTS().getAg().getASLSrc();
            path = path.substring(0, path.length() - (localAgArch.getAgName() + AslFileGenerator.ASL_EXTENSION).length());
            break;
        }
        path += agentName + AslFileGenerator.ASL_EXTENSION;
        return path;
    }

    public static int startAgent(TransitionSystem ts, String name, String path, String agArchClasse, int qtdAgentsInstantiated) {
        // TODO: Verificar o que deve acontecer se der erro na instanciação do agente.
        try {
            String agClass = null;
            List<String> agArchClasses = new ArrayList<String>();
            if(agArchClasse != null && !agArchClasse.isEmpty()) {
                agArchClasses.add(agArchClasse);
            }
            ClassParameters bbPars = null;

            RuntimeServices runtimeServices = RuntimeServicesFactory.get();
            name = runtimeServices.createAgent(name, path, agClass, agArchClasses, bbPars, ts.getSettings(), ts.getAg());
            runtimeServices.startAgent(name);
            qtdAgentsInstantiated++;
            return qtdAgentsInstantiated;
        } catch (Exception e) {
            BioInspiredUtils.LOGGER.log(Level.SEVERE, "Error instantiating the agent: " + name);
            e.printStackTrace();
        }
        return qtdAgentsInstantiated;
    }

    public static void killAgentsNotTransferred(TransitionSystem ts, List<String> agentsName) {
        Map<String, LocalAgArch> agentsOfTheSMA = RunLocalMAS.getRunner().getAgs();
        for (LocalAgArch localAgArch : agentsOfTheSMA.values()) {
            if (!agentsName.contains(localAgArch.getAgName())) {
                try {
                    RuntimeServicesFactory.get().killAgent(localAgArch.getAgName(),
                            ts.getAgArch().getAgName(), 0);
                } catch (RemoteException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE, "Error when killing the agent '" + localAgArch.getAgName() + "'");
                }
                String aslSrc = localAgArch.getTS().getAg().getASLSrc();
                if (aslSrc.startsWith("file:")) {
                    aslSrc = aslSrc.substring(5);
                }
                File file = new File(aslSrc);
                BioInspiredUtils.LOGGER.log(Level.INFO, "Killing existing agents in the MAS: " + localAgArch.getAgName());
                deleteFileAsl(file);
            }
        }
    }

    public static void killTransferredAgents(TransitionSystem ts, List<String> agentsName) {
        Map<String, LocalAgArch> agentsOfTheSMA = RunLocalMAS.getRunner().getAgs();
        for (LocalAgArch localAgArch : agentsOfTheSMA.values()) {
            if (agentsName.contains(localAgArch.getAgName())) {
                try {
                    RuntimeServicesFactory.get().killAgent(localAgArch.getAgName(),
                            ts.getAgArch().getAgName(), 0);
                } catch (RemoteException e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE, "Error when killing the agent '" + localAgArch.getAgName() + "'");
                }
                // TODO: Resolver o problema de estar vindo esse 'file:' no inicio do path do agent.
                String aslSrc = localAgArch.getTS().getAg().getASLSrc();
                if (aslSrc.startsWith("file:")) {
                    aslSrc = aslSrc.substring(5);
                }
                File file = new File(aslSrc);
                BioInspiredUtils.LOGGER.log(Level.INFO, "Killing the transferred agent copy: " + localAgArch.getAgName());
                deleteFileAsl(file);
            }
        }
    }

    public static void deleteFileAsl(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}