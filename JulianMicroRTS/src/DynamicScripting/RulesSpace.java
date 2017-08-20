package DynamicScripting;

public class RulesSpace {
	
	//number of conditions, actions, paramethers
	private int numberConditions=3;
	private int numberActions=2;
	private int numberParamethers=2;

	//These variables identify the possible conditions in a rule
	
	//No condition
	private final int condition_no=0;
	//If (enemy is inside ourUnit unit fire range)
	private final int condition_enemyInsideRange = 1;
	//If (enemy is not inside ourUnit fire range)
	private final int condition_enemyInsideRange_not = 2;

	//These variables identify the possible actions of a rule
	
	//Attack
	private final int action_attack=0;	
	//Move_To
//	private final int action_moveto=1;
	//MoveAwayOf
	private final int action_moveawayof=2;
	//wait
//	private final int action_wait=3;
	//cluster
//	private final int action_cluster=4;
	
	//These variables identify the possible actions of a rule

	//Closest_EnemyUnit
	private final int paramether_closestEnemy=0;
	//Fartest_EnemyUnit
	private final int paramether_fartestEnemy=1;
	//Weakest_EnemyUnit
//	private final int paramether_weakestEnemy=0;
	//Strongest_EnemyUnit
//	private final int paramether_strongestEnemy=1;

	
	
	public RulesSpace(){
		
	}


	/**
	 * @return the numberConditions
	 */
	public int getNumberConditions() {
		return numberConditions;
	}


	/**
	 * @return the numberActions
	 */
	public int getNumberActions() {
		return numberActions;
	}


	/**
	 * @return the numberParamethers
	 */
	public int getNumberParamethers() {
		return numberParamethers;
	}


	/**
	 * @return the condition_no
	 */
	public int getCondition_no() {
		return condition_no;
	}


	/**
	 * @return the condition_enemyInsideRange
	 */
	public int getCondition_enemyInsideRange() {
		return condition_enemyInsideRange;
	}


	/**
	 * @return the condition_enemyInsideRange_not
	 */
	public int getCondition_enemyInsideRange_not() {
		return condition_enemyInsideRange_not;
	}


	/**
	 * @return the action_attack
	 */
	public int getAction_attack() {
		return action_attack;
	}


//	/**
//	 * @return the action_moveto
//	 */
//	public int getAction_moveto() {
//		return action_moveto;
//	}


	/**
	 * @return the action_moveawayof
	 */
	public int getAction_moveawayof() {
		return action_moveawayof;
	}


//	/**
//	 * @return the action_wait
//	 */
//	public int getAction_wait() {
//		return action_wait;
//	}

//	/**
//	 * @return the action_cluster
//	 */
//	public int getAction_cluster() {
//		return action_cluster;
//	}


//	/**
//	 * @return the paramether_weakestEnemy
//	 */
//	public int getParamether_weakestEnemy() {
//		return paramether_weakestEnemy;
//	}


	/**
	 * @return the paramether_strongestEnemy
	 */
//	public int getParamether_strongestEnemy() {
//		return paramether_strongestEnemy;
//	}


	/**
	 * @return the paramether_closestEnemy
	 */
	public int getParamether_closestEnemy() {
		return paramether_closestEnemy;
	}


	/**
	 * @return the paramether_fartestEnemy
	 */
	public int getParamether_fartestEnemy() {
		return paramether_fartestEnemy;
	}
	
}
