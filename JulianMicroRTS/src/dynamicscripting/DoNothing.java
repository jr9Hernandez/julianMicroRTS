/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicscripting;

import ai.abstraction.AbstractAction;
import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import util.XMLWriter;

/**
 *
 * @author santi
 */
public class DoNothing extends AbstractAction  {

    
    public DoNothing(Unit u) {
        super(u);
    }
    
    public boolean completed(GameState gs) {
        return false;
    }

    public UnitAction execute(GameState gs, ResourceUsage ru) {
        
        return new UnitAction(UnitAction.TYPE_NONE);
        
    }

	@Override
	public void toxml(XMLWriter w) {
		// TODO Auto-generated method stub
		
	}    
}
