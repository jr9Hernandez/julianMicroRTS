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
	
	public ScriptGeneration(int totalRules, ArrayList<Rule> rulesSpaceList)
	{
		
		this.totalRules=totalRules;
		lineAdded=false;
		this.ruleSpaceList=rulesSpaceList;
		rulesSelectedList=new ArrayList<Rule>();
		objAuxMethods=new AuxMethods();
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
					lineAdded=insertInScript(ruleSpaceList.get(selected));
				}
				if(lineAdded==true)
				{
					rulesSelectedList.add(ruleSpaceList.get(selected));
				}
				trySelection++;
			}
		}
		return rulesSelectedList;
	}
	
	public boolean insertInScript(Rule selectedRule)
	{
	    for (Rule rule : rulesSelectedList) {
	        if (rule.getRule_id() == selectedRule.getRule_id()) {
	            return false;
	        }
	    }
	    return true;
	}
}
