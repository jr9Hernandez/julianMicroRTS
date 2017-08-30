package dynamicscripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ai.abstraction.Attack;
import ai.abstraction.WorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.portfolio.PortfolioAI;
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
	
	HashMap<String, ArrayList<Rule>> RulesSpaceUnit = new HashMap<>();;
	HashMap<Integer, ArrayList<Rule>> RulesSelectedUnit = new HashMap<>();
	HashMap<Unit, Integer> UnitsTimeDeath = new HashMap<>();
	private RulesSpace rulesSpace = new RulesSpace();
	private int totalRules;
	private ConditionsScripts conditionsScripts;
	private ParametersScripts parametersScripts;
	UnitScript attackTo;
	UnitScript moveAwayTo;
	UnitScript moveTo;
	boolean firstExecution=true;
	boolean isPlayout=true;
	boolean firstAll=true;
	AuxMethods aux=new AuxMethods();
	UnitStatistics unitStatistics;
	int nplayouts = 0;
	int LOOKAHEAD = 1000;
	EvaluationFunction evaluation = null;
	int initialWeight=100;
	
	int numTypesUnits;
	String [] typesUnits;
	

	// This is the default constructor that microRTS will call:
	public DynamicScripting(UnitTypeTable utt) {
		this(utt, -1, -1, new AStarPathFinding(), new SimpleSqrtEvaluationFunction3());
	}

	public DynamicScripting(UnitTypeTable utt, int time, int max_playouts, PathFinding a_pf, EvaluationFunction e) {
		super(time, max_playouts);
		m_utt = utt;
		pf = a_pf;
		evaluation=e;

		attackTo = new UnitScriptAttackTo(pf);
		moveAwayTo = new UnitScriptMoveAwayTo(pf);
		//moveTo = new UnitScriptMoveTo(pf);
		
		numTypesUnits=4;
		typesUnits=new String[numTypesUnits];
		typesUnits[0]="Worker";
		typesUnits[1]="Light";
		typesUnits[2]="Heavy";
		typesUnits[3]="Ranged";

	}

	// This will be called by microRTS when it wants to create new instances of this
	// bot (e.g., to play multiple games).
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
		//pa.fillWithNones(gs, player, 10);

		// Here I have to assig an action for each unit!
		if(isPlayout)
		{
			try {
				for(int i=0;i<1000;i++)
				{
					playout(player, gs);
				}				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			firstExecution=true;
			isPlayout=false;
		}
		else
		{
			pa = ActionsAssignments(player, gs);
		}
		return pa;
	}

	// This will be called by the microRTS GUI to get the
	// list of parameters that this bot wants exposed
	// in the GUI.
	public List<ParameterSpecification> getParameters() {
		return new ArrayList<>();
	}

	// This method will create the space of rules
	public ArrayList<Rule> rulesGeneration() {
		ArrayList<Rule> rulesSpaceList = new ArrayList<Rule>();
		totalRules = rulesSpace.getNumberConditions() * rulesSpace.getNumberParamethers()
				* rulesSpace.getNumberActions();
		int counterId = 0;
		for (int i = 0; i < rulesSpace.getNumberConditions(); i++) {
			for (int j = 0; j < rulesSpace.getNumberActions(); j++) {
				for (int k = 0; k < rulesSpace.getNumberParamethers(); k++) {
					Rule rule = new Rule(counterId, initialWeight, false, i, j, k);
					counterId++;
					rulesSpaceList.add(rule);

				}
			}
		}

		// code for print the actual arraylist of objects
		for (Rule rule : rulesSpaceList) {
			System.out.println(rule.getRule_id() + " " + rule.getWeight() + " " + rule.getActive() + " "
					+ rule.getRule_condition() + " " + rule.getRule_action() + " " + rule.getRule_paramether());
		}
		return rulesSpaceList;
	}

	private void generationRulesSpaces(int n1) {
		for (int i = 0; i < numTypesUnits; i++) {
			//Unit u = playerUnits.get(i);
			ArrayList<Rule> rulesSpaceList = rulesGeneration();
			RulesSpaceUnit.put(typesUnits[i], rulesSpaceList);
		}
	}

	public void selectionRulesForUnits(int n1,List<Unit> playerUnits) {
		for (int i = 0; i < n1; i++) {
			Unit u = playerUnits.get(i);
			ScriptGeneration actualScript = new ScriptGeneration(totalRules, RulesSpaceUnit.get(u.getType().name));
			ArrayList<Rule> rulesSelectedList = actualScript.selectionRules();
			RulesSelectedUnit.put(i, rulesSelectedList);

		}
	}
	
	public PlayerAction ActionsAssignments(int player, GameState gs)
	{
		PlayerAction pa = new PlayerAction();
		pa.fillWithNones(gs, player, 10);
		
		List<Unit> playerUnits = aux.units1(player,gs);
		int n1=playerUnits.size();
		
		if(firstAll)
		{
			generationRulesSpaces(n1);
			firstAll=false;
		}
		
		if(isPlayout && firstExecution)
		{
			RulesSelectedUnit.clear();
			selectionRulesForUnits(n1,playerUnits);
			firstExecution=false;
		}
		
		else if(!isPlayout && firstExecution)
		{		
			RulesSelectedUnit.clear();
			selectionRulesForUnits(n1,playerUnits);
			firstExecution=false;
			
			for (int i = 0; i < n1; i++) {
				System.out.println("Rule selected "+i+" "+RulesSelectedUnit.get(i).get(0).getRule_condition()+RulesSelectedUnit.get(i).get(0).getRule_action()+RulesSelectedUnit.get(i).get(0).getRule_paramether());
//				System.out.println("Rule selected "+i+" "+RulesSelectedUnit.get(i).get(1).getRule_condition()+RulesSelectedUnit.get(i).get(1).getRule_action()+RulesSelectedUnit.get(i).get(1).getRule_paramether());
			}
		}
		
		parametersScripts = new ParametersScripts(rulesSpace);
		conditionsScripts = new ConditionsScripts(rulesSpace, parametersScripts, gs);

		for (int i = 0; i < n1; i++) {
			Unit u = playerUnits.get(i);
			boolean ruleApplied=false;
			if (gs.getUnitAction(u) == null) {

				ArrayList<Rule> rulesSelected = RulesSelectedUnit.get(i);

				for (int j = 0; j < rulesSelected.size(); j++) {
					Rule currentRule = rulesSelected.get(j);

					if (conditionsScripts.validationCondition(currentRule.getRule_condition(),
							currentRule.getRule_paramether(), u)) {

						Unit u2 = parametersScripts.validationParameter(u, gs,currentRule.getRule_paramether());
						if (currentRule.getRule_action() == rulesSpace.getAction_attack()) {
							//System.out.println("action Attack " + rulesSelected.get(j).getRule_paramether());
							UnitScript s = attackTo.instantiate(u, gs, u2);
							UnitAction ua = s.getAction(u, gs);
							pa.addUnitAction(u, ua);
							ruleApplied=true;
							break;
							
						} else if (currentRule.getRule_action() == rulesSpace.getAction_moveawayof()) {
							//System.out.println("action move Away " + rulesSelected.get(j).getRule_paramether());
							UnitScript s = moveAwayTo.instantiate(u, gs, u2);
							UnitAction ua = s.getAction(u, gs);
							pa.addUnitAction(u, ua);
							ruleApplied=true;
							break;
						}	
//						} else if (rulesSelected.get(j).getRule_action() == rulesSpace.getAction_moveto()) {
//							System.out.println("action move " + rulesSelected.get(j).getRule_paramether());
//							UnitScript s = moveTo.instantiate(u, gs, u2);
//							UnitAction ua = s.getAction(u, gs);
//							pa.addUnitAction(u, ua);
//							break;
//							
//						}

					}

				}
			}
		}
		
		return pa;
	}
	
	public void playout(int player, GameState gs) throws Exception {
		// if (DEBUG>=1) System.out.println(" playout... " + LOOKAHEAD);
		nplayouts++;
		firstExecution=true;
		GameState gs2 = gs.clone();
		AI ai1 = new WorkerRush(m_utt, new BFSPathFinding());
		int timeLimit = gs2.getTime() + LOOKAHEAD;
		boolean gameover = false;
		
		List<Unit> playerUnitsg2 = aux.units1(player,gs2);
		List<Unit> playerUnitsEnemyg2 = aux.units1(player-1,gs2);
		unitStatistics=new UnitStatistics(player, player-1, gs2, playerUnitsg2, playerUnitsEnemyg2);
		int [] timeDeath=new int[playerUnitsg2.size()];
		
		for(int i=0;i<timeDeath.length;i++)
		{
			timeDeath[i]=-1;
		}
		
		while (!gameover && gs2.getTime()<timeLimit) {
			if (gs2.isComplete()) {
				gameover = gs2.cycle();					
			}else {
				 
				PlayerAction pa = ai1.getAction(player-1, gs2);
				gs2.issue(pa);
				
				PlayerAction pa2 = ActionsAssignments(player, gs2);
				gs2.issue(pa2);
				
				timeDeath=unitStatistics.aFactor(timeDeath);
			}
		}	
		timeDeath=unitStatistics.aFactor(timeDeath);

		//From Here the parameter for adjustment
		double globalEvaluation = evaluation.evaluate(player, 1 - player, gs2);
		globalEvaluation=aux.NormalizeInRangue(globalEvaluation,2,0.5);
		System.out.println(" done: " + globalEvaluation);
		
		double teamFactor=unitStatistics.teamFactor();
		System.out.println("teamFactor "+teamFactor);
		
		double bFactor=unitStatistics.bFactor();
		System.out.println("bFactor "+bFactor);
		
		double cFactor=unitStatistics.cFactor();
		System.out.println("cFactor "+cFactor);
		
//		for (int i = 0; i < playerUnitsg2.size(); i++) {
//			System.out.println("unit "+timeDeath[i]+ " "+playerUnitsg2.get(i).getType().name);
//		}
		
		//Here we are updating
		ScriptGeneration actualScript = new ScriptGeneration(totalRules); 
		List<Unit> playerUnits = aux.units1(player,gs);
		int n1=playerUnits.size();
		for (int i = 0; i < n1; i++) {
			Unit u = playerUnits.get(i);
			actualScript.UpdateWeightsBeta(RulesSelectedUnit.get(i), RulesSpaceUnit.get(u.getType().name), globalEvaluation ,initialWeight, teamFactor,bFactor,cFactor);
		}
	}

}