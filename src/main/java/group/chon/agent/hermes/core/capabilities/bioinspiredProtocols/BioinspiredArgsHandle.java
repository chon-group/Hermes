package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols;

import group.chon.agent.hermes.Hermes;
import jason.JasonException;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredRoleEnum;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredStageEnum;
import group.chon.agent.hermes.core.capabilities.manageTrophicLevel.TrophicLevelEnum;
import group.chon.agent.hermes.core.utils.ArgsUtils;
import group.chon.agent.hermes.core.utils.BioInspiredUtils;
import group.chon.agent.hermes.core.utils.HermesUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BioinspiredArgsHandle {

    public static BioinspiredData getBioinspiredDataByArgs(Term[] args, Hermes hermes) throws JasonException {
        BioinspiredData bioinspiredData;

        String receiver = ArgsUtils.getInString(args[0]);
        String protocolName = ArgsUtils.getInString(args[1]).toUpperCase();
        BioinspiredProtocolsEnum bioinspiredProtocol = BioinspiredProtocolsEnum.getBioInspiredProtocol(protocolName);

        String connectionIdentifier = hermes.getFirstConnectionAvailable();
        TrophicLevelEnum trophicLevelEnum = hermes.getBioinspiredData().getMyTrophicLevel();

        if (args.length == 2) {
            bioinspiredData = BioinspiredArgsHandle.getByTwoArgs(bioinspiredProtocol,
                    connectionIdentifier, trophicLevelEnum);
        } else if (args.length == 3){
            bioinspiredData = BioinspiredArgsHandle.getByThreeArgs(args[2], bioinspiredProtocol,
                    connectionIdentifier, trophicLevelEnum);
        } else {
            bioinspiredData = BioinspiredArgsHandle.getByFourOrMoreArgs(args[2], args[3], bioinspiredProtocol,
                    trophicLevelEnum);
        }


        String myIdentification = hermes.getCommunicationMiddleware(
                bioinspiredData.getConnectionIdentifier()).getAgentIdentification();
        bioinspiredData.setSenderIdentification(myIdentification);
        bioinspiredData.setReceiverIdentification(receiver);
        hermes.setBioinspiredData(bioinspiredData);

        return bioinspiredData;
    }

    private static BioinspiredData getByTwoArgs(BioinspiredProtocolsEnum bioinspiredProtocol,
                                                String connectionIdentifier,
                                                TrophicLevelEnum trophicLevelEnum) {
        List<String> nameOfAgentsToBeTransferred;
        if (BioinspiredProtocolsEnum.MUTUALISM.equals(bioinspiredProtocol)){
            nameOfAgentsToBeTransferred = BioInspiredUtils.getAgentsNameExceptHermesAgentName();
        } else {
            nameOfAgentsToBeTransferred = BioInspiredUtils.getAllAgentsName();
        }
        return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                trophicLevelEnum, true);
    }

    private static BioinspiredData getByThreeArgs(Term arg2, BioinspiredProtocolsEnum bioinspiredProtocol,
                                                  String connectionIdentifier, TrophicLevelEnum trophicLevelEnum) throws JasonException {
        boolean entireMAS = false;
        List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
        if (arg2.isList()) {
            ListTerm agentNamesList = ArgsUtils.getInListTerm(arg2);
            nameOfAgentsToBeTransferred.addAll(HermesUtils.getAgentNamesInList(agentNamesList));
            entireMAS = nameOfAgentsToBeTransferred.size() == BioInspiredUtils.getAllAgentsName().size();
        } else {
            String agentNameOrConnectionIdentifier = ArgsUtils.getInString(arg2);
            if (HermesUtils.verifyAgentExist(agentNameOrConnectionIdentifier)) {
                nameOfAgentsToBeTransferred.add(agentNameOrConnectionIdentifier);
                entireMAS = false;
            } else {
                connectionIdentifier = agentNameOrConnectionIdentifier;
                if (BioinspiredProtocolsEnum.MUTUALISM.equals(bioinspiredProtocol)) {
                    nameOfAgentsToBeTransferred = BioInspiredUtils.getAgentsNameExceptHermesAgentName();
                } else {
                    nameOfAgentsToBeTransferred = BioInspiredUtils.getAllAgentsName();
                }
                entireMAS = true;
            }
        }
        return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                trophicLevelEnum, entireMAS);
    }

    private static BioinspiredData getByFourOrMoreArgs(Term arg2, Term arg3, BioinspiredProtocolsEnum bioinspiredProtocol,
                                                       TrophicLevelEnum trophicLevelEnum) throws JasonException {
        boolean entireMAS = false;
        List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
        if (arg2.isList()) {
            ListTerm agentNamesList = ArgsUtils.getInListTerm(arg2);
            nameOfAgentsToBeTransferred.addAll(HermesUtils.getAgentNamesInList(agentNamesList));
            entireMAS = nameOfAgentsToBeTransferred.size() == BioInspiredUtils.getAllAgentsName().size();
        } else {
            String agentName = ArgsUtils.getInString(arg2);
            nameOfAgentsToBeTransferred.add(agentName);
        }
        String connectionIdentifier = ArgsUtils.getInString(arg3);
        return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                trophicLevelEnum, entireMAS);
    }

}
