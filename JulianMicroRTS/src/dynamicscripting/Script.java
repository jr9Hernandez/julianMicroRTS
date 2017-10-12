package dynamicscripting;

import java.util.ArrayList;


public class Script {
	
	private ArrayList<Rule> script;
	private double globalFitness;

	public Script(ArrayList<Rule> script, double globalFitness)
	{
		this.script=script;
		this.globalFitness=globalFitness;
	}
	
}
