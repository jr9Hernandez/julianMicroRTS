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
	
	public void orderInReverseArraylistRule(ArrayList<Rule> l)
	{
    	Collections.sort(l, new Comparator<Rule>() {
    	    @Override
    	    public int compare(Rule o1, Rule o2) {
    	    	Integer w=new Integer(o2.getWeight()); 
    	        return w.compareTo(o1.getWeight());
    	    }
    	});
	}
	public void orderInReverseArraylistCompoundScript(ArrayList<CompoundScript> l)
	{
    	Collections.sort(l, new Comparator<CompoundScript>() {
    	    @Override
    	    public int compare(CompoundScript o1, CompoundScript o2) {
    	    	Integer w=new Integer(o2.getGlobalValue()); 
    	        return w.compareTo(o1.getGlobalValue());
    	    }
    	});
	}
	public void includeInBestScripts(int calculateAdjustment, ArrayList<CompoundScript> bestCompoundScript, CompoundScript candidate, int limitScripts)
	{		
		if(bestCompoundScript.size()>=limitScripts)
		{
			int min = bestCompoundScript.get(0).getGlobalValue();
			CompoundScript bc=bestCompoundScript.get(0);
			int index=0;
			for(int i = 0 ; i < bestCompoundScript.size(); i++)
			{
				if(bestCompoundScript.get(i).getGlobalValue() < min){
					min = bestCompoundScript.get(i).getGlobalValue();
					bc= bestCompoundScript.get(i);
					index=i;
				}
			}
			if(candidate.getGlobalValue() > min){
				if(validateDuplicate(bestCompoundScript,candidate))
					bestCompoundScript.set(index ,candidate);
			}

		}
		else
		{
			if(validateDuplicate(bestCompoundScript,candidate))
				bestCompoundScript.add(candidate);
		}
	}
	public boolean validateDuplicate(ArrayList<CompoundScript> bestCompoundScript, CompoundScript candidate)
	{
		
		for(int i = 0 ; i < bestCompoundScript.size(); i++)
		{
			int different=0;
			if(bestCompoundScript.get(i).getCompoundScript().size() == candidate.getCompoundScript().size())
			{
				for(int j=0; j<bestCompoundScript.get(i).getCompoundScript().size();j++)
				{
					if((bestCompoundScript.get(i).getCompoundScript().get(j).getRule_condition()!=candidate.getCompoundScript().get(j).getRule_condition())&&(bestCompoundScript.get(i).getCompoundScript().get(j).getRule_action()!=candidate.getCompoundScript().get(j).getRule_action())&&(bestCompoundScript.get(i).getCompoundScript().get(j).getRule_paramether()!=candidate.getCompoundScript().get(j).getRule_paramether()))
					{
						different++;
					}
				}
				if(different==0)
				{
					return false;
				}
				
			}
			else
			{
				return true;
			}
		}
		return true;
	}

}
