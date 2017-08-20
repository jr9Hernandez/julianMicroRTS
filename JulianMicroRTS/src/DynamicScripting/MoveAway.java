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
public class MoveAway extends AbstractAction  {
    Unit target;
    PathFinding pf;
    Unit u;
    
    public MoveAway(Unit u, Unit a_target, PathFinding a_pf) {
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
        
        int newX=0;
        int newY=0;
        
        if(dx>0)
        {
        	newX=u.getX()+1;
        }
        else if(dx>0) 
        {
        	newX=u.getX()-1;     	
        }
        
        if(dy>0)
        {
        	newY=u.getY()+1;
        }
        else if(dx>0) 
        {
        	newY=u.getY()-1;        	
        }

//        double d = Math.sqrt(dx*dx+dy*dy);
        
            // move towards the unit:
    //  System.out.println("AStarAttak returns: " + move);
        UnitAction move = pf.findPathToPositionInRange(u, newX+newY*gs.getPhysicalGameState().getWidth(), u.getAttackRange(), gs, ru);
        if (move!=null && gs.isUnitActionAllowed(u, move)) return move;
        return null;
                
    }    
}
