package dynamicscripting;

import java.util.ArrayList;

import rts.GameState;
import rts.UnitAction;
import rts.units.Unit;

public class ConditionsScripts {
	
	RulesSpace rulesSpace;
	ParametersScripts parametersScripts;
	GameState gs;
	
	public ConditionsScripts(RulesSpace rulesSpace, ParametersScripts parametersScripts, GameState gs)
	{
		this.rulesSpace=rulesSpace; 
		this.parametersScripts=parametersScripts;
		this.gs=gs;
	}
	
	public boolean validationCondition(int idCondition, int idParamether, Unit u, ArrayList<Unit> unitsAssignedEnemys)
	{
		if(idCondition==rulesSpace.getCondition_no())
		{
			return true;
		}
	    else if(idCondition==rulesSpace.getCondition_enemyInsideRange() && validationConditionEnemyInsideRange(idParamether,u,unitsAssignedEnemys))
		{
			return true;
		}
//	    else if(idCondition==rulesSpace.getCondition_enemyInsideRange_not() && !validationConditionEnemyInsideRange(idParamether,u))
//		{
//			return true;
//		}
	    else if(idCondition==rulesSpace.getCondition_enemyPointingRange() && validationConditionEnemyPointingRange(idParamether,u,unitsAssignedEnemys))
		{
			return true;
		}
		return false;
	}
	
	public boolean validationConditionEnemyInsideRange(int idParameter,Unit u, ArrayList<Unit> unitsAssignedEnemys) {
		
		Unit u2 = parametersScripts.validationParameter(u, gs, idParameter,unitsAssignedEnemys);

		int dx = u2.getX()-u.getX();
        int dy = u2.getY()-u.getY();
        double d = Math.sqrt(dx*dx+dy*dy);
        if (d<=u.getAttackRange()+1) 
        {
            return true;
        }
        return false;
	}
	
	public boolean validationConditionEnemyPointingRange(int idParameter,Unit u, ArrayList<Unit> unitsAssignedEnemys) {
		
		Unit u2 = parametersScripts.validationParameter(u, gs, idParameter,unitsAssignedEnemys);

		int dx = u2.getX()-u.getX();
        int dy = u2.getY()-u.getY();
        double d = Math.sqrt(dx*dx+dy*dy);
        if (d<=u2.getAttackRange()+1) 
        {
            return true;
        }
        return false;
	}

}
