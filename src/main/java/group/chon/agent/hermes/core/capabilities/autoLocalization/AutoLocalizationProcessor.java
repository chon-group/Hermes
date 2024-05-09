package jason.hermes.capabilities.autoLocalization;

import group.chon.agent.hermes.Hermes;
import jason.asSemantics.Agent;
import jason.asSyntax.Atom;
import jason.bb.BeliefBase;
import jason.hermes.utils.BeliefUtils;
import jason.infra.local.RunLocalMAS;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AutoLocalizationProcessor {

    public static void initAutoLocalization(Agent agent) {
        BeliefUtils.replaceAllBelief(BeliefUtils.MY_MAS_BELIEF_PREFIX, BeliefUtils.MY_MAS_BELIEF_VALUE, BeliefBase.ASelf,
                RunLocalMAS.getRunner().getProject().getSocName(), agent);
    }

    public static void updateOtherAgentsLocalization(Hermes hermes) {
        List<Agent> agentsOfTheMAS = RunLocalMAS.getRunner().getAgs().values().stream()
                    .map(localAgArch -> localAgArch.getTS().getAg()).collect(Collectors.toList());

        AutoLocalizationProcessor.autoLocalization(hermes.getAgName(), agentsOfTheMAS, false);
    }


    public static void autoLocalization(String agentName, List<Agent> agents, boolean reload) {
        String masName = RunLocalMAS.getRunner().getProject().getSocName();

        for (Agent agent : agents) {
            if (!agentName.equals(agent.getTS().getAgArch().getAgName())) {
                if (reload) {
                    BeliefUtils.replaceAllBelief(BeliefUtils.MY_MAS_BELIEF_PREFIX, BeliefUtils.MY_MAS_BELIEF_VALUE,
                            new Atom(agentName), masName, agent);
                } else {
                    BeliefUtils.addBeliefIfAbsent(BeliefUtils.MY_MAS_BELIEF_PREFIX, BeliefUtils.MY_MAS_BELIEF_VALUE,
                            new Atom(agentName), masName, agent);
                }
            }
        }
    }

}
