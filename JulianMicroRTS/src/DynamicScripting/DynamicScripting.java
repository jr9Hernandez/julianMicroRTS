package DynamicScripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import rts.GameState;
import rts.PlayerAction;
import rts.PlayerActionGenerator;
import rts.units.UnitTypeTable;

public class DynamicScripting extends AIWithComputationBudget {
	
    UnitTypeTable m_utt = null;
    
    private ArrayList <Rule> rulesSpaceList=new ArrayList <Rule> ();;
    private ArrayList<Rule> rulesSelectedList;
    private RulesSpace objRulesSpace= new RulesSpace();
    private int totalRules;
    private ScriptGeneration actualScript; 
    private RulesScripts rulesScripts=new RulesScripts();


    // This is the default constructor that microRTS will call:
    public DynamicScripting(UnitTypeTable utt) {
        this(utt,-1,-1);       
    }
    
    public DynamicScripting(UnitTypeTable utt, int time, int max_playouts) {
        super(time,max_playouts);
        m_utt = utt;

        rulesGeneration();        
        actualScript=new ScriptGeneration(totalRules,rulesSpaceList);
        rulesSelectedList=actualScript.selectionRules();
    }

    // This will be called by microRTS when it wants to create new instances of this bot (e.g., to play multiple games).
    public AI clone() {
        return new DynamicScripting(m_utt);
    }
    
    // This will be called once at the beginning of each new game:    
    public void reset() {
    }
       
    // Called by microRTS at each game cycle.
    // Returns the action the bot wants to execute.
    public PlayerAction getAction(int player, GameState gs) {
        PlayerAction pa = new PlayerAction();
        pa.fillWithNones(gs, player, 10);
        //Here I have to assig an action for each unit!, calling the scriptRun Metthod
        return pa;
    }   
    
    // This will be called by the microRTS GUI to get the
    // list of parameters that this bot wants exposed
    // in the GUI.
    public List<ParameterSpecification> getParameters()
    {
        return new ArrayList<>();
    }
    
    //This method will create the space of rules
    public void rulesGeneration()
    {
    	
    	totalRules=objRulesSpace.getNumberConditions()*objRulesSpace.getNumberParamethers()*objRulesSpace.getNumberActions();
    	int counterId=0;
    	for(int i=0;i<objRulesSpace.getNumberConditions();i++)
    	{
    		for(int j=0;j<objRulesSpace.getNumberActions();j++)
    		{
    			for(int k=0;k<objRulesSpace.getNumberParamethers();k++)
    			{
    				Rule rule=new Rule(counterId,100, false, i,j,k);
    				counterId++;
    				rulesSpaceList.add(rule);
    				
    			}
    		}
    	}
    	
    	//code for print the actual arraylist of objects
        for(Rule rule : rulesSpaceList) {
            System.out.println(rule.getRule_id()+" "+rule.getWeight()+" "+rule.getActive()+" "+rule.getRule_condition()+" "+rule.getRule_action()+" "+rule.getRule_paramether());
        }
    	
    }

    public void ScriptRun()
    {
    	for(int i=0;i<rulesSelectedList.size();i++)
    	{    		
    		Rule rule=rulesSelectedList.get(i);
    		if(rule.getRule_action()==objRulesSpace.getAction_attack())
    		{
    			rulesScripts.attack(rule.getRule_condition(), rule.getRule_paramether());
    		}
    		else if(rule.getRule_action()==objRulesSpace.getAction_moveawayof())
    		{
    			
    		}
//    		else if(rule.getRule_action()==objRulesSpace.getAction_moveawayof())
//    		{
//    				//rulesScripts.action	
//    		}
//    		else if(rule.getRule_action()==objRulesSpace.getAction_cluster())
//    		{
//    			//rulesScripts.action
//    		}
    	}
    	
    }
    
}