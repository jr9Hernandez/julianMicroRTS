/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DSPGSAI;

import ai.RandomBiasedAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.EvaluationFunctionForwarding;
import ai.evaluation.SimpleSqrtEvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction2;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import dynamicscripting.AuxMethods;
import dynamicscripting.ConditionsScripts;
import dynamicscripting.DynamicScripting;
import dynamicscripting.ParametersScripts;
import dynamicscripting.Rule;
import dynamicscripting.UnitScript;
import dynamicscripting.UnitScriptAttackTo;
import dynamicscripting.UnitScriptMoveAwayTo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

/**
 *
 * @author santi
 *
 * This class implements "Portfolio Greedy Search", as presented by Churchill and Buro in the paper:
 * "Portfolio Greedy Search and Simulation for Large-Scale Combat in StarCraft"
 *
 * Moreover, their original paper focused purely on combat, and thus their portfolio was very samll.
 * Here:
 * - getSeedPlayer does not make sense in general, since each unit type might have a different set of scripts, so it's ignored
 * - the portfolios might be very large, since we have to include scripts for training, building, harvesting, etc.
 * - new units might be created, so a script is selected as the "default" for those new units before hand
 *
 */
public class DSPGSAI extends AIWithComputationBudget {

    public static int DEBUG = 0;

    int LOOKAHEAD = 200;
    int I = 1;  // number of iterations for improving a given player
    int R = 1;  // number of times to improve with respect to the response fo the other player
    EvaluationFunction evaluation = null;
    HashMap<UnitType, ArrayList<Rule>> scripts = null;
    UnitTypeTable utt;
    PathFinding pf;

    Rule defaultScript = null;
    Rule defaultScriptEnemy = null;

    long start_time = 0;
    int nplayouts = 0;
    
    DynamicScripting DS=null;
    int sizePortfolio=2;
    AuxMethods aux=new AuxMethods();
    private ArrayList<Unit> unitsAssignedEnemys;
    private ParametersScripts parametersScripts;
    private ConditionsScripts conditionsScripts;
    UnitScript attackTo;
    UnitScript moveAwayTo;
    
