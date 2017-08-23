package dynamicscripting;

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
    HashMap<Unit, ArrayList<Rule>> RulesSpaceUnit = null;
    HashMap<Unit, ArrayList<Rule>> RulesSelectedUnit = null;        
    private RulesSpace rulesSpace= new RulesSpace();
    private int totalRules;
    private ConditionsScripts conditionsScripts;
    private ParametersScripts parametersScripts;
    UnitScript attackTo;
    UnitScript moveAwayTo;
    UnitScript moveTo;


    // This is the default constructor that microRTS will call:
    public DynamicScripting(UnitTypeTable utt) {
        this(utt,-1,-1,new AStarPathFinding());       
    }
    
    public DynamicScripting(UnitTypeTable utt, int time, int max_playouts, PathFinding a_pf) {
        super(time,max_playouts);
        m_utt = utt;
        pf=a_pf;                
        
        attackTo = new UnitScriptAttackTo(pf);
        moveAwayTo=new UnitScriptMoveAwayTo(pf);
        moveTo=new UnitScriptMoveTo(pf);
        
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
        List<Unit> enemyUnits = new ArrayList<>();

        for(Unit u:gs.getUnits()) {
            if (u.getPlayer()==player) playerUnits.add(u);
            else if (u.getPlayer()>=0) enemyUnits.add(u);
        }
        int n1 = playerUnits.size();
        int n2 = enemyUnits.size();
        
        RulesSpaceUnit=new HashMap<>();
        RulesSelectedUnit=new HashMap<>();
        generationRulesSpaces(playerUnits, n1);
        selectionRulesForUnits(playerUnits,n1);
        
        parametersScripts=new ParametersScripts(rulesSpace);
        conditionsScripts=new ConditionsScripts(rulesSpace,parametersScripts,gs);
        
        for(int i = 0;i<n1;i++) {
            Unit u = playerUnits.get(i);
            if (gs.getUnitAction(u)==null) {
            	
            ArrayList<Rule> rulesSelected=RulesSelectedUnit.get(u);
            for(int j=0;j<rulesSelected.size();j++)
            {    		
            	Rule rule=rulesSelected.get(j);

            	if(conditionsScripts.validationCondition(rulesSelected.get(j).getRule_condition(), rulesSelected.get(j).getRule_paramether(),u))
            	{
            		
            		Unit u2 = parametersScripts.validationParameter(u, gs, rulesSelected.get(j).getRule_paramether());
            		if(rulesSelected.get(j).getRule_action()==rulesSpace.getAction_attack())
            		{
            			System.out.println("action Attack "+rulesSelected.get(j).getRule_paramether());
            			UnitScript s=attackTo.instantiate(u, gs, u2);
            			UnitAction ua = s.getAction(u, gs);
            			pa.addUnitAction(u, ua);
            			break;
            		}
            		else if(rulesSelected.get(j).getRule_action()==rulesSpace.getAction_moveawayof())
            		{
            			System.out.println("action move Away "+rulesSelected.get(j).getRule_paramether());
            			UnitScript s=moveAwayTo.instantiate(u, gs, u2);
            			UnitAction ua = s.getAction(u, gs);
            			pa.addUnitAction(u, ua);
            			break;
            		}
            		else if(rulesSelected.get(j).getRule_action()==rulesSpace.getAction_moveto())
            		{
            			System.out.println("action move "+rulesSelected.get(j).getRule_paramether());
            			UnitScript s=moveTo.instantiate(u, gs, u2);
            			UnitAction ua = s.getAction(u, gs);
            			pa.addUnitAction(u, ua);
            			break;
            		}
            		
            		
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
    public ArrayList <Rule> rulesGeneration()
    {
    	ArrayList <Rule> rulesSpaceList=new ArrayList <Rule> ();
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
    	return rulesSpaceList;
    }
    
    private void generationRulesSpaces(List<Unit> playerUnits, int n1)
    {
    	for(int i = 0;i<n1;i++) {
            Unit u = playerUnits.get(i);
            ArrayList <Rule> rulesSpaceList=rulesGeneration();
            RulesSpaceUnit.put(u, rulesSpaceList);
    	}    	
    }
    
    public void selectionRulesForUnits(List<Unit> playerUnits, int n1)
    {
    	for(int i = 0;i<n1;i++) {
            Unit u = playerUnits.get(i);
            ScriptGeneration actualScript=new ScriptGeneration(totalRules,RulesSpaceUnit.get(u));
            ArrayList<Rule> rulesSelectedList=actualScript.selectionRules();
            RulesSelectedUnit.put(u, rulesSelectedList);
            
    	}
    }
    
}