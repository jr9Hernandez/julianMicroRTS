package dynamicscripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ai.RandomBiasedAI;
import ai.abstraction.Attack;
import ai.abstraction.LightRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.mlps.MLPSMCTS;
import ai.mcts.uct.UCT;
import ai.minimax.ABCD.ABCD;
import ai.montecarlo.MonteCarlo;
import ai.portfolio.PortfolioAI;
import ai.portfolio.portfoliogreedysearch.PGSAI;
import ai.puppet.PuppetSearchMCTS;
import rts.GameState;
import rts.PhysicalGameState;
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
	int LOOKAHEAD = 3000;
	EvaluationFunction evaluation = null;
	int initialWeight=100;
	Unit [] maxUnits;
	private ArrayList<Unit> unitsAssignedEnemys;
	int convergenceWin=0;
	int convergenceDraw=0;
	int roundConvergenceWin=-1;
	int roundConvergenceDraw=-1;
	int limitConvergence=8;
	int enemyIA;
	
	int numTypesUnits;
	String [] typesUnits;
	

	// This is the default constructor that microRTS will call:
	public DynamicScripting(UnitTypeTable utt, int enemy) {
		this(utt, -1, -1, new AStarPathFinding(), new SimpleSqrtEvaluationFunction3(),enemy);
	}

	public DynamicScripting(UnitTypeTable utt, int time, int max_playouts, PathFinding a_pf, EvaluationFunction e,int enemyIA) {
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
		
		this.enemyIA=enemyIA;

	}

	// This will be called by microRTS when it wants to create new instances of this
	// bot (e.g., to play multiple games).
	public AI clone() {
		return new DynamicScripting(m_utt,enemyIA);
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
				for(int i=0;i<750;i++)
				{
					//System.out.println("New Simulation! ");
					playout(player, gs);
					
					if(convergenceWin>=limitConvergence && roundConvergenceWin==-1)
					{
						roundConvergenceWin=i;
					}
					if(convergenceDraw>=limitConvergence && roundConvergenceDraw==-1)
					{
						roundConvergenceDraw=i;
					}
				}				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			firstExecution=true;
			isPlayout=false;
			System.out.print(roundConvergenceWin+" "+roundConvergenceDraw+" , ");
			
			//here Im printing the current dataRules Space
			for (int i = 0; i < numTypesUnits; i++) {
				//Unit u = playerUnits.get(i);
				for(int j=0; j<totalRules;j++)
				{
					Rule rule=RulesSpaceUnit.get(typesUnits[i]).get(j);
					System.out.println("Final Rule "+rule.getRule_id()+" "+rule.getRule_condition()+" "+rule.getRule_action()+" "+rule.getRule_paramether()+" "+rule.getWeight());
				}
			}
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
	public ArrayList<Rule> rulesGeneration(int unitsSize) {
		ArrayList<Rule> rulesSpaceList = new ArrayList<Rule>();
		
		int counterId = 0;
		for (int i = 0; i < rulesSpace.getNumberConditions(); i++) {
			for (int j = 0; j < rulesSpace.getNumberActions(); j++) {
				for (int k = 0; k < rulesSpace.getNumberParamethers(); k++) {
					
					if(aux.validationConstraintRule(i,j,k,rulesSpace))
					{
						Rule rule = new Rule(counterId, initialWeight, unitsSize, i, j, k);
						counterId++;
						rulesSpaceList.add(rule);
					}					

				}
			}
		}
		
		totalRules = counterId;

		// code for print the actual arraylist of objects
//		for (Rule rule : rulesSpaceList) {
//			System.out.println(rule.getRule_id() + " " + rule.getWeight() + " " + rule.getActive() + " "
//					+ rule.getRule_condition() + " " + rule.getRule_action() + " " + rule.getRule_paramether());
//		}
		return rulesSpaceList;
	}

	private void generationRulesSpaces(int unitsSize) {
		for (int i = 0; i < numTypesUnits; i++) {
			//Unit u = playerUnits.get(i);
			ArrayList<Rule> rulesSpaceList = rulesGeneration(unitsSize);
			RulesSpaceUnit.put(typesUnits[i], rulesSpaceList);
		}
	}

	public void selectionRulesForUnits(int n1,Unit [] playerUnits) {
		for (int i = 0; i < n1; i++) {
			Unit u = playerUnits[i];
			ScriptGeneration actualScript = new ScriptGeneration(totalRules, RulesSpaceUnit.get(u.getType().name));
			ArrayList<Rule> rulesSelectedList = actualScript.selectionRules();
			RulesSelectedUnit.put(i, rulesSelectedList);

		}
	}
	
	public void selectionFinalRulesForUnits(int n1,Unit [] playerUnits) {
		for (int i = 0; i < n1; i++) {
			Unit u = playerUnits[i];
			ScriptGeneration actualScript = new ScriptGeneration(totalRules, RulesSpaceUnit.get(u.getType().name));
			ArrayList<Rule> rulesSelectedList = actualScript.selectionFinalRules();
			RulesSelectedUnit.put(i, rulesSelectedList);

		}
	}
	
	public PlayerAction ActionsAssignments(int player, GameState gs)
	{
		PlayerAction pa = new PlayerAction();
		pa.fillWithNones(gs, player, 10);
		
		
		
		
		if(firstAll)
		{
			List<Unit> playerUnits = aux.units1(player,gs);
			generationRulesSpaces(playerUnits.size());
			firstAll=false;
		}
		
		if(isPlayout && firstExecution)
		{
			List<Unit> playerUnits = aux.units1(player,gs);
			maxUnits=new Unit[playerUnits.size()];
			for (int i = 0; i < playerUnits.size(); i++) {
				Unit u = playerUnits.get(i);
				maxUnits[i]=u;
			}
			
			RulesSelectedUnit.clear();
			selectionRulesForUnits(maxUnits.length,maxUnits);
			firstExecution=false;
			
			//here Im printing the current dataRules Space
//			for (int i = 0; i < numTypesUnits; i++) {
//				//Unit u = playerUnits.get(i);
//				for(int j=0; j<totalRules;j++)
//				{
//					Rule rule=RulesSpaceUnit.get(typesUnits[i]).get(j);
//					System.out.println("Final Rule "+rule.getRule_id()+" "+rule.getRule_condition()+" "+rule.getRule_action()+" "+rule.getRule_paramether()+" "+rule.getWeight());
//				}
//			}
			
			for (int i = 0; i < maxUnits.length; i++) {
				for(int j=0;j<RulesSelectedUnit.get(i).size();j++)
				{
				System.out.println("Rule selected "+i+" "+RulesSelectedUnit.get(i).get(j).getRule_condition()+RulesSelectedUnit.get(i).get(j).getRule_action()+RulesSelectedUnit.get(i).get(j).getRule_paramether());
				}
				
			}
		
		}
		
		else if(!isPlayout && firstExecution)
		{	
			List<Unit> playerUnits = aux.units1(player,gs);
			maxUnits=new Unit[playerUnits.size()];
			for (int i = 0; i < playerUnits.size(); i++) {
				Unit u = playerUnits.get(i);
				maxUnits[i]=u;
			}
			
			RulesSelectedUnit.clear();
			selectionRulesForUnits(maxUnits.length,maxUnits);
			firstExecution=false;
			
			//here Im printing the current dataRules Space
			for (int i = 0; i < numTypesUnits; i++) {
				//Unit u = playerUnits.get(i);
				for(int j=0; j<totalRules;j++)
				{
					Rule rule=RulesSpaceUnit.get(typesUnits[i]).get(j);
					System.out.println("Final Rule "+rule.getRule_id()+" "+rule.getRule_condition()+" "+rule.getRule_action()+" "+rule.getRule_paramether()+" "+rule.getWeight());
				}
			}
			
			for (int i = 0; i < maxUnits.length; i++) {
				for(int j=0;j<RulesSelectedUnit.get(i).size();j++)
				{
				System.out.println("Rule selected "+i+" "+RulesSelectedUnit.get(i).get(j).getRule_condition()+RulesSelectedUnit.get(i).get(j).getRule_action()+RulesSelectedUnit.get(i).get(j).getRule_paramether());
				}
				
			}
		}
		
		parametersScripts = new ParametersScripts(rulesSpace);
		conditionsScripts = new ConditionsScripts(rulesSpace, parametersScripts, gs);
		PhysicalGameState pgs = gs.getPhysicalGameState();
		
		unitsAssignedEnemys=new ArrayList<Unit>();
		for (int i = 0; i < maxUnits.length; i++) {
			Unit u = maxUnits[i];
			boolean ruleApplied=false;
			if (gs.getUnitAction(u) == null && pgs.getUnits().contains(u)) {

				ArrayList<Rule> rulesSelected = RulesSelectedUnit.get(i);

				for (int j = 0; j < rulesSelected.size(); j++) {
					Rule currentRule = rulesSelected.get(j);
					
					Unit u2 = parametersScripts.validationParameter(u, gs,currentRule.getRule_paramether(),unitsAssignedEnemys);

					if (conditionsScripts.validationCondition(currentRule.getRule_condition(),
							u2, u)) {
						
						if (currentRule.getRule_action() == rulesSpace.getAction_attack()) {
							currentRule.active[i]=true;
							//System.out.println("action Attack " + rulesSelected.get(j).getRule_paramether());
							UnitScript s = attackTo.instantiate(u, gs, u2);
			                if (s!=null) {
			                    UnitAction ua = s.getAction(u, gs);
			                    if (ua!=null) {
			                    	unitsAssignedEnemys.add(u2);
			                        pa.addUnitAction(u, ua);			                        
			                    } else {
			                        pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_NONE));
			                    }
			                } else {
			                    pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_NONE));                
			                }			                
							ruleApplied=true;
							break;
							
						} else if (currentRule.getRule_action() == rulesSpace.getAction_moveawayof()) {
							currentRule.active[i]=true;
							//System.out.println("action move Away " + rulesSelected.get(j).getRule_paramether());
							UnitScript s = moveAwayTo.instantiate(u, gs, u2);
			                if (s!=null) {
			                    UnitAction ua = s.getAction(u, gs);
			                    if (ua!=null) {
			                        pa.addUnitAction(u, ua);			                        
			                    } else {
			                        pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_NONE));
			                    }
			                } else {
			                    pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_NONE));                
			                }			                
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
		int timeLimit = gs2.getTime() + LOOKAHEAD;
		boolean gameover = false;
		AI ai1=null;
		
		if (enemyIA==1) {
        	ai1 = new RandomBiasedAI(m_utt);
        }else if (enemyIA==2) {
        	ai1 = new WorkerRush(m_utt,new BFSPathFinding()); 
        } else if (enemyIA==3) {
        	ai1=new LightRush(m_utt);
        } else if (enemyIA==4) {
        	ai1= new PGSAI(m_utt);
        } else if (enemyIA==5) {        	
        	ai1=new ABCD(m_utt);
        } else if (enemyIA==6) {        	
    		ai1=new POHeavyRush(m_utt);
    	} else if (enemyIA==7) {        	
    		ai1=new POLightRush(m_utt);
    	} else if (enemyIA==8) {        	
    		ai1=new PORangedRush(m_utt);
    	}
		
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
				PlayerAction pa2 = ActionsAssignments(player, gs2);
				gs2.issue(pa);
				gs2.issue(pa2);
				
				timeDeath=unitStatistics.timeDeath(timeDeath);
			}
		}	
		timeDeath=unitStatistics.timeDeath(timeDeath);

		//From Here the parameter for adjustment
		double globalEvaluation = evaluation.evaluate(player, 1 - player, gs2);
		globalEvaluation=aux.NormalizeInRangue(globalEvaluation,2,0.5);
		
		System.out.println(" done: " + gs2.winner());
		
		if(gs2.winner()==player ) 
		{
			convergenceWin++;

		}
		if(gs2.winner()==player  || gs2.winner()==-1 )
		{
			convergenceDraw++;
			if( gs2.winner()==-1)
			{
				convergenceWin=0;
			}
		}
		if(gs2.winner()==0)
		{
			convergenceWin=0;
			convergenceDraw=0;
		}
		
		double aFactor[]=new double[playerUnitsg2.size()];
		for (int i = 0; i < playerUnitsg2.size(); i++) {
			aFactor[i]=unitStatistics.aFactor(timeDeath[i], LOOKAHEAD, i);
		}
		
		double teamFactor=unitStatistics.teamFactor();
		//System.out.println("teamFactor "+teamFactor);
		
		double bFactor=unitStatistics.bFactor();
		//System.out.println("bFactor "+bFactor);
		
		double cFactor=unitStatistics.cFactor();
		//System.out.println("cFactor "+cFactor);
		
//		for (int i = 0; i < playerUnitsg2.size(); i++) {
//			System.out.println("unit "+timeDeath[i]+ " "+playerUnitsg2.get(i).getType().name+" "+aFactor[i]);
//		}
		
		//Here we are updating
		ScriptGeneration actualScript = new ScriptGeneration(totalRules); 
		for (int i = 0; i < playerUnitsg2.size(); i++) {
//			System.out.println("New Script Updating");
			Unit u = playerUnitsg2.get(i);
			actualScript.UpdateWeightsBeta(i,RulesSelectedUnit.get(i), RulesSpaceUnit.get(u.getType().name), globalEvaluation ,initialWeight, teamFactor,bFactor,cFactor,aFactor[i]);
		}
	}

	/**
	 * @return the rulesSpaceUnit
	 */
	public HashMap<String, ArrayList<Rule>> getRulesSpaceUnit() {
		return RulesSpaceUnit;
	}

	/**
	 * @return the rulesSpace
	 */
	public RulesSpace getRulesSpace() {
		return rulesSpace;
	}

}