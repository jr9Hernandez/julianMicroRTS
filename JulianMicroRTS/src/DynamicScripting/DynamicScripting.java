package DynamicScripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ai.abstraction.Attack;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import rts.GameState;
import rts.PlayerAction;
import rts.PlayerActionGenerator;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

public class DynamicScripting extends AIWithComputationBudget {
	
    UnitTypeTable m_utt = null;
    PathFinding pf;
    HashMap<UnitType, List<UnitScript>> scripts = null;
    
    private ArrayList <Rule> rulesSpaceList=new ArrayList <Rule> ();;
    private ArrayList<Rule> rulesSelectedList;
    private RulesSpace rulesSpace= new RulesSpace();
    private int totalRules;
    private ScriptGeneration actualScript; 
    private ConditionsScripts conditionsScripts;
    private ParametersScripts parametersScripts;
    UnitScript attack;


    // This is the default constructor that microRTS will call:
    public DynamicScripting(UnitTypeTable utt) {
        this(utt,-1,-1,new AStarPathFinding());       
    }
    
    public DynamicScripting(UnitTypeTable utt, int time, int max_playouts, PathFinding a_pf) {
        super(time,max_playouts);
        m_utt = utt;
        pf=a_pf;
        
        rulesGeneration();        
        actualScript=new ScriptGeneration(totalRules,rulesSpaceList);
        rulesSelectedList=actualScript.selectionRules();
        
        attack = new UnitScriptAttack(pf);
        
//        scripts = new HashMap<>();
//        {
//            List<UnitScript> l = new ArrayList<>();
//            //l.add(harvest);
//            //l.add(buildBarracks);
//            //l.add(buildBase);
//            l.add(attack);
//            //l.add(idle);
//            scripts.put(utt.getUnitType("Worker"),l);
//        }
//        {
//            List<UnitScript> l = new ArrayList<>();
//            scripts.put(utt.getUnitType("Base"),l);
//            l.add(trainWorker);
//            l.add(idle);
//        }
//        {
//            List<UnitScript> l = new ArrayList<>();
//            scripts.put(utt.getUnitType("Barracks"),l);
//            l.add(trainLight);
//            l.add(trainHeavy);
//            l.add(trainRanged);
//            l.add(idle);
//        }
//        {
//            List<UnitScript> l = new ArrayList<>();
//            scripts.put(utt.getUnitType("Light"),l);
//            l.add(attack);
//            l.add(idle);
//        }
//        {
//            List<UnitScript> l = new ArrayList<>();
//            scripts.put(utt.getUnitType("Heavy"),l);
//            l.add(attack);
//            l.add(idle);
//        }
//        {
//            List<UnitScript> l = new ArrayList<>();
//            scripts.put(utt.getUnitType("Ranged"),l);
//            l.add(attack);
//            l.add(idle);
//        }
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
        
        //Here I have to assig an action for each unit!
        
        List<Unit> playerUnits = new ArrayList<>();

        for(Unit u:gs.getUnits()) {
            if (u.getPlayer()==player) playerUnits.add(u);           
        }
        int n1 = playerUnits.size();
        
        parametersScripts=new ParametersScripts(rulesSpace);
        conditionsScripts=new ConditionsScripts(rulesSpace,parametersScripts,gs);
        
        for(int i = 0;i<n1;i++) {
            Unit u = playerUnits.get(i);
//          List<UnitScript> candidates = scripts.get(u.getType());
            if (gs.getUnitAction(u)==null) {
            for(int j=0;j<rulesSelectedList.size();j++)
            {    		
            	Rule rule=rulesSelectedList.get(j);

            	if(conditionsScripts.validationCondition(rulesSelectedList.get(j).getRule_condition(), rulesSelectedList.get(j).getRule_paramether(),u))
            	{
            		Unit u2 = parametersScripts.validationParameter(u, gs, rulesSelectedList.get(j).getRule_paramether());
            		attack=attack.instantiate(u, gs, u2);
            		UnitAction ua = attack.getAction(u, gs);
            		pa.addUnitAction(u, ua);
            		
            	}

            } 
            }
        }
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
    	
    	totalRules=rulesSpace.getNumberConditions()*rulesSpace.getNumberParamethers()*rulesSpace.getNumberActions();
    	int counterId=0;
    	for(int i=0;i<rulesSpace.getNumberConditions();i++)
    	{
    		for(int j=0;j<rulesSpace.getNumberActions();j++)
    		{
    			for(int k=0;k<rulesSpace.getNumberParamethers();k++)
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
    
}