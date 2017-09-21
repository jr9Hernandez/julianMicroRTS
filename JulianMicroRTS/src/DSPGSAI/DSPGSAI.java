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

    int LOOKAHEAD = 500;
    int I = 1;  // number of iterations for improving a given player
    int R = 1;  // number of times to improve with respect to the response fo the other player
    EvaluationFunction evaluation = null;
    HashMap<UnitType, ArrayList<Rule>> scripts = null;
    UnitTypeTable utt;
    PathFinding pf;

    UnitScript defaultScript = null;

    long start_time = 0;
    int nplayouts = 0;
    
    DynamicScripting DS=null;
    int sizePortfolio=3;
    AuxMethods aux=new AuxMethods();
    private ArrayList<Unit> unitsAssignedEnemys;
    private ParametersScripts parametersScripts;
    private ConditionsScripts conditionsScripts;
    UnitScript attackTo;
    UnitScript moveAwayTo;
    int [] actionsExecuted;
    
    public DSPGSAI(UnitTypeTable utt, DynamicScripting aiAux) {
        this(100, -1, 100, 1, 1, 
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
		
    	
    	aux.orderInReverseArraylist(heavyS);
    	aux.orderInReverseArraylist(lightS);
    	aux.orderInReverseArraylist(rangedS);
//    	
    	System.out.println("PrintingAfter ");

			for(int j=0; j<heavyS.size();j++)
			{
				Rule rule=RulesSpaceUnit.get("Heavy").get(j);
				System.out.println("Final Rule "+heavyS.get(j).getRule_id()+" "+heavyS.get(j).getRule_condition()+" "+heavyS.get(j).getRule_action()+" "+heavyS.get(j).getRule_paramether()+" "+heavyS.get(j).getWeight());
			}
			for(int j=0; j<lightS.size();j++)
			{
				Rule rule=RulesSpaceUnit.get("Light").get(j);
				System.out.println("Final Rule "+lightS.get(j).getRule_id()+" "+lightS.get(j).getRule_condition()+" "+lightS.get(j).getRule_action()+" "+lightS.get(j).getRule_paramether()+" "+lightS.get(j).getWeight());
			}
    	
    	scripts = new HashMap<>();
    	scripts.put(utt.getUnitType("Heavy"),heavyS);
    	scripts.put(utt.getUnitType("Light"),lightS);
    	scripts.put(utt.getUnitType("Ranged"),rangedS);
    	
    	attackTo = new UnitScriptAttackTo(pf);
    	moveAwayTo = new UnitScriptMoveAwayTo(pf);
    	
    	defaultScript=new UnitScriptAttackTo(pf);
        

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

        UnitScript playerScripts[] = new UnitScript[n1];
        UnitScript enemyScripts[] = new UnitScript[n2];

        // Init the players:
        unitsAssignedEnemys=new ArrayList<Unit>();
        for(int i = 0;i<n1;i++) playerScripts[i] = defaultScript(playerUnits.get(i), gs);
        unitsAssignedEnemys=new ArrayList<Unit>();
        for(int i = 0;i<n2;i++) enemyScripts[i] = defaultScript(enemyUnits.get(i), gs);

        // Note: here, the original algorithm does "getSeedPlayer", which only makes sense if the same scripts can be used for all the units

        start_time = System.currentTimeMillis();
        nplayouts = 0;
        improve(player, playerScripts, playerUnits, enemyScripts, enemyUnits, gs);
        for(int r = 0;r<R;r++) {
            improve(1-player, enemyScripts, enemyUnits, playerScripts, playerUnits, gs);
            improve(player, playerScripts, playerUnits, enemyScripts, enemyUnits, gs);
        }

        // generate the final Player Action:
        unitsAssignedEnemys=new ArrayList<Unit>();
        PlayerAction pa = new PlayerAction();
        for(int i = 0;i<n1;i++) {
            Unit u = playerUnits.get(i);
            if (gs.getUnitAction(u)==null) {
                UnitScript s = playerScripts[i];
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
        }

        return pa;
    }


    public UnitScript defaultScript(Unit u, GameState gs) {
        // the first script added per type is considered the default:
    	
    	parametersScripts = new ParametersScripts(DS.getRulesSpace());
        ArrayList<Rule> l = scripts.get(u.getType());
        Rule currentRule = l.get(0);
        Unit u2 = parametersScripts.validationParameter(u, gs,currentRule.getRule_paramether(),unitsAssignedEnemys);
        
        UnitScript s=null;
        if (currentRule.getRule_action() == DS.getRulesSpace().getAction_attack()) 
        {        	
        	s = attackTo.instantiate(u, gs, u2);
        	if (s!=null) {
                UnitAction ua = s.getAction(u, gs);
                if (ua!=null) {
                	unitsAssignedEnemys.add(u2);		                        
                }
            }
        }
        else if(currentRule.getRule_action() == DS.getRulesSpace().getAction_moveawayof())
        {
        	s = moveAwayTo.instantiate(u, gs, u2);
        }
        return s.instantiate(u, gs, u2);
    }


    public void improve(int player,
                        UnitScript scriptsToImprove[], List<Unit> units,
                        UnitScript otherScripts[], List<Unit> otherUnits, GameState gs) throws Exception {
    	actionsExecuted=new int [scriptsToImprove.length];
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
                UnitScript bestScript = null;
                ArrayList<Rule> candidates = scripts.get(unit.getType());
                
                UnitScript s=null;
                
                for(int j=0;j<sizePortfolio;j++) {
                	
                	Rule us=candidates.get(j);
                	//System.out.println("UnitType "+unit);
                	//System.out.println("candidate "+j+" "+us.getRule_condition()+" "+us.getRule_action()+" "+us.getRule_paramether());
                	Unit u2 = parametersScripts.validationParameter(unit, gs,us.getRule_paramether(),unitsAssignedEnemys);
                	
					if (conditionsScripts.validationCondition(us.getRule_condition(),
							u2, unit)) {
						
						if (us.getRule_action() == DS.getRulesSpace().getAction_attack()) {
							//System.out.println("action Attack " + rulesSelected.get(j).getRule_paramether());
							s = attackTo.instantiate(unit, gs, u2);
					      	if (s!=null) {
				                UnitAction ua = s.getAction(unit, gs);
				                if (ua!=null) {
				                	unitsAssignedEnemys.add(u2);		                        
				                }
				            }
							
						} else if (us.getRule_action() == DS.getRulesSpace().getAction_moveawayof()) {
							//System.out.println("action move Away " + rulesSelected.get(j).getRule_paramether());
							s = moveAwayTo.instantiate(unit, gs, u2);

						}	

					}

                    if (s!=null) {
                        if (DEBUG>=2) System.out.println("  " + unit + " -> " + s.getClass().toString());
                        scriptsToImprove[u] = s;
                        actionsExecuted[u]=us.getRule_action();
                        double e = playout(player, scriptsToImprove, units, otherScripts, otherUnits, gs);
                        if (bestScript==null || e>bestEvaluation) {
                            bestScript = s;
                            bestEvaluation = e;
                            if (DEBUG>=2) System.out.println("    new best: " + e);
                        }
                    }
                }
                scriptsToImprove[u] = bestScript;
            }
        }
    }


    public double playout(int player,
                          UnitScript scripts1[], List<Unit> units1,
                          UnitScript scripts2[], List<Unit> units2, GameState gs) throws Exception {
//        if (DEBUG>=1) System.out.println("  playout... " + LOOKAHEAD);
        nplayouts++;

        AI ai1 = new UnitScriptsAI(scripts1, units1, scripts, defaultScript,DS);
        AI ai2 = new UnitScriptsAI(scripts2, units2, scripts, defaultScript,DS);

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