package group.chon.agent.hermes.core.capabilities.cryogenate;


import jason.asSemantics.Agent;

import java.util.List;

public class Mas2jBuilderStructure {

	private static final String NEW_LINE = "\n";
	private static final String SPACE = " ";
	private static final String TAB = "\t";
	private static final String COLON = ":";
	private static final String SEMI_COLON = ";";
	private static final String OPEN_BRACE = "{";
	private static final String CLOSE_BRASE = "}";

	private static final String QUOTATION_MARKS = "\"";
	private static final String MAS_FIELD = "MAS";
	private static final String INFRASTRUCTURE_FIELD = "infrastructure";
	private static final String AGENTS_FIELD = "agents";
	public static final String AGENT_ARCH_CLASS_FIELD = "agentArchClass";
	private static final String AGENT_SOURCE_PATH_FIELD = "aslSourcePath";
	
	
	private String masName = "mas_name";
	
	private String infrastruture = "Centralised";
	
	private String agentsSource = "asl";
	
	private List<Agent> agents;

	public Mas2jBuilderStructure() {
		super();
	}

	public Mas2jBuilderStructure(String masName, List<Agent> agents, String infrastruture) {
		super();
		this.masName = masName;
		this.agents = agents;
		this.infrastruture = infrastruture;
	}



	private StringBuffer getCommonStructure() {
		StringBuffer structure = new StringBuffer();
		structure.append(MAS_FIELD).append(SPACE).append(masName).append(SPACE).append(OPEN_BRACE).append(NEW_LINE);
		structure.append(TAB).append(INFRASTRUCTURE_FIELD).append(COLON).append(SPACE).append(infrastruture).append(NEW_LINE);
		structure.append(TAB).append(AGENTS_FIELD).append(COLON).append(NEW_LINE);
		for(Agent agent : agents) {
			structure.append(TAB).append(TAB).append(agent.getTS().getAgArch().getAgName());
			structure.append(SPACE).append(AGENT_ARCH_CLASS_FIELD).append(SPACE).append(agent.getTS().getAgArch().getClass().getName());
			structure.append(SEMI_COLON).append(NEW_LINE);
			
		}
		
		return structure;
	}

	public String getStructure() {
		StringBuffer structure = getCommonStructure();
		structure.append(CLOSE_BRASE).append(NEW_LINE);
		
		return structure.toString();
	}

	public String getCompleteStructure(String agentsSource) {
		this.agentsSource = agentsSource;
		StringBuffer structure = getCommonStructure();
		structure.append(TAB).append(AGENT_SOURCE_PATH_FIELD).append(COLON).append(NEW_LINE);
		structure.append(TAB).append(TAB).append(QUOTATION_MARKS).append(agentsSource).append(QUOTATION_MARKS).append(SEMI_COLON).append(NEW_LINE);
		structure.append(CLOSE_BRASE).append(NEW_LINE);
		
		return structure.toString();
	}

	public String getMasName() {
		return masName;
	}

	public void setMasName(String masName) {
		this.masName = masName;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}
	
}
