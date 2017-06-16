package DynamicScripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PlayerAction;
import rts.PlayerActionGenerator;
import rts.units.UnitTypeTable;

public class DynamicScripting extends AIWithComputationBudget {
    UnitTypeTable m_utt = null;
    ArrayList <Rule> rulesSpaceList;
    private ArrayList<Rule> rulesSelectedList;
    RulesSpace objRulesSpace= new RulesSpace();
    private int totalRules;
    private ScriptGeneration actualScript; 

    // This is the default constructor that microRTS will call:
    public DynamicScripting(UnitTypeTable utt) {
        super(-1,-1);
        rulesSpaceList=new ArrayList <Rule> ();
        rulesGeneration();        
        actualScript=new ScriptGeneration(totalRules,rulesSpaceList);
        rulesSelectedList=actualScript.selectionRules();
        m_utt = utt;
        
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
        try {
            if (!gs.canExecuteAnyAction(player)) return new PlayerAction();
            PlayerActionGenerator pag = new PlayerActionGenerator(gs, player);
            return pag.getRandom();
        }catch(Exception e) {
            // The only way the player action generator returns an exception is if there are no units that
            // can execute actions, in this case, just return an empty action:
            // However, this should never happen, since we are checking for this at the beginning
            return new PlayerAction();
        }
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
    	while(rulesSelectedList.size()>0)
    	{
    		Rule rule=rulesSelectedList.get(0);
    		if(rule.getActive()==true 
    				&& rule.getRule_condition()==objRulesSpace.getCondition_enemyInsideRange()
    				&& rule.getRule_action()==objRulesSpace.getAction_attack()
    				&& rule.getRule_paramether()== objRulesSpace.getParamether_closestEnemy())
    		{
    			
    		}
    	}
    	
    }
    
}