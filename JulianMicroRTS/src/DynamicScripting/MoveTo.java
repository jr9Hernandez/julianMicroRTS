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
public class MoveTo extends AbstractAction {

    int x,y;
    PathFinding pf;
    Unit u;

    
    public MoveTo(Unit u, int a_x, int a_y, PathFinding a_pf) {
        super(u);
        x = a_x;
        y = a_y;
        pf = a_pf;
        this.u=u;
    }
    
    public boolean completed(GameState gs) {
        if (u.getX()==x && u.getY()==y) return true;
        return false;
    }

    public UnitAction execute(GameState gs, ResourceUsage ru) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        UnitAction move = pf.findPathToAdjacentPosition(u, x+y*pgs.getWidth(), gs, ru);
//        System.out.println("AStarAttak returns: " + move);
        if (move!=null && gs.isUnitActionAllowed(u, move)) return move;
        return null;
    }
}
