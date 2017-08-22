/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicScripting;

import ai.abstraction.AbstractAction;
import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;

/**
 *
 * @author santi
 */
public class MoveTo extends AbstractAction  {
    Unit target;
    PathFinding pf;
    Unit u;
    
    public MoveTo(Unit u, Unit a_target, PathFinding a_pf) {
        super(u);
        target = a_target;
        pf = a_pf;
        this.u=u;
    }
    
    public boolean completed(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        if (!pgs.getUnits().contains(target)) return true;
        return false;
    }

    public UnitAction execute(GameState gs, ResourceUsage ru) {
        
        int dx = target.getX()-u.getX();
        int dy = target.getY()-u.getY();
        double d = Math.sqrt(dx*dx+dy*dy);
        if (d<=target.getAttackRange()) {
        	System.out.println("gol");
            return new UnitAction(UnitAction.TYPE_MOVE,UnitAction.DIRECTION_RIGHT);
        } else {
            // move towards the unit:
    //        System.out.println("AStarAttak returns: " + move);
        	return new UnitAction(UnitAction.TYPE_NONE);
        }        
    }    
}
