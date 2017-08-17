package DynamicScripting;

import rts.GameState;
import rts.units.Unit;

public class ParametersScripts {

	RulesSpace rulesSpace;
	
	public ParametersScripts(RulesSpace rulesSpace)
	{
		this.rulesSpace=rulesSpace;
	}
	
	public Unit validationParameter(Unit u, GameState gs, int idParameter)
	{
		if(idParameter==rulesSpace.getParamether_closestEnemy())
		{
			return closestEnemyUnit(u, gs);
		}
		else if(idParameter==rulesSpace.getParamether_fartestEnemy())
		{
			return fartestEnemyUnit(u, gs);
		}
		return null;
	}
	
    public Unit closestEnemyUnit(Unit u, GameState gs) 
    {
        Unit closest = null;
        int closestDistance = 0;
        for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
            if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closest == null || d < closestDistance) {
                    closest = u2;
                    closestDistance = d;
                }
            }
        }
        return closest;
    }
    public Unit fartestEnemyUnit(Unit u, GameState gs) 
    {
        Unit farthest = null;
        int farthestDistance = 0;
        for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
            if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (farthest == null || d > farthestDistance) {
                	farthest = u2;
                	farthestDistance = d;
                }
            }
        }
        return farthest;
    }
}
