/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DSPGS_Churchill;

import java.util.ArrayList;

import ai.abstraction.AbstractAction; 
import ai.abstraction.Attack;
import ai.abstraction.Move;
import ai.abstraction.pathfinding.PathFinding;
import dynamicscripting.CompoundScript;
import dynamicscripting.ConditionsScripts;
import dynamicscripting.DynamicScripting;
import dynamicscripting.ParametersScripts;
import dynamicscripting.Rule;
import dynamicscripting.UnitScript;
import dynamicscripting.UnitScriptAttackTo;
import dynamicscripting.UnitScriptMoveAwayTo;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

/**
 *
 * @author This class is based in the original class with the same name for portafolio greedy search, by Santi Ontañon
 */
public class UnitScriptSingle  {
    
    AbstractAction action = null;
    CompoundScript sc=null;
	ConditionsScripts conditionsScripts;
	ParametersScripts parametersScripts;
	UnitScript s=null;
	PathFinding pf;
	
    
    public UnitScriptSingle(CompoundScript a_sc, PathFinding a_pf) {
        this.sc=a_sc;
        this.pf=a_pf;
    }
    
    
    public UnitScript instantiate(Unit u, GameState gs, DynamicScripting DS, ArrayList<Unit> unitsAssignedEnemys) {

    	s=null;
    	parametersScripts = new ParametersScripts(DS.getRulesSpace());
		conditionsScripts = new ConditionsScripts(DS.getRulesSpace(), parametersScripts, gs);        
    	
		
		for (int j = 0; j < sc.getCompoundScript().size(); j++) 
		{
		Rule rule = sc.getCompoundScript().get(j);
		Unit u2 = parametersScripts.validationParameter(u, gs,rule.getRule_paramether(),unitsAssignedEnemys);
		if (conditionsScripts.validationCondition(rule.getRule_condition(),
				u2, u)) {
			
			if (rule.getRule_action() == DS.getRulesSpace().getAction_attack()) {
				s=new UnitScriptAttackTo(pf);
				s = s.instantiate(u, gs, u2);
				if(s.getAction(u, gs)!=null)
					unitsAssignedEnemys.add(u2);
				break;
				
			} else if (rule.getRule_action() == DS.getRulesSpace().getAction_moveawayof()) {
				//System.out.println("action move Away " + rulesSelected.get(j).getRule_paramether());
				s=new UnitScriptMoveAwayTo(pf);
				s = s.instantiate(u, gs, u2);
				break;
			}	

		}
		}
		return s;
    }
    
}
