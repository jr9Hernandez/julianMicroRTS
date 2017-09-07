package dynamicscripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rts.GameState;
import rts.units.Unit;

public class AuxMethods {

	public AuxMethods(){
		
	}
	
	public int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
	
	public List<Unit> units1(int player, GameState gs){
		List<Unit> playerUnits = new ArrayList<>();
		for (Unit u : gs.getUnits()) {
			if (u.getPlayer() == player)
				playerUnits.add(u);
		}
		return playerUnits;
	}
	public double NormalizeInRangue(double currentValue, int space, double dislocate)
	{
		double newValue=(currentValue/space)+dislocate;
		return newValue;
	}
	public boolean validationConstraintRule(int i, int j, int k, RulesSpace rulesSpace)
	{
		if(j==rulesSpace.getAction_moveawayof() && k==rulesSpace.getParamether_closestEnemyNotAssigned())
		{return false;
		}
		
		return false;
	}

}
