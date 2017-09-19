package dynamicscripting;

public class RulesSpace {
	
	//number of conditions, actions, paramethers
	private int numberConditions=7;
	private int numberActions=2;
	private int numberParamethers=5;

	//These variables identify the possible conditions in a rule
	
	//No condition
	private final int condition_no=0;
	//If (enemy is inside ourUnit unit fire range)
	private final int condition_enemyInsideRange1 = 1;
	private final int condition_enemyInsideRange2 = 2;
	private final int condition_enemyInsideRange3 = 3;
	//If (enemy is not inside ourUnit fire range)
//	private final int condition_enemyInsideRange_not = 2;
	//If (our unit is inside inside ourUnit fire range)
	private final int condition_enemyPointingRange1 = 4;
	private final int condition_enemyPointingRange2 = 5;
	private final int condition_enemyPointingRange3 = 6;

	//These variables identify the possible actions of a rule
	
	//Attack
	private final int action_attack=0;	
	//Move_To
//	private final int action_moveto=1;
	//MoveAwayOf
	private final int action_moveawayof=1;
	//wait
//	private final int action_wait=3;
	//cluster
//	private final int action_cluster=4;
	
	//These variables identify the possible actions of a rule

	//Closest_EnemyUnit
	private final int paramether_closestEnemy=3;
	//Fartest_EnemyUnit
	private final int paramether_fartestEnemy=2;
	//Weakest_EnemyUnit
	private final int paramether_weakestEnemy=0;
	//Strongest_EnemyUnit
	private final int paramether_strongestEnemy=1;
	//ClosestEnemy Not Assigned
	private final int paramether_closestEnemyNotAssigned=4;

	
	
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
	public int getCondition_enemyInsideRange1() {
		return condition_enemyInsideRange1;
	}
	
	/**
	 * @return the condition_enemyInsideRange
	 */
	public int getCondition_enemyInsideRange2() {
		return condition_enemyInsideRange2;
	}
	
	/**
	 * @return the condition_enemyInsideRange
	 */
	public int getCondition_enemyInsideRange3() {
		return condition_enemyInsideRange3;
	}


//	/**
//	 * @return the condition_enemyInsideRange_not
//	 */
//	public int getCondition_enemyInsideRange_not() {
//		return condition_enemyInsideRange_not;
//	}


	/**
	 * @return the condition_enemyPointingRange_not
	 */
	public int getCondition_enemyPointingRange1() {
		return condition_enemyPointingRange1;
	}
	/**
	 * @return the condition_enemyPointingRange_not
	 */
	public int getCondition_enemyPointingRange2() {
		return condition_enemyPointingRange2;
	}
	/**
	 * @return the condition_enemyPointingRange_not
	 */
	public int getCondition_enemyPointingRange3() {
		return condition_enemyPointingRange3;
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


	/**
	 * @return the paramether_weakestEnemy
	 */
	public int getParamether_weakestEnemy() {
		return paramether_weakestEnemy;
	}


	/**
	 * @return the paramether_strongestEnemy
	 */
	public int getParamether_strongestEnemy() {
		return paramether_strongestEnemy;
	}


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


	/**
	 * @return the paramether_closestEnemyNotAssigned
	 */
	public int getParamether_closestEnemyNotAssigned() {
		return paramether_closestEnemyNotAssigned;
	}
	
}