    public DSPGSAI(UnitTypeTable utt, DynamicScripting aiAux) {
        this(100, -1, 200, 1, 1, 
             new SimpleSqrtEvaluationFunction3(),
             utt,
             new AStarPathFinding(), aiAux);
    }
    
    
    public DSPGSAI(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf, DynamicScripting aiAux) {
        super(time, max_playouts);
        LOOKAHEAD = la;
        I = a_I;
        R = a_R;
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        DS=aiAux;
    	HashMap<String, ArrayList<Rule>> RulesSpaceUnit=DS.getRulesSpaceUnit();
    	
    	attackTo = new UnitScriptAttackTo(pf);
    	moveAwayTo = new UnitScriptMoveAwayTo(pf);
    	
    	ArrayList<Rule> heavyS=RulesSpaceUnit.get("Heavy"); 
    	ArrayList<Rule> lightS=RulesSpaceUnit.get("Light"); 
    	ArrayList<Rule> rangedS=RulesSpaceUnit.get("Ranged"); 
    	
//    	System.out.println("PrintingBefore ");
//
//			for(int j=0; j<heavyS.size();j++)
//			{
//				Rule rule=RulesSpaceUnit.get("Heavy").get(j);
//				System.out.println("Final Rule "+heavyS.get(j).getRule_id()+" "+heavyS.get(j).getRule_condition()+" "+heavyS.get(j).getRule_action()+" "+heavyS.get(j).getRule_paramether()+" "+heavyS.get(j).getWeight());
//			}
		
    	
    	aux.orderInReverseArraylistRule(heavyS);
    	aux.orderInReverseArraylistRule(lightS);
    	aux.orderInReverseArraylistRule(rangedS);
//    	
    	System.out.println("PrintingAfter ");

			for(int j=0; j<heavyS.size();j++)
			{
				Rule rule=RulesSpaceUnit.get("Heavy").get(j);
				System.out.println("Final Rule Heavy "+heavyS.get(j).getRule_id()+" "+heavyS.get(j).getRule_condition()+" "+heavyS.get(j).getRule_action()+" "+heavyS.get(j).getRule_paramether()+" "+heavyS.get(j).getWeight());
			}
			for(int j=0; j<lightS.size();j++)
			{
				Rule rule=RulesSpaceUnit.get("Light").get(j);
				System.out.println("Final Rule Light"+lightS.get(j).getRule_id()+" "+lightS.get(j).getRule_condition()+" "+lightS.get(j).getRule_action()+" "+lightS.get(j).getRule_paramether()+" "+lightS.get(j).getWeight());
			}
    	
    	scripts = new HashMap<>();
    	scripts.put(utt.getUnitType("Heavy"),heavyS);
    	scripts.put(utt.getUnitType("Light"),lightS);
    	scripts.put(utt.getUnitType("Ranged"),rangedS);
    	
    	defaultScript=heavyS.get(0);
    	defaultScriptEnemy=new Rule(1200, 100, 0, 0, 0, 0); 

//        UnitScript harvest = new UnitScriptHarvest(pf,utt);
//        UnitScript buildBarracks = new UnitScriptBuild(pf,utt.getUnitType("Barracks"));
//        UnitScript buildBase = new UnitScriptBuild(pf,utt.getUnitType("Base"));
//        UnitScript attack = new UnitScriptAttack(pf);
//        UnitScript idle = new UnitScriptIdle();
//        UnitScript trainWorker = new UnitScriptTrain(utt.getUnitType("Worker"));
//        UnitScript trainLight = new UnitScriptTrain(utt.getUnitType("Light"));
//        UnitScript trainHeavy = new UnitScriptTrain(utt.getUnitType("Heavy"));
//        UnitScript trainRanged = new UnitScriptTrain(utt.getUnitType("Ranged"));

//        defaultScript = idle;
//        
//        {
//            List<UnitScript> l = new ArrayList<>();
//            l.add(harvest);
//            l.add(buildBarracks);
//            l.add(buildBase);
//            l.add(attack);
//            l.add(idle);
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


    public void reset() {
    }


    public PlayerAction getAction(int player, GameState gs) throws Exception {
    	
        if (gs.winner()!=-1) return new PlayerAction();
        if (!gs.canExecuteAnyAction(player)) return new PlayerAction();

        if (DEBUG>=1) System.out.println("PGSAI " + player + "(MAX_TIME = " + TIME_BUDGET +", I: " + I + ", R: " + R + ")");

        List<Unit> playerUnits = new ArrayList<>();
        List<Unit> enemyUnits = new ArrayList<>();

        for(Unit u:gs.getUnits()) {
            if (u.getPlayer()==player) playerUnits.add(u);
            else if (u.getPlayer()>=0) enemyUnits.add(u);
        }
        int n1 = playerUnits.size();
        int n2 = enemyUnits.size();

        Rule playerScripts[] = new Rule[n1];
        Rule enemyScripts[] = new Rule[n2];

        // Init the players:
        for(int i = 0;i<n1;i++) playerScripts[i] = defaultScript(playerUnits.get(i), gs);
        for(int i = 0;i<n2;i++) enemyScripts[i] = defaultScriptEnemy(enemyUnits.get(i), gs);

        // Note: here, the original algorithm does "getSeedPlayer", which only makes sense if the same scripts can be used for all the units

        start_time = System.currentTimeMillis();
        nplayouts = 0;
        improve(player, playerScripts, playerUnits, enemyScripts, enemyUnits, gs);
//        for(int r = 0;r<R;r++) {
//            improve(1-player, enemyScripts, enemyUnits, playerScripts, playerUnits, gs);
//            improve(player, playerScripts, playerUnits, enemyScripts, enemyUnits, gs);
//        }

        // generate the final Player Action:
        unitsAssignedEnemys=new ArrayList<Unit>();
        PlayerAction pa = new PlayerAction();
        System.out.println("uniFInalRUle "+playerUnits.get(0)+" "+playerScripts[0].getRule_condition()+" "+playerScripts[0].getRule_action()+" "+playerScripts[0].getRule_paramether());
        for(int i = 0;i<n1;i++) {
        	
            Unit u = playerUnits.get(i);
            if (gs.getUnitAction(u)==null) {
				Rule currentRule = playerScripts[i];
				
				Unit u2 = parametersScripts.validationParameter(u, gs,currentRule.getRule_paramether(),unitsAssignedEnemys);

				if (conditionsScripts.validationCondition(currentRule.getRule_condition(),
						u2, u)) {
					
					if (currentRule.getRule_action() == DS.getRulesSpace().getAction_attack()) {
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
						
						
					} else if (currentRule.getRule_action() == DS.getRulesSpace().getAction_moveawayof()) {
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
						
					}	
					else {
						pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_NONE));
					}
//					} else if (rulesSelected.get(j).getRule_action() == rulesSpace.getAction_moveto()) {
//						System.out.println("action move " + rulesSelected.get(j).getRule_paramether());
//						UnitScript s = moveTo.instantiate(u, gs, u2);
//						UnitAction ua = s.getAction(u, gs);
//						pa.addUnitAction(u, ua);
//						break;
//						
//					}

				}
				
            }
        }

