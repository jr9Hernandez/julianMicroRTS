package DynamicScripting;

import java.util.ArrayList;
import java.util.List;

import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PlayerAction;
import rts.PlayerActionGenerator;
import rts.units.UnitTypeTable;

public class DynamicScripting extends AIWithComputationBudget {
    UnitTypeTable m_utt = null;

    // This is the default constructor that microRTS will call:
    public DynamicScripting(UnitTypeTable utt) {
        super(-1,-1);
        m_utt = utt;
    }

    // This will be called by microRTS when it wants to create new instances of this bot (e.g., to play multiple games).
    public AI clone() {
        return new DynamicScripting(m_utt);
    }
    
    // This will be called once at the beginning of each new game:    
    public void reset() {
    }
       
    // Called by microRTS at each game cycle.
    // Returns the action the bot wants to execute.
    public PlayerAction getAction(int player, GameState gs) {
        try {
            if (!gs.canExecuteAnyAction(player)) return new PlayerAction();
            PlayerActionGenerator pag = new PlayerActionGenerator(gs, player);
            return pag.getRandom();
        }catch(Exception e) {
            // The only way the player action generator returns an exception is if there are no units that
            // can execute actions, in this case, just return an empty action:
            // However, this should never happen, since we are checking for this at the beginning
            return new PlayerAction();
        }
    }   
    
    // This will be called by the microRTS GUI to get the
    // list of parameters that this bot wants exposed
    // in the GUI.
    public List<ParameterSpecification> getParameters()
    {
        return new ArrayList<>();
    }
}