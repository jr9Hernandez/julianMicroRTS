package dynamicscripting;

import java.util.ArrayList;
import java.util.List;

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
	}
	
	public ScriptGeneration(int totalRules)
	{
		
		this.totalRules=totalRules;
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
	
	public boolean insertInScript(Rule selectedRule, ArrayList<Rule> rulesSelectedList)
	{
	    for (Rule rule : rulesSelectedList) {
	        if (rule.getRule_id() == selectedRule.getRule_id()) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public void UpdateWeightsBeta(ArrayList<Rule> rulesSelectedList, ArrayList<Rule> ruleSpaceList, double globalEvaluation, int wInit, double teamFactor, double bFactor, double cFactor, double aFactor)
	{
		//System.out.println("Rule Before! "+rulesSelectedList.get(0).getWeight());
		int wMax=2000;
		int wMin=0;
		int active=0;
		int totalWeights=0;
		for(int i=0;i<totalRules;i++)
		{	
			totalWeights=totalWeights+ruleSpaceList.get(i).getWeight();
			if(!insertInScript(ruleSpaceList.get(i),rulesSelectedList))
			{	
				active=active+1;
			}
		}
		if(active<=0 || active>=totalRules)
		{
			return;
		}
		int nonActive=totalRules-active;
		int adjustment=calculateAdjustment(globalEvaluation,teamFactor,bFactor,cFactor,aFactor);
		int compensation= -active*adjustment/nonActive;
		int remainder=-active*adjustment-nonActive*compensation;
		
		for(int i=0;i<totalRules;i++)
		{
			Rule currentRule=ruleSpaceList.get(i);
			if(!insertInScript(currentRule,rulesSelectedList))
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
		double Rmax=150;
		double Pmax=40;
		double bValue=0.1;
		double differenceWeight;
		
		double fitness=(0.1)*(5*globalEvaluation+0*aFactor+0*bFactor+5*cFactor);
		//System.out.println("cali "+fitness);
		if(fitness<bValue)
		{
			differenceWeight=-(Pmax*((bValue-fitness)/bValue));
		}
		else
		{
			differenceWeight=(Rmax*((fitness-bValue)/(1-bValue)));
		}
		
		//System.out.println("differenceWeight "+differenceWeight);
		return (int)(differenceWeight);
	}
}
