/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicscripting;

import java.util.Random;

import ai.abstraction.AbstractAction;
import ai.abstraction.Attack;
import ai.abstraction.Idle;
import ai.abstraction.Move;
import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

/**
 *
 * @author This class is based in the original class with the same name for
 *         portafolio greedy search, by Santi Ontañon
 */
public class UnitScriptMoveAwayTo extends UnitScript {

	AbstractAction action = null;
	PathFinding pf = null;
	AuxMethods aux = new AuxMethods();

	public UnitScriptMoveAwayTo(PathFinding a_pf) {
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

		int dx = targetParameterRule.getX() - u.getX();
		int dy = targetParameterRule.getY() - u.getY();
		double d = Math.sqrt(dx * dx + dy * dy);

		int newX = u.getX();
		int newY = u.getY();

		int direction = -1;
		boolean[][] free = gs.getAllFree();

		if (dx >= 0 && dy >= 0) {
			direction = aux.randomNumberInRange(UnitAction.DIRECTION_UP, UnitAction.DIRECTION_LEFT);
			if (direction == UnitAction.DIRECTION_UP) {
				newY = newY - 1;
				if (newY < 0 || newY > gs.getPhysicalGameState().getHeight() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_LEFT;
					newY = newY + 1;
					newX = newX - 1;
				}
			} else if (direction == UnitAction.DIRECTION_LEFT) {
				newX = newX - 1;
				if (newX < 0 || newX > gs.getPhysicalGameState().getWidth() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_UP;
					newX = newX + 1;
					newY = newY - 1;
				}
			}

		} else if (dx >= 0 && dy <= 0) {
			direction = aux.randomNumberInRange(UnitAction.DIRECTION_DOWN, UnitAction.DIRECTION_LEFT);
			if (direction == UnitAction.DIRECTION_DOWN) {
				newY = newY + 1;
				if (newY < 0 || newY > gs.getPhysicalGameState().getHeight() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_LEFT;
					newY = newY - 1;
					newX = newX - 1;
				}
			} else if (direction == UnitAction.DIRECTION_LEFT) {
				newX = newX - 1;
				if (newX < 0 || newX > gs.getPhysicalGameState().getWidth() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_DOWN;
					newX = newX + 1;
					newY = newY + 1;
				}
			}
		} else if (dx <= 0 && dy >= 0) {
			direction = aux.randomNumberInRange(UnitAction.DIRECTION_UP, UnitAction.DIRECTION_RIGHT);
			if (direction == UnitAction.DIRECTION_UP) {
				newY = newY - 1;
				if (newY < 0 || newY > gs.getPhysicalGameState().getHeight() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_RIGHT;
					newY = newY + 1;
					newX = newX + 1;
				}
			} else if (direction == UnitAction.DIRECTION_RIGHT) {
				newX = newX + 1;
				if (newX < 0 || newX > gs.getPhysicalGameState().getWidth() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_UP;
					newX = newX - 1;
					newY = newY - 1;
				}
			}

		}

		else if (dx <= 0 && dy <= 0) {

			direction = aux.randomNumberInRange(UnitAction.DIRECTION_RIGHT, UnitAction.DIRECTION_DOWN);
			if (direction == UnitAction.DIRECTION_DOWN) {
				newY = newY + 1;
				if (newY < 0 || newY > gs.getPhysicalGameState().getHeight() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_RIGHT;
					newY = newY - 1;
					newX = newX + 1;
				}
			} else if (direction == UnitAction.DIRECTION_RIGHT) {
				newX = newX + 1;
				if (newX < 0 || newX > gs.getPhysicalGameState().getWidth() - 1 || free[newX][newY] == false) {
					direction = UnitAction.DIRECTION_DOWN;
					newX = newX - 1;
					newY = newY + 1;
				}
			}
		}

		if ((newX < 0 || newX > gs.getPhysicalGameState().getWidth() - 1)
				|| (newY < 0 || newY > gs.getPhysicalGameState().getHeight() - 1) || free[newX][newY] == false) {
			newX = u.getX();
			newY = u.getY();
		}

		if (targetParameterRule != null) {
			UnitScriptMoveAwayTo script = new UnitScriptMoveAwayTo(pf);
			if (d <= targetParameterRule.getAttackRange() + 1) {
				
				if (u.getX() == newX && u.getY() == newY) {
					script.action = new DoNothing(u);
					return script;
				} else {
					script.action = new Move(u, newX, newY, pf);
					return script;
				}
			} else {
				script.action = new DoNothing(u);
				return script;
			}
		} else {
			return null;
		}
	}

	// public Unit closestEnemyUnit(Unit u, GameState gs) {
	// Unit closest = null;
	// int closestDistance = 0;
	// for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
	// if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
	// int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
	// if (closest == null || d < closestDistance) {
	// closest = u2;
	// closestDistance = d;
	// }
	// }
	// }
	// return closest;
	// }

}
