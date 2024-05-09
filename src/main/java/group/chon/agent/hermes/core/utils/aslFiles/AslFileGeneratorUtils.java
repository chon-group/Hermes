package group.chon.agent.hermes.core.utils.aslFiles;

import group.chon.agent.hermes.Hermes;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Intention;
import jason.asSyntax.Literal;
import jason.asSyntax.Plan;
import jason.asSyntax.PlanBody;
import jason.asSyntax.Trigger;
import group.chon.agent.hermes.core.capabilities.manageConnections.configuration.ContextNetConfiguration;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.ContextNetMiddleware;
import group.chon.agent.hermes.jasonStdLib.cryogenic;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerador de arquivo .asl do jason para um agente em tempo de execução.
 */
public abstract class AslFileGeneratorUtils {

    /** Extensão de arquivo asl. */
    public static final String ASL_EXTENSION = ".asl";

    /** Pulo de linha. */
    private static final String NEXT_LINE = "\n";

    /** Símbolo de término de comando. */
    private static final String END_SYMBOL = ".";

    /** Símbolo que fica no início das declarações de objetivo. */
    private static final String INITIAL_GOALS_SYMBOL = "!";

    /** Texto inicial para localizar planos padrão de kqml. */
    private static final String KQML_PREFIX = "@kqml";

    /**
     * Gera o conteúdo de um arquivo asl sem as intenções do agente e encapsula no modelo serializável para ser
     * transferido via contextNet.
     *
     * @return Modelo de transferência de agente.
     */
    public static AslTransferenceModel generateAslContentWithoutIntentions(AgArch agArch) {
        Agent agent = agArch.getTS().getAg();

        StringBuilder content = new StringBuilder();
        content.append(generateInitialBeliefs(agent) + NEXT_LINE);
        content.append(generatePlans(agent) + NEXT_LINE);

        AslTransferenceModel aslTransferenceModel = new AslTransferenceModel(agArch.getAgName(),
                content.toString().getBytes(), agArch.getClass().getName());
        return aslTransferenceModel;
    }

    /**
     * Gera o conteúdo de um arquivo asl e encapsula no modelo serializável para ser transferido via contextNet.
     *
     * @return Modelo de transferência de agente.
     */
    public static AslTransferenceModel generateAslContent(AgArch agArch) {
        Agent agent = agArch.getTS().getAg();

        StringBuilder content = new StringBuilder();
        content.append(generateInitialBeliefs(agent) + NEXT_LINE);
        content.append(generateInitialGoals(agent) + NEXT_LINE);
        content.append(generatePlans(agent) + NEXT_LINE);

        AslTransferenceModel aslTransferenceModel = new AslTransferenceModel(agArch.getAgName(),
                content.toString().getBytes(), agArch.getClass().getName());
        return aslTransferenceModel;
    }

    public static AslTransferenceModel generateAslContentWithRandomUUID(AgArch agArch) {
        Agent agent = agArch.getTS().getAg();

        StringBuilder content = new StringBuilder();
        content.append(generateInitialBeliefsWithRandomUUID(agent) + NEXT_LINE);
        content.append(generateInitialGoals(agent) + NEXT_LINE);
        content.append(generatePlans(agent) + NEXT_LINE);

        AslTransferenceModel aslTransferenceModel = new AslTransferenceModel(agArch.getAgName(),
                content.toString().getBytes(), agArch.getClass().getName());
        return aslTransferenceModel;
    }

