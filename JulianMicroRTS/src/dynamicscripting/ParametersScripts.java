package dynamicscripting;

import java.util.ArrayList;

import rts.GameState;
import rts.units.Unit;

public class ParametersScripts {

	RulesSpace rulesSpace;
	
	public ParametersScripts(RulesSpace rulesSpace)
	{
		this.rulesSpace=rulesSpace;
	}
	
	public Unit validationParameter(Unit u, GameState gs, int idParameter, ArrayList<Unit> unitsAssignedEnemys)
	{
		if(idParameter==rulesSpace.getParamether_closestEnemy())
		{
			return closestEnemyUnit(u, gs);
		}
		else if(idParameter==rulesSpace.getParamether_fartestEnemy())
		{
			return fartestEnemyUnit(u, gs);
		}
		else if(idParameter==rulesSpace.getParamether_weakestEnemy())
		{
			return weakestEnemyUnit(u, gs);
		}
		else if(idParameter==rulesSpace.getParamether_strongestEnemy())
		{
			return strongestEnemyUnit(u, gs);
		}
		else if(idParameter==rulesSpace.getParamether_closestEnemyNotAssigned())
		{
			return closestEnemyNotAssignedUnit(u, gs,unitsAssignedEnemys);
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
    
    public Unit weakestEnemyUnit(Unit u, GameState gs) 
    {
        Unit weakest = null;
        int weakesthp = 0;
        for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
            if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
            	int hp=u2.getHitPoints();
                if (weakest == null || hp < weakesthp) {
                	weakest = u2;
                	weakesthp = hp;
                }
            }
        }
        return weakest;
    }

    public Unit strongestEnemyUnit(Unit u, GameState gs) 
    {
        Unit strongest = null;
        int strongesthp = 0;
        for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
            if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
            	int hp=u2.getHitPoints();
                if (strongest == null || hp > strongesthp) {
                	strongest = u2;
                	strongesthp = hp;
                }
            }
        }
        return strongest;
    }
    public Unit closestEnemyNotAssignedUnit(Unit u, GameState gs,ArrayList<Unit> unitsAssignedEnemys) 
    {
        Unit closest = null;
        int closestDistance = 0;
        for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
            if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if ((closest == null || d < closestDistance) && (!unitsAssignedEnemys.contains(u2))) {
                    closest = u2;
                    closestDistance = d;
                }
            }
        }
        return closest;
    }
}
