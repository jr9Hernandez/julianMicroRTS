package dynamicscripting;

import java.util.List;

import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class UnitStatistics {
	int maxPlayer;
	int minPlayer;
	GameState g;
	Unit[] maxUnits;
	Unit[] minUnits;
	
	
	public UnitStatistics(int maxplayer, int minplayer, GameState g, List<Unit> playerUnitsg2, List<Unit> playerUnitsEnemyg2) 
	{
		this.maxPlayer=maxPlayer;
		this.minPlayer=minPlayer;
		this.g=g;
		
		maxUnits=new Unit[playerUnitsg2.size()];
		minUnits=new Unit[playerUnitsEnemyg2.size()];
		
		for (int i = 0; i < playerUnitsg2.size(); i++) {
			Unit u = playerUnitsg2.get(i);
			maxUnits[i]=u;
		}
		for (int i = 0; i < playerUnitsEnemyg2.size(); i++) {
			Unit u = playerUnitsEnemyg2.get(i);
			minUnits[i]=u;
		}
	}

	public double bFactor()
	{
		double sumFactor=0;
		double bFactor=0;
		PhysicalGameState pgs = g.getPhysicalGameState();
		for(int i=0;i<maxUnits.length;i++)
		{
			Unit u=maxUnits[i];
			if(pgs.getUnits().contains(u))
			{
				if(u.getHitPoints()<=0)
				{
					sumFactor=sumFactor+0;
				}
				else
				{
					
					sumFactor=sumFactor+(1+((double)u.getHitPoints()/(double)u.getMaxHitPoints()));
					System.out.println("algumsinho"+sumFactor);
				}
			}
		}
		bFactor=(1/(2*(double)maxUnits.length))*sumFactor;
		return bFactor;
	}
}