    /**
     * Cria um novo arquivo .asl no endereço passado, de acordo com o nome e o conteúdo passado no modelo de
     * transferência de agente.
     *
     * @param path Caminho da pasta.
     * @param aslTransferenceModel Modelo de transferência de agente.
     */
    public static void createAslFile(String path, AslTransferenceModel aslTransferenceModel) {
        // TODO: verificar o que deve ser feito com esse 'file:'.
        // TODO: fazer um tratamento correto para cada excecao.
        path = path.replaceAll("\\\\", "/");
        if (path.charAt(path.length() -1) != '/') {
            path += "/";
        }
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(aslTransferenceModel.getFileContent());
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Captura as crenças do agente em tempo de execução.
     *
     * @param agent Agente.
     * @return Declaração em String das crença do agente em tempo de execução.
     */
    private static String generateInitialBeliefs(Agent agent) {
        StringBuilder beliefs = new StringBuilder();
        beliefs.append("/* Initial beliefs and rules */" + NEXT_LINE);

        Iterator<Literal> beliefsIterator = agent.getBB().iterator();

        while (beliefsIterator.hasNext()) {
            Literal literal = beliefsIterator.next();
            String beliefString = literal.toString();
            //if (!beliefString.startsWith(BeliefUtils.MY_MAS_BELIEF_PREFIX)) {
                beliefs.append(beliefString + END_SYMBOL + NEXT_LINE);
            //}
        }

        return beliefs.toString();
    }

    /**
     * Captura as crenças do agente em tempo de execução.
     *
     * @param agent Agente.
     * @return Declaração em String das crença do agente em tempo de execução.
     */
    private static String generateInitialBeliefsWithRandomUUID(Agent agent) {
        StringBuilder beliefs = new StringBuilder();
        Literal connectionBelief = null;
        if (agent.getTS().getAgArch() instanceof Hermes) {
            Hermes hermes = (Hermes) agent.getTS().getAgArch();
            for (CommunicationMiddleware middleware : hermes.getCommunicationMiddlewareHashMap().values()) {
                if (middleware instanceof ContextNetMiddleware) {
                    ContextNetMiddleware contextNetMiddleware = (ContextNetMiddleware) middleware;
                    ContextNetConfiguration contextNetConfiguration = (ContextNetConfiguration) contextNetMiddleware
                            .getConfiguration();
                    ContextNetConfiguration clone = contextNetConfiguration.clone();
                    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                    buffer.putLong(new Date().getTime());
                    clone.setMyUUIDString(UUID.nameUUIDFromBytes(buffer.array()).toString());
                    connectionBelief = clone.toBelief();
                }
            }
        }
        beliefs.append("/* Initial beliefs and rules */" + NEXT_LINE);

        Iterator<Literal> beliefsIterator = agent.getBB().iterator();

        while (beliefsIterator.hasNext()) {
            Literal literal = beliefsIterator.next();
            String beliefString = literal.toString();
            if (literal.getFunctor().equals(ContextNetConfiguration.BELIEF_PREFIX)) {
                if (connectionBelief != null) {
                    beliefs.append(connectionBelief.toString() + END_SYMBOL + NEXT_LINE);
                }
            } else {
                beliefs.append(beliefString + END_SYMBOL + NEXT_LINE);
            }
        }

        return beliefs.toString();
    }

    private static String getIntentionName(Intention intention) {
        String intentionName = null;
        while (intention.iterator().hasNext()) {
            Trigger trigger = intention.iterator().next().getTrigger();
            if (trigger != null) {
                intentionName = trigger.toString();
                break;
            }
        }
        if (intentionName != null) {
            intentionName = intentionName.replaceAll("!", "");
            intentionName = intentionName.replaceAll("\\+", "");
            intentionName = intentionName.replaceAll("-", "");
            intentionName = intentionName.trim();
            if (intentionName.contains(" ")) {
                intentionName = intentionName.replaceAll(" ", "");
            }
        }
        return intentionName;
    }

    private static boolean hasCryogenicIntention(Intention intention, StringBuilder beliefOfCryogenicTrigger) {
        boolean hasCryogenicIntention = false;
        for (IntendedMeans intendedMeans : intention) {
            PlanBody currentStep = intendedMeans.getCurrentStep();
            hasCryogenicIntention = currentStep.toString().equals(END_SYMBOL + cryogenic.class.getSimpleName());
            if (hasCryogenicIntention) {
                Trigger intentionTrigger = intendedMeans.getTrigger();
                Literal intentionTriggerLiteral = intentionTrigger.getLiteral();

                if (intentionTriggerLiteral.canBeAddedInBB() && Trigger.TEType.belief.equals(intentionTrigger.getType())) {
                    beliefOfCryogenicTrigger.append(intentionTriggerLiteral.toString()).append(END_SYMBOL);
                }
                break;
            }
        }
        return hasCryogenicIntention;
    }

    /**
     * Captura os objetivos iniciais do agente em tempo de execução.
     *
     * @param agent Agente.
     * @return Texto com os objetivos iniciais do agente em tempo de execução.
     */
    private static String generateInitialGoals(Agent agent) {
        StringBuilder initialGoals = new StringBuilder();
        initialGoals.append("/* Initial goals */" + NEXT_LINE);
        List<String> intentionsNames = new LinkedList<>();

        Circumstance circumstance = agent.getTS().getC();
        Queue<Intention> intentions = circumstance.getRunningIntentions();
        // recupera as intenções atuais.
        for (Intention intention : intentions) {
            String runningIntentionName = getIntentionName(intention);
            if (runningIntentionName != null && !intentionsNames.contains(runningIntentionName)) {
                intentionsNames.add(runningIntentionName);
            }
        }
        // recupera a intenção selecionada para ser executada no proximo ciclo.
        Intention selectedIntention = circumstance.getSelectedIntention();
        if (selectedIntention != null) {
            String selectedIntentionName = getIntentionName(selectedIntention);
            if (selectedIntentionName != null && !intentionsNames.contains(selectedIntentionName)) {
                intentionsNames.add(selectedIntentionName);
            }
        }
        // recupera as intenções que estão pendentes para serem executadas em algum momento em ordem.
        Map<String, Intention> pendingIntentions = circumstance.getPendingIntentions();
        if (pendingIntentions != null && !pendingIntentions.isEmpty()) {
            Map<Integer, String> intentionsNumberAndKeyMap = new HashMap<>();
            for (String keyPendingIntention : pendingIntentions.keySet()) {
                intentionsNumberAndKeyMap.put(pendingIntentions.get(keyPendingIntention).getId(), keyPendingIntention);
            }

            List<Integer> intentionsNumberOrdenedList = intentionsNumberAndKeyMap.keySet().stream().sorted().collect(Collectors.toList());

            for (Integer intentionId : intentionsNumberOrdenedList) {
                Intention intention = pendingIntentions.get(intentionsNumberAndKeyMap.get(intentionId));
                String pendingIntentionName = getIntentionName(intention);
                if (pendingIntentionName != null && !intentionsNames.contains(pendingIntentionName)) {
                    intentionsNames.add(pendingIntentionName);
                }
            }
        }
        for (String intentionsName : intentionsNames) {
            initialGoals.append(INITIAL_GOALS_SYMBOL + intentionsName + END_SYMBOL + NEXT_LINE);
        }

        return initialGoals.toString();
    }

    /**
     * Captura os planos do agente.
     *
     * @param agent Agente.
     * @return Texto com os planos do agente.
     */
    private static String generatePlans(Agent agent) {
        StringBuilder plains = new StringBuilder();
        plains.append("/* Plans */" + NEXT_LINE);

        List<Plan> plans = agent.getPL().getPlans();

        for (Plan plan : plans) {
            String p = plan.toASString();
            if (!p.startsWith(KQML_PREFIX)) {
                plains.append(p + NEXT_LINE);
            }
        }

        return plains.toString();
    }

}
