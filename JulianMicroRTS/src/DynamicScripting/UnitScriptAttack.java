/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicScripting;

import ai.abstraction.AbstractAction;
import ai.abstraction.Attack;
import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

/**
 *
 * @author This class is based in the original class with the same name for portafolio greedy search, by Santi Onta�on
 */
public class UnitScriptAttack extends UnitScript {
    
    AbstractAction action = null;
    PathFinding pf = null;
    
    public UnitScriptAttack(PathFinding a_pf) {
        pf = a_pf;
    }
    
    public UnitAction getAction(Unit u, GameState gs) {
        if (action.completed(gs)) {
            return null;
        } else {
            return action.execute(gs);
        }
    }
    
    public UnitScript instantiate(Unit u, GameState gs, Unit u2) {
        Unit targetParameterRule = u2;
        if (u2 != null) {
            UnitScriptAttack script = new UnitScriptAttack(pf);
            script.action = new Attack(u, u2, pf);
            return script;
        } else {
            return null;
        }
    }
    
    
//    public Unit closestEnemyUnit(Unit u, GameState gs) {
//        Unit closest = null;
//        int closestDistance = 0;
//        for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
//            if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
//                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
//                if (closest == null || d < closestDistance) {
//                    closest = u2;
//                    closestDistance = d;
//                }
//            }
//        }
//        return closest;
//    }
    
}
