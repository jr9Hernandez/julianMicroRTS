package dynamicscripting;

import java.util.List;

import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

public class UnitStatistics {
	int maxPlayer;
	int minPlayer;
	GameState g;
	List<Unit> playerUnitsg2;
	List<Unit> playerUnitsEnemyg2;
	Unit[] maxUnits;
	Unit[] minUnits;
	
	
	public UnitStatistics(int maxplayer, int minplayer, GameState g, List<Unit> playerUnitsg2, List<Unit> playerUnitsEnemyg2) 
	{
		this.maxPlayer=maxPlayer;
		this.minPlayer=minPlayer;
		this.g=g;
		this.playerUnitsg2=playerUnitsg2;
		this.playerUnitsEnemyg2=playerUnitsEnemyg2;
		
		maxUnits=new Unit[playerUnitsg2.size()];
		minUnits=new Unit[playerUnitsEnemyg2.size()];
		
		for (int i = 0; i < playerUnitsg2.size(); i++) {
			Unit u = playerUnitsg2.get(i);
			maxUnits[i]=u;
			System.out.println("ulocal"+u.getType().name);
		}
		for (int i = 0; i < playerUnitsEnemyg2.size(); i++) {
			Unit u = playerUnitsEnemyg2.get(i);
			minUnits[i]=u;
			System.out.println("uVist"+u.getType().name);
		}
	}

	public double aFactor()
	{
        PhysicalGameState pgs = g.getPhysicalGameState();

        
        
        
        
        float score = g.getPlayer(maxPlayer).getResources();
        boolean anyunit = false;
        for(Unit u:pgs.getUnits()) {
            if (u.getPlayer()==maxPlayer) {
                anyunit = true;
                score += u.getResources();
                score += u.getCost()*Math.sqrt( u.getHitPoints()/u.getMaxHitPoints() );
            }
        }
        if (!anyunit) return 0;
        return score;
	}
}
