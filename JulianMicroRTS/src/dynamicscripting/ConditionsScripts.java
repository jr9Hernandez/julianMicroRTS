package dynamicscripting;

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
	
	public boolean validationCondition(int idCondition, int idParamether, Unit u)
	{
		if(idCondition==rulesSpace.getCondition_no())
		{
			return true;
		}
	    else if(idCondition==rulesSpace.getCondition_enemyInsideRange() && validationConditionEnemyInsideRange(idParamether,u))
		{
			return true;
		}
	    else if(idCondition==rulesSpace.getCondition_enemyInsideRange_not() && !validationConditionEnemyInsideRange(idParamether,u))
		{
			return true;
		}
		return false;
	}
	
	public boolean validationConditionEnemyInsideRange(int idParameter,Unit u) {
		
		Unit u2 = parametersScripts.validationParameter(u, gs, idParameter);

		int dx = u2.getX()-u.getX();
        int dy = u2.getY()-u.getY();
        double d = Math.sqrt(dx*dx+dy*dy);
        if (d<=u.getAttackRange()) 
        {
            return true;
        }
        return false;
	}

}
