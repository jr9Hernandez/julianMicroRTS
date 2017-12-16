/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DSPGS_Churchill;

import ai.abstraction.LightRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.LanchesterEvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction2;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.puppet.SingleChoiceConfigurableScript;
import dynamicscripting.AuxMethods;
import dynamicscripting.CompoundScript;
import dynamicscripting.DynamicScripting;
import dynamicscripting.Rule;
import dynamicscripting.UnitScript;
import dynamicscripting.UnitScriptAttackTo;
import dynamicscripting.UnitScriptMoveAwayTo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import ai.abstraction.pathfinding.FloodFillPathFinding;

/**
 *
 * @author rubens, modified by Julian.
 */
public class DSPGSmRTS extends AIWithComputationBudget implements InterruptibleAI {

	int LOOKAHEAD = 200;
	int I = 1; // number of iterations for improving a given player
	int R = 0; // number of times to improve with respect to the response fo the other player
	EvaluationFunction evaluation = null;
	UnitTypeTable utt;
	PathFinding pf;
	int _startTime;

	AI defaultScript = null;

	long start_time = 0;
	int nplayouts = 0;

	GameState gs_to_start_from = null;
	int playerForThisComputation;

	DynamicScripting DS = null;
	AuxMethods aux = new AuxMethods();
	HashMap<UnitType, List<UnitScriptSingle>> scripts = null;
	List<AI> scriptsOponent = null;
	int sizePortfolio = 3;
	HashMap<Long, UnitScriptSingle> playerScripts=null;
	HashMap<Long, UnitScriptSingle> enemyScripts=null;
	List<Unit> playerUnits = new ArrayList<>();
	List<Unit> enemyUnits = new ArrayList<>();

	public DSPGSmRTS(UnitTypeTable utt, DynamicScripting aiAux) {
		this(100, -1, 200, 1, 1, new SimpleSqrtEvaluationFunction3(),
				// new SimpleSqrtEvaluationFunction2(),
				 //new LanchesterEvaluationFunction(),
				utt, new AStarPathFinding(), aiAux);
	}

	public DSPGSmRTS(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, UnitTypeTable a_utt,
			PathFinding a_pf, DynamicScripting aiAux) {
		super(time, max_playouts);

		LOOKAHEAD = la;
		I = a_I;
		R = a_R;
		evaluation = e;
		utt = a_utt;
		pf = a_pf;
		new POLightRush(a_utt);
		DS = aiAux;
		scripts = new HashMap<>();
		scriptsOponent = new ArrayList<>();
		buildPortfolio();
	}

