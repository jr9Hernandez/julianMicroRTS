/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicscripting;

import rts.GameState;
import rts.UnitAction;
import rts.units.Unit;

/**
 *
 * This class is based in the original class with the same name for portafolio greedy search, by Santi Ontañon
 */
public abstract class UnitScript {
    public abstract UnitAction getAction(Unit u, GameState gs);
    public abstract UnitScript instantiate(Unit u, GameState gs, Unit u2);
}
