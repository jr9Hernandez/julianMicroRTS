package dynamicscripting;

public class Rule {
	
	private int rule_id;
	private int weight;
	protected boolean [] active;
	private int rule_condition;
	private int rule_action;
	private int rule_paramether;
	

	public Rule(int rule_id,int weight, int sizeUnits, int rule_condition, int rule_action, int rule_paramether){
		
		this.rule_id=rule_id;		
		this.weight=weight;
		this.active=new boolean[sizeUnits];
		this.rule_condition=rule_condition;
		this.rule_action=rule_action;
		this.rule_paramether=rule_paramether;

	}

	/**
	 * @return the weight of the rule, is fixed during all the encounter
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the active, determines if the current object rule is active for the script
	 */
	public boolean [] getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean [] active) {
		this.active = active;
	}

	/**
	 * @return the rule_condition, defines the if condition of the rule
	 */
	public int getRule_condition() {
		return rule_condition;
	}

	/**
	 * @param rule_condition the rule_condition to set
	 */
	public void setRule_condition(int rule_condition) {
		this.rule_condition = rule_condition;
	}

	/**
	 * @return the rule_action, the action to do by the unit
	 */
	public int getRule_action() {
		return rule_action;
	}

	/**
	 * @param rule_action the rule_action to set
	 */
	public void setRule_action(int rule_action) {
		this.rule_action = rule_action;
	}

	/**
	 * @return the rule_paramether, for the action to do
	 */
	public int getRule_paramether() {
		return rule_paramether;
	}

	/**
	 * @param rule_paramether the rule_paramether to set
	 */
	public void setRule_paramether(int rule_paramether) {
		this.rule_paramether = rule_paramether;
	}

	/**
	 * @return the rule_id
	 */
	public int getRule_id() {
		return rule_id;
	}

	/**
	 * @param rule_id the rule_id to set
	 */
	public void setRule_id(int rule_id) {
		this.rule_id = rule_id;
	}
	
}
