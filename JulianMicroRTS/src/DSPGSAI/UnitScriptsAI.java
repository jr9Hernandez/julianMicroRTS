/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DSPGSAI;

import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import dynamicscripting.ConditionsScripts;
import dynamicscripting.DynamicScripting;
import dynamicscripting.ParametersScripts;
import dynamicscripting.Rule;
import dynamicscripting.UnitScript;
import dynamicscripting.UnitScriptAttackTo;
import dynamicscripting.UnitScriptMoveAwayTo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;

/**
 *
 * @author santi
 */
public class UnitScriptsAI extends AI {

    public static int DEBUG = 0;
    
    Rule scriptsInput[];
    List<Unit> unitsInput;
    HashMap<Unit, Rule> scripts  = new HashMap<>();
    HashMap<UnitType, ArrayList<Rule>> allScripts = null;
    Rule defaultScript = null;
    private ParametersScripts parametersScripts;
    private ConditionsScripts conditionsScripts;
    DynamicScripting DS=null;
    UnitScript attackTo;
    UnitScript moveAwayTo;
    PathFinding pf;
    
    
    public UnitScriptsAI(Rule a_scripts [], List<Unit> a_units,
                         HashMap<UnitType, ArrayList<Rule>> a_allScripts,
                         Rule a_defaultScript, DynamicScripting a_DS,PathFinding a_pf) {
        scriptsInput = a_scripts;
        unitsInput = a_units;
        for(int i = 0;i<a_scripts.length;i++) {
            scripts.put(a_units.get(i), a_scripts[i]);
        }
        allScripts = a_allScripts;
        defaultScript = a_defaultScript;
        DS=a_DS;
        pf=a_pf;
    	attackTo = new UnitScriptAttackTo(pf);
    	moveAwayTo = new UnitScriptMoveAwayTo(pf);
    }
    
    
    public void reset() {
    }
    
//    public void resetScripts(GameState gs) {   
//        for(Unit u:scripts.keySet()) {
//            UnitScript s = scripts.get(u);
//            scripts.put(u, s.instantiate(u, gs));
//        }
//    }
    

    public PlayerAction getAction(int player, GameState gs) throws Exception {
//        System.out.println("    UnitScriptsAI.getAction " + player + ", " + gs.getTime());
    	parametersScripts = new ParametersScripts(DS.getRulesSpace());
		conditionsScripts = new ConditionsScripts(DS.getRulesSpace(), parametersScripts, gs);
        PlayerAction pa = new PlayerAction();
         ArrayList<Unit> unitsAssignedEnemys=new ArrayList<Unit>();
        for(Unit u:gs.getUnits()) {
            if (u.getPlayer()==player && gs.getUnitAction(u)==null) {

					Rule currentRule = scripts.get(u);
					
					if(currentRule==null)
					{
						currentRule=defaultScript;
					}
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
							break;
							
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
        return pa;
    }

    
    @Override
    public AI clone() {
        return new UnitScriptsAI(scriptsInput, unitsInput, allScripts, defaultScript,DS,pf);
    }
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }

    
    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();
        
        parameters.add(new ParameterSpecification("Scripts", List.class, scriptsInput));
        parameters.add(new ParameterSpecification("Units", List.class, unitsInput));
        parameters.add(new ParameterSpecification("AllScripts", HashMap.class, allScripts));
        parameters.add(new ParameterSpecification("DefaultScript", UnitScript.class, defaultScript));
        
        return parameters;
    }
    
}
