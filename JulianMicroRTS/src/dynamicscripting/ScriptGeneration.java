package dynamicscripting;

import java.util.ArrayList;
import java.util.List;

public class ScriptGeneration {

	private final int scriptSize=1;
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
				if(lineAdded==true)
				{
					rulesSelectedList.add(ruleSpaceList.get(selected));
					atLeastOneAdded=true;
				}
				trySelection++;
			}
		}
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
	
	public ArrayList<Rule> UpdateWeightsBeta(ArrayList<Rule> rulesSelectedList, ArrayList<Rule> ruleSpaceList, int fitness, int wInit)
	{
		int wMax=wInit;
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
			return rulesSelectedList;
		}
		int nonActive=totalRules-active;
		int adjustment=fitness;
		int compensation= -active*adjustment/nonActive;
		int remainder=0;
		
		for(int i=0;i<totalRules;i++)
		{
			if(!insertInScript(ruleSpaceList.get(i),rulesSelectedList))
			{
				ruleSpaceList.get(i).setWeight(ruleSpaceList.get(i).getWeight()+adjustment);
			}
			else
			{
				ruleSpaceList.get(i).setWeight((ruleSpaceList.get(i).getWeight()+compensation));
			}
//			if(ruleSpaceList.get(i).getWeight()<wMin)
//			{
//				remainder=remainder+(ruleSpaceList.get(i).getWeight()-wMin);
//				ruleSpaceList.get(i).setWeight(wMin);	
//			}
//			else if(ruleSpaceList.get(i).getWeight()>wMax)
//			{
//				remainder=remainder+(ruleSpaceList.get(i).getWeight()-wMax);
//				ruleSpaceList.get(i).setWeight(wMax);	
//			}
		}
		ruleSpaceList=distributeRemainder(wMax,ruleSpaceList);
		return ruleSpaceList;
	}
	
	public ArrayList<Rule> distributeRemainder(int totalWeights,ArrayList<Rule> ruleSpaceList)
	{
		
		
		int totalWeightsCurrent=0;
		for(int i=0;i<totalRules;i++)
		{	
			totalWeightsCurrent=totalWeightsCurrent+ruleSpaceList.get(i).getWeight();
		}
	
		double difference=Math.abs(totalWeights-totalWeightsCurrent);
		double fractiontoDistribute=difference/(double)totalRules;
		for(int i=0;i<totalRules;i++)
		{	
			if(totalWeightsCurrent<totalWeights)
			{
				ruleSpaceList.get(i).setWeight(ruleSpaceList.get(i).getWeight()+(int)(fractiontoDistribute+0.5));
			}
			else
			{
				ruleSpaceList.get(i).setWeight(ruleSpaceList.get(i).getWeight()-(int)(fractiontoDistribute+0.5));
			}
		}
		return ruleSpaceList;
	}
}
