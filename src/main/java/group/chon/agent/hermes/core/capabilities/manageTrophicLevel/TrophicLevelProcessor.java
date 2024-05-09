package jason.hermes.capabilities.manageTrophicLevel;

import jason.asSemantics.Agent;
import jason.bb.BeliefBase;
import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredData;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.HermesUtils;

import java.util.List;

public abstract class TrophicLevelProcessor {

    public static void initTrophicLevel(Agent agent, BioinspiredData bioinspiredData) {
        BeliefBase beliefBase = agent.getBB();
        List<String> beliefByStartWithList = BeliefUtils.getBeliefsInStringByFunction(beliefBase,
                BeliefUtils.MY_TROPHIC_LEVEL_PREFIX);

        if (beliefByStartWithList.isEmpty()) {
            BeliefUtils.addBelief(BeliefUtils.MY_TROPHIC_LEVEL_VALUE, BeliefBase.ASelf,
                    bioinspiredData.getMyTrophicLevel().name(), agent);
        } else {
            String source = HermesUtils.getTermInString(BeliefBase.ASelf);
            List<String> beliefValue = BeliefUtils.getBeliefValue(beliefByStartWithList, source);
            String value = HermesUtils.treatString(beliefValue.get(0));
            TrophicLevelEnum trophicLevelEnum = TrophicLevelEnum.get(value);
            bioinspiredData.setMyTrophicLevel(trophicLevelEnum);
        }
    }

}
