package dynamicscripting;

import java.util.ArrayList;
import java.util.List;

import rts.units.Unit;

public class CompoundScript {
	
	private int globalValue;
	private ArrayList<Rule> compoundScript;


	public CompoundScript(int globalValue, ArrayList<Rule> compoundScript)
	{
		this.globalValue=globalValue;
		this.compoundScript=compoundScript;
	}


	/**
	 * @return the globalValue
	 */
	public int getGlobalValue() {
		return globalValue;
	}


	/**
	 * @return the compoundScript
	 */
	public ArrayList<Rule> getCompoundScript() {
		return compoundScript;
	}
	
}
