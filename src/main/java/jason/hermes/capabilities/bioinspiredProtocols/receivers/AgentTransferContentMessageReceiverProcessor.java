package jason.hermes.capabilities.bioinspiredProtocols.receivers;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.hermes.OutGoingMessage;
import jason.hermes.capabilities.autoLocalization.AutoLocalizationProcessor;
import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredData;
import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredProcessor;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferConfirmationMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferContentMessageDto;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;
import jason.hermes.utils.aslFiles.AslFileGeneratorUtils;
import jason.hermes.utils.aslFiles.AslTransferenceModel;
import jason.infra.local.RunLocalMAS;
import jason.runtime.RuntimeServicesFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class AgentTransferContentMessageReceiverProcessor {

    public static void receiveAgentsContent(TransitionSystem ts, BioinspiredData bioinspiredData,
                                             CommunicationMiddleware communicationMiddleware,
                                             AgentTransferContentMessageDto agentTransferContentMessageDto) {
        boolean agentTransferSuccess = false;
        boolean canKill = false;

        List<String> nameOfAgentsToBeTransferredList = bioinspiredData.getNameOfAgentsToBeTransferred();
        int qtdAgentsInstantiated = 0;

        List<String> nameOfAgentsInstantiated = new ArrayList<>();
        List<Agent> hermesAgentsTransferred = new ArrayList<>();
        List<Agent> notHermesAgentsTransferred = new ArrayList<>();
        // TODO: tratar se tiver erro para instanciar os agentes.
        for (String agentName : nameOfAgentsToBeTransferredList) {
            AslTransferenceModel aslTransferenceModel = agentTransferContentMessageDto.getAgentsSourceCode()
                    .get(agentName);
            String agentNameInstantiated = null;
            try {
                agentNameInstantiated = RuntimeServicesFactory.get().getNewAgentName(aslTransferenceModel.getName());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            if (agentNameInstantiated != null) {
                String path = BioInspiredUtils.getPath(agentNameInstantiated);
                String agArchClass = aslTransferenceModel.getAgentArchClass();

                AslFileGeneratorUtils.createAslFile(path, aslTransferenceModel);

                qtdAgentsInstantiated = BioInspiredUtils.startAgent(ts, agentNameInstantiated, path, agArchClass, qtdAgentsInstantiated);
                Agent agent = RunLocalMAS.getRunner().getAg(agentNameInstantiated).getTS().getAg();
                if (Hermes.class.getName().equals(aslTransferenceModel.getAgentArchClass())) {
                    try {
                        Hermes hermes = HermesUtils.checkArchClass(agent.getTS().getAgArch());
                        hermes.setMoved(true);
                    } catch (JasonException e) {
                        throw new RuntimeException(e);
                    }
                    hermesAgentsTransferred.add(agent);
                } else {
                    notHermesAgentsTransferred.add(agent);
                }

                nameOfAgentsInstantiated.add(agentNameInstantiated);
            }
        }

        if (qtdAgentsInstantiated == nameOfAgentsToBeTransferredList.size()) {
            // Todos os agentes instanciados
            agentTransferSuccess = true;
            if (bioinspiredData.isHasHermesAgentTransferred()) {
                bioinspiredData.setHermesAgentsTransferred(hermesAgentsTransferred);
                AutoLocalizationProcessor.autoLocalization(ts.getAgArch().getAgName(), notHermesAgentsTransferred, true);
            } else {
                AutoLocalizationProcessor.autoLocalization(ts.getAgArch().getAgName(), notHermesAgentsTransferred, true);
            }
            canKill = BioinspiredProcessor.canKillOriginCopy(bioinspiredData);

        }

        bioinspiredData.setNameOfAgentsInstantiated(nameOfAgentsInstantiated);
        bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());
        AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto =
                new AgentTransferConfirmationMessageDto(agentTransferSuccess, canKill);
        BioInspiredUtils.log(Level.INFO, "Sending the agent transfer confirmation that is: " + agentTransferSuccess);
        OutGoingMessage.sendMessageBioinspiredMessage(agentTransferConfirmationMessageDto,
                communicationMiddleware,
                bioinspiredData.getReceiverIdentification());

    }

}