	protected void buildPortfolio() {

		//HashMap<String, ArrayList<Rule>> RulesSpaceUnit = DS.getRulesSpaceUnit();
		ArrayList<CompoundScript> [] bestCompoundScript=DS.getBestCompoundScript();
		String typesUnits[]=DS.getTypesUnits();
//		ArrayList<Rule> heavyS = RulesSpaceUnit.get("Heavy");
//		ArrayList<Rule> lightS = RulesSpaceUnit.get("Light");
//		ArrayList<Rule> rangedS = RulesSpaceUnit.get("Ranged");
		
		ArrayList<CompoundScript> workerS=null;
		ArrayList<CompoundScript> lightS=null;
		ArrayList<CompoundScript> heavyS=null;
		ArrayList<CompoundScript> rangedS=null;
		for(int i=0;i<bestCompoundScript.length;i++)
		{
			if(typesUnits[i].equals("Worker"))
			{
				workerS = bestCompoundScript[i];
			}
			else if(typesUnits[i].equals("Light"))
			{
				lightS = bestCompoundScript[i];
			}
			else if(typesUnits[i].equals("Heavy"))
			{
				heavyS = bestCompoundScript[i];
			}	
			else if(typesUnits[i].equals("Ranged"))
			{
				rangedS = bestCompoundScript[i];
			}
		}
			
		
		//This code is just to validate how was the final rulespace for lights and heavy units
		//System.out.println("PrintingAfter ");

//		for (int j = 0; j < heavyS.size(); j++) {
//			Rule rule = RulesSpaceUnit.get("Heavy").get(j);
////			System.out.println("Final Rule Heavy " + heavyS.get(j).getRule_id() + " "
////					+ heavyS.get(j).getRule_condition() + " " + heavyS.get(j).getRule_action() + " "
////					+ heavyS.get(j).getRule_paramether() + " " + heavyS.get(j).getWeight());
//		}
//		for (int j = 0; j < lightS.size(); j++) {
//			Rule rule = RulesSpaceUnit.get("Light").get(j);
////			System.out.println("Final Rule Light" + lightS.get(j).getRule_id() + " " + lightS.get(j).getRule_condition()
////					+ " " + lightS.get(j).getRule_action() + " " + lightS.get(j).getRule_paramether() + " "
////					+ lightS.get(j).getWeight());
//		}

		scripts = new HashMap<>();
		if(heavyS.size()>0)
		{
			List<UnitScriptSingle> l = new ArrayList<>();
			l.add(new UnitScriptSingle(heavyS.get(0), pf));
			l.add(new UnitScriptSingle(heavyS.get(1), pf));
			l.add(new UnitScriptSingle(heavyS.get(2), pf));
			scripts.put(utt.getUnitType("Heavy"), l);
		}
		if(lightS.size()>0)
		{
			List<UnitScriptSingle> l = new ArrayList<>();
			l.add(new UnitScriptSingle(lightS.get(0), pf));
			l.add(new UnitScriptSingle(lightS.get(1), pf));
			l.add(new UnitScriptSingle(lightS.get(2), pf));
			scripts.put(utt.getUnitType("Light"), l);
		}
		if(rangedS.size()>0)
		{
			List<UnitScriptSingle> l = new ArrayList<>();
			l.add(new UnitScriptSingle(rangedS.get(0), pf));
			l.add(new UnitScriptSingle(rangedS.get(1), pf));
			l.add(new UnitScriptSingle(rangedS.get(2), pf));
			scripts.put(utt.getUnitType("Ranged"), l);
		}

		// this.scripts.add(new POWorkerRush(utt));
		// this.scripts.put(utt.getUnitType("Heavy"), arg1)
		// this.scripts.add(new POLightRush(utt));
		// this.scripts.add(new PORangedRush(utt));
		// this.scripts.add(new EconomyMilitaryRush(utt));

		// this.scripts.add(new POHeavyRush(utt, new FloodFillPathFinding()));
		// this.scripts.add(new POLightRush(utt, new FloodFillPathFinding()));
		// this.scripts.add(new PORangedRush(utt, new FloodFillPathFinding()));
		
        this.scriptsOponent.add(new POHeavyRush(utt));
        this.scriptsOponent.add(new POLightRush(utt));
        this.scriptsOponent.add(new PORangedRush(utt));

	}

	@Override
	public void reset() {

	}
	
