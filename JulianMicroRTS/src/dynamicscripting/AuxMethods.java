package dynamicscripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		{
			return false;
		}
		if(j==rulesSpace.getAction_moveawayof() && k==rulesSpace.getParamether_fartestEnemy())
		{
			return false;
		}
		if(i==rulesSpace.getCondition_enemyPointingRange1() && k!=rulesSpace.getParamether_closestEnemy())
		{
			return false;
		}
		if(i==rulesSpace.getCondition_enemyInsideRange1() && k!=rulesSpace.getParamether_closestEnemy())
		{
			return false;
		}
		if(i==rulesSpace.getCondition_enemyPointingRange2() && k!=rulesSpace.getParamether_closestEnemy())
		{
			return false;
		}
		if(i==rulesSpace.getCondition_enemyInsideRange2() && k!=rulesSpace.getParamether_closestEnemy())
		{
			return false;
		}
		if(i==rulesSpace.getCondition_enemyPointingRange3() && k!=rulesSpace.getParamether_closestEnemy())
		{
			return false;
		}
		if(i==rulesSpace.getCondition_enemyInsideRange3() && k!=rulesSpace.getParamether_closestEnemy())
		{
			return false;
		}
		return true;
	}
	
	public void orderInReverseArraylist(ArrayList<Rule> l)
	{
    	Collections.sort(l, new Comparator<Rule>() {
    	    @Override
    	    public int compare(Rule o1, Rule o2) {
    	    	Integer w=new Integer(o2.getWeight()); 
    	        return w.compareTo(o1.getWeight());
    	    }
    	});
	}

}
