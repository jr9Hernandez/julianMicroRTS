/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DSPGS_Churchill;

import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import dynamicscripting.DynamicScripting;
import dynamicscripting.UnitScript;

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

	HashMap<Long, UnitScriptSingle> scriptsInput;
	List<Unit> unitsInput;
	HashMap<Long, UnitScriptSingle> scripts = new HashMap<>();
	HashMap<UnitType, List<UnitScriptSingle>> allScripts = null;
	DynamicScripting DS = null;
	PathFinding pf;

	public UnitScriptsAI(HashMap<Long, UnitScriptSingle> a_scripts, List<Unit> a_units,
			HashMap<UnitType, List<UnitScriptSingle>> a_allScripts, DynamicScripting a_DS, PathFinding a_pf) {

		scriptsInput = a_scripts;
		unitsInput = a_units;
		for (int i = 0; i < a_scripts.size(); i++) {
			scripts.put(a_units.get(i).getID(), a_scripts.get(a_units.get(i).getID()));
		}
		allScripts = a_allScripts;
		DS = a_DS;
		pf = a_pf;
	}

	public void reset() {
	}

	// public void resetScripts(GameState gs) {
	// for(Unit u:scripts.keySet()) {
	// UnitScriptSingle s = scripts.get(u);
	// scripts.put(u, s.instantiate(u, gs,DS));
	// }
	// }

	public PlayerAction getAction(int player, GameState gs) throws Exception {
		// System.out.println(" UnitScriptsAI.getAction " + player + ", " +
		// gs.getTime());
		PlayerAction pa = new PlayerAction();
		ArrayList<Unit> unitsAssignedEnemys=new ArrayList<Unit>(); 	
		for (Unit u : gs.getUnits()) {
			if (u.getPlayer() == player && gs.getUnitAction(u) == null) {
				UnitScriptSingle su = scripts.get(u.getID());
				UnitScript s = null;
				s = su.instantiate(u, gs, DS,unitsAssignedEnemys);
				
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

	@Override
	public AI clone() {
		return new UnitScriptsAI(scriptsInput, unitsInput, allScripts, DS, pf);
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
		// parameters.add(new ParameterSpecification("DefaultScript", UnitScript.class,
		// defaultScript));

		return parameters;
	}

}