    protected void evalPortfolio(int heightMap){
        if(heightMap <= 16 && !portfolioHasWorkerRush()){
            this.scriptsOponent.add(new POWorkerRush(utt));
        }
    }

	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		if (gs.canExecuteAnyAction(player)) {
			evalPortfolio(gs.getPhysicalGameState().getHeight());
			startNewComputation(player, gs);
			return getBestActionSoFar();
		} else {
			return new PlayerAction();
		}

	}

	@Override
	public PlayerAction getBestActionSoFar() throws Exception {

		// pego o melhor script do portfolio para ser a semente
		// AI seedPlayer = getSeedPlayer(playerForThisComputation);
		// AI seedEnemy = getSeedPlayer(1 - playerForThisComputation);
		AI seedPlayer = new UnitScriptsAI(playerScripts, playerUnits, scripts, DS, pf);
		//AI seedEnemy = new UnitScriptsAI(enemyScripts, enemyUnits, scripts, DS, pf);
		AI seedEnemy= getSeedPlayer(1 - playerForThisComputation);

		defaultScript = seedPlayer;

//		UnitScriptData currentScriptData = new UnitScriptData(playerForThisComputation);
//		currentScriptData.setSeedUnits(seedPlayer);
//		setAllScripts(playerForThisComputation, currentScriptData, seedPlayer);
		if ((System.currentTimeMillis() - start_time) < TIME_BUDGET) {
			doPortfolioSearch(playerForThisComputation, seedPlayer, seedEnemy);
		}

		return getFinalAction(playerUnits);
	}

	 protected AI getSeedPlayer(int player) throws Exception {
	 AI seed = null;
	 double bestEval = -9999;
	 AI enemyAI = new POLightRush(utt);
	 //vou iterar para todos os scripts do portfolio
	 for (AI script : scriptsOponent) {
	 double tEval = eval(player, gs_to_start_from, script, enemyAI);
	 if (tEval > bestEval) {
	 bestEval = tEval;
	 seed = script;
	 }
	 }
	
	 return seed;
	 }

	/*
	 * Executa um playout de tamanho igual ao @LOOKAHEAD e retorna o valor
	 */
	public double eval(int player, GameState gs, AI aiPlayer, AI aiEnemy) throws Exception {
		AI ai1 = aiPlayer.clone();
		AI ai2 = aiEnemy.clone();

		GameState gs2 = gs.clone();
		ai1.reset();
		ai2.reset();
		int timeLimit = gs2.getTime() + LOOKAHEAD;
		boolean gameover = false;
		while (!gameover && gs2.getTime() < timeLimit) {
			if (gs2.isComplete()) {
				gameover = gs2.cycle();
			} else {
				gs2.issue(ai1.getAction(player, gs2));
				gs2.issue(ai2.getAction(1 - player, gs2));
			}
		}
		double e = evaluation.evaluate(player, 1 - player, gs2);

		return e;
	}

	/**
	 * Realiza um playout (Dave playout) para calcular o improve baseado nos scripts
	 * existentes.
	 *
	 * @param player
	 * @param gs
	 * @param uScriptPlayer
	 * @param aiEnemy
	 * @return a avaliação para ser utilizada como base.
	 * @throws Exception
	 */
	public double eval(int player, GameState gs, UnitScriptData uScriptPlayer, AI aiEnemy) throws Exception {
		// AI ai1 = defaultScript.clone();
		AI ai2 = aiEnemy.clone();

		GameState gs2 = gs.clone();
		// ai1.reset();
		ai2.reset();
		int timeLimit = gs2.getTime() + LOOKAHEAD;
		boolean gameover = false;
		while (!gameover && gs2.getTime() < timeLimit) {
			if (gs2.isComplete()) {
				gameover = gs2.cycle();
			} else {
				// gs2.issue(ai1.getAction(player, gs2));
				gs2.issue(uScriptPlayer.getAction(player, gs2));
				//
				gs2.issue(ai2.getAction(1 - player, gs2));
			}
		}

		return evaluation.evaluate(player, 1 - player, gs2);
	}

	@Override
	public AI clone() {
		return new DSPGSmRTS(TIME_BUDGET, ITERATIONS_BUDGET, LOOKAHEAD, I, R, evaluation, utt, pf, DS);
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		List<ParameterSpecification> parameters = new ArrayList<>();

		parameters.add(new ParameterSpecification("TimeBudget", int.class, 100));
		parameters.add(new ParameterSpecification("IterationsBudget", int.class, -1));
		parameters.add(new ParameterSpecification("PlayoutLookahead", int.class, 100));
		parameters.add(new ParameterSpecification("I", int.class, 1));
		parameters.add(new ParameterSpecification("R", int.class, 1));
		parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class,
				new SimpleSqrtEvaluationFunction3()));
		parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

		return parameters;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + TIME_BUDGET + ", " + ITERATIONS_BUDGET + ", " + LOOKAHEAD + ", " + I
				+ ", " + R + ", " + evaluation + ", " + pf + ")";
	}

	public int getPlayoutLookahead() {
		return LOOKAHEAD;
	}

	public void setPlayoutLookahead(int a_pola) {
		LOOKAHEAD = a_pola;
	}

	public int getI() {
		return I;
	}

	public void setI(int a) {
		I = a;
	}

	public int getR() {
		return R;
	}

	public void setR(int a) {
		R = a;
	}

	public EvaluationFunction getEvaluationFunction() {
		return evaluation;
	}

	public void setEvaluationFunction(EvaluationFunction a_ef) {
		evaluation = a_ef;
	}

	public PathFinding getPathFinding() {
		return pf;
	}

	public void setPathFinding(PathFinding a_pf) {
		pf = a_pf;
	}

	@Override
	public void startNewComputation(int player, GameState gs) throws Exception {
		
        playerForThisComputation = player;
        gs_to_start_from = gs;
        nplayouts = 0;
        _startTime = gs.getTime();
        start_time = System.currentTimeMillis();
		
		playerUnits = getUnitsPlayer(playerForThisComputation);
		enemyUnits = getUnitsPlayer(1 - playerForThisComputation);

		int n1 = playerUnits.size();
		int n2 = enemyUnits.size();

		playerScripts = new HashMap<>();
		enemyScripts = new HashMap<>();

		for (int i = 0; i < n1; i++)
			playerScripts.put(playerUnits.get(i).getID(), defaultScript(playerUnits.get(i), gs));
		for (int i = 0; i < n2; i++)
			enemyScripts.put(enemyUnits.get(i).getID(), defaultScript(enemyUnits.get(i), gs));
	}

	@Override
	public void computeDuringOneGameFrame() throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	private void setAllScripts(int playerForThisComputation, UnitScriptData currentScriptData, AI seedPlayer) {
		currentScriptData.reset();
		for (Unit u : gs_to_start_from.getUnits()) {
			if (u.getPlayer() == playerForThisComputation) {
				currentScriptData.setUnitScript(u, seedPlayer);
			}
		}
	}

	private void doPortfolioSearch(int player, AI seedPlayer, AI seedEnemy) throws Exception {
		int enemy = 1 - player;
		double bestScore = eval(player, gs_to_start_from, seedPlayer, seedEnemy);
		ArrayList<Unit> unitsPlayer = getUnitsPlayer(player);
		// controle pelo número de iterações
		for (int i = 0; i < I; i++) {
			//System.out.println("Iteration number "+i);
			// fazer o improve de cada unidade
			for (Unit unit : unitsPlayer) {
				// inserir controle de tempo
				if (System.currentTimeMillis() >= (start_time + (TIME_BUDGET - 10))) {
					return;
				}
				// iterar sobre cada script do portfolio
				UnitScriptSingle bestScriptSingle=null;
				for (int j = 0; j < sizePortfolio; j++) {
					UnitScriptSingle candidate = scripts.get(unit.getType()).get(j);	
					playerScripts.put(unit.getID(), candidate);
					AI ai = new UnitScriptsAI(playerScripts, playerUnits, scripts, DS, pf);
					//currentScriptData.setUnitScript(unit, ai);
					double scoreTemp = eval(player, gs_to_start_from, ai, seedEnemy);
					//System.out.println("Candidate "+j+" "+candidate.rule.getRule_condition()+" "+candidate.rule.getRule_action()+" "+candidate.rule.getRule_paramether()+" "+scoreTemp);

					if (scoreTemp > bestScore || bestScriptSingle==null) {
						bestScriptSingle = playerScripts.get(unit.getID());
						bestScore = scoreTemp;
					}
				}
				// seto o melhor vetor para ser usado em futuras simulações
				//currentScriptData = bestScriptData.clone();
				//System.out.println("Candidate "+bestScriptSingle.rule.getRule_condition()+" "+bestScriptSingle.rule.getRule_action()+" "+bestScriptSingle.rule.getRule_paramether());
				playerScripts.put(unit.getID(), bestScriptSingle);
			}
		}
	}

	private ArrayList<Unit> getUnitsPlayer(int player) {
		ArrayList<Unit> unitsPlayer = new ArrayList<>();
		for (Unit u : gs_to_start_from.getUnits()) {
			if (u.getPlayer() == player) {
				unitsPlayer.add(u);
			}
		}

		return unitsPlayer;
	}

	private PlayerAction getFinalAction(List<Unit> playerUnits) throws Exception {
		
		
		AI ai1 = new UnitScriptsAI(playerScripts, playerUnits, scripts, DS,pf);
		PlayerAction pAction = new PlayerAction();
		HashMap<String, PlayerAction> actions = new HashMap<>();
		
		actions.put(ai1.toString(), ai1.getAction(playerForThisComputation, gs_to_start_from));
		
		for (Unit u : playerUnits) {

			//Rule r=playerScripts.get(u.getID()).rule;
			//System.out.print("Selected "+r.getRule_condition()+" "+r.getRule_action()+" "+r.getRule_paramether());
			UnitAction unt = actions.get(ai1.toString()).getAction(u);
			if (unt != null) {
				pAction.addUnitAction(u, unt);
			}
		}

		return pAction;
	}

	public UnitScriptSingle defaultScript(Unit u, GameState gs) {
		// the first script added per type is considered the default:
		List<UnitScriptSingle> l = scripts.get(u.getType());
		return l.get(0);
	}
	
    private boolean portfolioHasWorkerRush() {
        for (AI script : scriptsOponent) {
            if(script.toString().contains("POWorkerRush")){
                return true;
            }
        }
        return false;
    }

}
