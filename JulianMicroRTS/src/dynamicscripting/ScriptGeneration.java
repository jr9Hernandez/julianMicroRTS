package dynamicscripting;

import java.util.ArrayList;
import java.util.List;

import rts.units.Unit;

public class ScriptGeneration {

	private final int scriptSize=2;
	private int maxTries=5;
	
	
	private int totalRules;
	private boolean lineAdded;
	private ArrayList<Rule> ruleSpaceList;
	private ArrayList<Rule> rulesSelectedList;
	private AuxMethods objAuxMethods;
	private boolean atLeastOneAdded=false;
	
	public ScriptGeneration(int totalRules, ArrayList<Rule> rulesSpaceList)
	{
		
		this.totalRules=totalRules;
		lineAdded=false;
		this.ruleSpaceList=rulesSpaceList;
		rulesSelectedList=new ArrayList<Rule>();
		objAuxMethods=new AuxMethods();
		desactivateRules();
		
	}
	
	public ScriptGeneration(int totalRules)
	{
		
		this.totalRules=totalRules;
	}
	public void desactivateRules()
	{
		for(int i=0;i<ruleSpaceList.size();i++)
		{
			Rule currentRule=ruleSpaceList.get(i);
			for (int j=0;j<currentRule.getActive().length;j++)
			{
				currentRule.active[j]=false;
			}
		}
	}
	/**
	 * SelectionRules() is based in the algorithm for script generation presented in Adaptive game AI with dynamic scripting
	 * from Pieter Spronck · Marc Ponsen ·Ida Sprinkhuizen-Kuyper · Eric Postma 
	 */
	public ArrayList<Rule> selectionRules()
	{
		
		int sumWeights=0;
		for(int i=0;i<totalRules;i++)
		{
			sumWeights=sumWeights+ruleSpaceList.get(i).getWeight();
			
//			if(sumWeights<0)
//			{
//				sumWeights=0;
//			}
		}
//		System.out.println("sumWeights "+sumWeights);
		for(int i=0; i<scriptSize;i++)
		{
			int trySelection=0;
			boolean lineAdded=false;
			while(trySelection<maxTries && lineAdded==false)
			{
				int sum=0;
				int j=0;
				int selected=-1;
				int fraction=objAuxMethods.randomNumberInRange(0, sumWeights);
				while(selected<0 && j<totalRules)
				{
					sum=sum+(ruleSpaceList.get(j).getWeight());
					if(sum>fraction)
					{
						selected=j;
					}
					else
					{
						j=j+1;
					}
				}
				if(selected!=-1)
				{
					
					lineAdded=insertInScript(ruleSpaceList.get(selected),rulesSelectedList);
					
				}
				if(lineAdded)
				{
					rulesSelectedList.add(ruleSpaceList.get(selected));
					atLeastOneAdded=true;

				}
				trySelection++;
			}
		}
		//This represents the finish Script Function ()
		if(!atLeastOneAdded)
		{
			int r=objAuxMethods.randomNumberInRange(0, totalRules-1);
			rulesSelectedList.add(ruleSpaceList.get(r));
		}
		return rulesSelectedList;
	}
	
	public ArrayList<Rule> selectionFinalRules()
	{
		ArrayList<Rule> rulesSelectedFinalList=new ArrayList<Rule>();
		Rule best = null;
	    int bestWeight = 0;
	    
		for(int i=0; i<scriptSize;i++)
		{
			for(int j=0;j<ruleSpaceList.size();j++)
			{
				Rule currentRule=ruleSpaceList.get(j);
				if((best==null || currentRule.getWeight()>bestWeight) && !rulesSelectedFinalList.contains(currentRule))
				{
					best=currentRule;
					bestWeight=currentRule.getWeight();
				}
			}
			rulesSelectedFinalList.add(best);
			
		}

		return rulesSelectedFinalList;
	}
	
	public boolean insertInScript(Rule selectedRule, ArrayList<Rule> rulesSelectedList)
	{
	    for (Rule rule : rulesSelectedList) {
	        if (rule.getRule_id() == selectedRule.getRule_id()) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public int UpdateWeightsBeta(int unitId,ArrayList<Rule> rulesSelectedList, ArrayList<Rule> ruleSpaceList, double globalEvaluation, int wInit, double teamFactor, double bFactor, double cFactor, double aFactor)
	{
		//System.out.println("Rule Before! "+rulesSelectedList.get(0).getWeight());
		int wMax=650;
		int wMin=0;
		int active=0;
		int totalWeights=0;
		for(int i=0;i<totalRules;i++)
		{	
			totalWeights=totalWeights+ruleSpaceList.get(i).getWeight();
			//System.out.println("political "+ruleSpaceList.get(i).active[unitId]);
			if(ruleSpaceList.get(i).active[unitId])
			{	
				active=active+1;
			}
		}
		if(active<=0 || active>=totalRules)
		{
			return -1;
		}
		int nonActive=totalRules-active;
		int adjustment=calculateAdjustment(globalEvaluation,teamFactor,bFactor,cFactor,aFactor);
		int compensation= -active*adjustment/nonActive;
		int remainder=-active*adjustment-nonActive*compensation;
		
		for(int i=0;i<totalRules;i++)
		{
			Rule currentRule=ruleSpaceList.get(i);
			if(ruleSpaceList.get(i).active[unitId])
			{
				currentRule.setWeight(currentRule.getWeight()+adjustment);
			}
			else
			{
				currentRule.setWeight(currentRule.getWeight()+compensation);
			}
			if(currentRule.getWeight()<wMin)
			{
				remainder=remainder+(currentRule.getWeight()-wMin);
				currentRule.setWeight(wMin);	
			}
			else if(currentRule.getWeight()>wMax)
			{
				remainder=remainder+(currentRule.getWeight()-wMax);
				currentRule.setWeight(wMax);	
			}
		}
		distributeRemainder(ruleSpaceList,remainder,wMax,wMin);
		//System.out.println("Rule Right Now! "+rulesSelectedList.get(0).getWeight());
		return adjustment;
	}
	
	public void distributeRemainder(ArrayList<Rule> ruleSpaceList, int remainder,int maxWeight, int minWeight)
	{
		
		int i=0;
		while(remainder>0)
		{
			Rule currentRule=ruleSpaceList.get(i);
			if(currentRule.getWeight()<=maxWeight-1)
			{
				currentRule.setWeight(currentRule.getWeight()+1);
				remainder=remainder-1;
			}
			i=(i+1)%totalRules;
		}
		while(remainder<0)
		{
			Rule currentRule=ruleSpaceList.get(i);
			if(currentRule.getWeight()>=minWeight+1)
			{
				currentRule.setWeight(currentRule.getWeight()-1);
				remainder=remainder+1;
			}
			i=(i+1)%totalRules;
		}

	}
	
	public int calculateAdjustment(double globalEvaluation,double teamFactor, double bFactor, double cFactor, double aFactor)
	{
		double Rmax=100;
		double Pmax=70;
		double bValue=0.3;
		double differenceWeight;
		
		double fitness=(0.1)*(3*teamFactor+0*aFactor+0*bFactor+7*cFactor);
//		System.out.println("fitness "+fitness);
		if(fitness<bValue)
		{
			differenceWeight=-(Pmax*((bValue-fitness)/bValue));
		}
		else
		{
			differenceWeight=(Rmax*((fitness-bValue)/(1-bValue)));
		}
		
//		System.out.println("differenceWeight "+differenceWeight);
		return (int)(differenceWeight);
	}
}