        return pa;
    }


    public Rule defaultScript(Unit u, GameState gs) {
        // the first script added per type is considered the default:
    	

        ArrayList<Rule> l = scripts.get(u.getType());
        Rule currentRule = l.get(0);
        
        return currentRule;

    }
    public Rule defaultScriptEnemy(Unit u, GameState gs) {
        // the first script added per type is considered the default:
    	

        Rule currentRule = new Rule(1200, 100, 0, 0, 0, 0);      
        return currentRule;

    }


    public void improve(int player,
                        Rule scriptsToImprove[], List<Unit> units,
                        Rule otherScripts[], List<Unit> otherUnits, GameState gs) throws Exception {
    	
        for(int i = 0;i<I;i++) {
            if (DEBUG>=1) System.out.println("Improve player " + player + "(" + i + "/" + I + ")");
            
            unitsAssignedEnemys=new ArrayList<Unit>();
            parametersScripts = new ParametersScripts(DS.getRulesSpace());
            conditionsScripts = new ConditionsScripts(DS.getRulesSpace(), parametersScripts, gs);  
            
            
            for(int u = 0;u<scriptsToImprove.length;u++) {
                if (ITERATIONS_BUDGET>0 && nplayouts>=ITERATIONS_BUDGET) {
                    if (DEBUG>=1) System.out.println("nplayouts>=MAX_PLAYOUTS");
                    return;
                }
                if (TIME_BUDGET>0 && System.currentTimeMillis()>=start_time+TIME_BUDGET) {
                    if (DEBUG>=1) System.out.println("Time out!");
                    return;
                }

                Unit unit = units.get(u);
                double bestEvaluation = 0;
                Rule bestScript = null;
                ArrayList<Rule> candidates = scripts.get(unit.getType());
                
                
                
                for(int j=0;j<sizePortfolio;j++) {
                	
                	Rule s=candidates.get(j);						

                    if (s!=null) {
                        if (DEBUG>=2) System.out.println("  " + unit + " -> " + s.getClass().toString());
                        scriptsToImprove[u] = s;
                        double e = playout(player, scriptsToImprove, units, otherScripts, otherUnits, gs);
                        if (bestScript==null || e>bestEvaluation) {
                            bestScript = s;
                            bestEvaluation = e;
                            System.out.println("unit "+unit+    "new best: " + e +" "+j+" "+s.getRule_condition()+" "+s.getRule_action()+" "+s.getRule_paramether());
                        }
                    }
                }
                scriptsToImprove[u] = bestScript;
                
            }
        }
    }


    public double playout(int player,
                          Rule scripts1[], List<Unit> units1,
                          Rule scripts2[], List<Unit> units2, GameState gs) throws Exception {
//        if (DEBUG>=1) System.out.println("  playout... " + LOOKAHEAD);
        nplayouts++;

        AI ai1 = new UnitScriptsAI(scripts1, units1, scripts, defaultScript,DS,pf);
        AI ai2 = new UnitScriptsAI(scripts2, units2, scripts, defaultScriptEnemy,DS,pf);

        GameState gs2 = gs.clone();
        ai1.reset();
        ai2.reset();
        int timeLimit = gs2.getTime() + LOOKAHEAD;
        boolean gameover = false;
        while(!gameover && gs2.getTime()<timeLimit) {
            if (gs2.isComplete()) {
                gameover = gs2.cycle();
            } else {
                gs2.issue(ai1.getAction(player, gs2));
                gs2.issue(ai2.getAction(1-player, gs2));
            }
        }        
        double e = evaluation.evaluate(player, 1-player, gs2);
//        if (DEBUG>=1) System.out.println("  done: " + e);
        return e;
    }


    @Override
    public AI clone() {
        return new DSPGSAI(TIME_BUDGET, ITERATIONS_BUDGET, LOOKAHEAD, I, R, evaluation, utt, pf,DS);
    }
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + TIME_BUDGET + ", " + ITERATIONS_BUDGET + ", " + LOOKAHEAD + ", " + I + ", " + R + ", " + evaluation + ", " + pf + ")";
    }
    
    
    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();
        
        parameters.add(new ParameterSpecification("TimeBudget",int.class,100));
        parameters.add(new ParameterSpecification("IterationsBudget",int.class,-1));
        parameters.add(new ParameterSpecification("PlayoutLookahead",int.class,100));
        parameters.add(new ParameterSpecification("I", int.class, 1));
        parameters.add(new ParameterSpecification("R", int.class, 1));
        parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class, new SimpleSqrtEvaluationFunction3()));
        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));
        
        return parameters;
    }    
    
    
    public int getPlayoutLookahead() {
        return LOOKAHEAD;
    }
    
    
    public void setPlayoutLookahead(int a_pola) {
        LOOKAHEAD = a_pola;
    }

    
    public int getI() {
        return I;
    }
    
    
    public void setI(int a) {
        I = a;
    }
    
    
    public int getR() {
        return R;
    }
    
    
    public void setR(int a) {
        R = a;
    }
       
    
    public EvaluationFunction getEvaluationFunction() {
        return evaluation;
    }
    
    
    public void setEvaluationFunction(EvaluationFunction a_ef) {
        evaluation = a_ef;
    }        
        
    
    public PathFinding getPathFinding() {
        return pf;
    }
    
    
    public void setPathFinding(PathFinding a_pf) {
        pf = a_pf;
    }    
}