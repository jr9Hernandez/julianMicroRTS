 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import ai.core.AI;
import ai.*;
import ai.abstraction.LightRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.mcts.believestatemcts.BS3_NaiveMCTS;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.mcts.uct.UCT;
import ai.minimax.ABCD.ABCD;
import ai.montecarlo.MonteCarlo;
import ai.portfolio.PortfolioAI;
import ai.portfolio.portfoliogreedysearch.PGSAI;
import ai.puppet.PuppetSearchMCTS;
import gui.PhysicalGameStatePanel;
import java.io.OutputStreamWriter;
import javax.swing.JFrame;

import DSPGSAI.DSPGSAI;
import DSPGS_Churchill.DSPGSmRTS;
import PGS_Churchill.PGSmRTS;
import dynamicscripting.DynamicScripting;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import util.XMLWriter;

/**
 *
 * @author santi
 */
public class GameVisualSimulationTest { 
    public static void main(String args[]) throws Exception {
    	
    	
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = null;
        AI ai1=null;
        //int enemy=Integer.parseInt(args[0]);
        int enemy=1;
        //pgs = PhysicalGameState.load("maps/24x24/melee24x24Mixed16.xml", utt);        
        
        if (enemy==1) {
        	ai1 = new RandomBiasedAI(utt);
        }else if (enemy==2) {
        	ai1 = new WorkerRush(utt,new BFSPathFinding()); 
        } else if (enemy==3) {
        	ai1=new LightRush(utt);
        } else if (enemy==4) {
        	ai1= new PGSAI(utt);
        } else if (enemy==5) {        	
        	ai1=new ABCD(utt);
        } else if (enemy==6) {        	
    		ai1=new POHeavyRush(utt);
    	} else if (enemy==7) {        	
    		ai1=new POLightRush(utt);
    	} else if (enemy==8) {        	
    		ai1=new PORangedRush(utt);
    	} else if (enemy==9) {        	
    		ai1=new PGSmRTS(utt);
    	} else if (enemy==10) {        	
    		ai1=new UCT(utt);
    	} else if (enemy==11) {        	
    		ai1=new MonteCarlo(utt);
    	} else if (enemy==12) {        	
    		ai1=new PuppetSearchMCTS(utt);
    	} else if (enemy==13) {        	
    		ai1=new NaiveMCTS(utt);
    	} 
        
        for(int i=0;i<6;i++)
        {
        if (i==0) {
            pgs = PhysicalGameState.load("maps/8x8/melee8x8Mixed4.xml", utt);
        } else if (i==1) {
            pgs = PhysicalGameState.load("maps/16x16/melee16x16Mixed8.xml", utt);
        } else if (i==2) {
            pgs = PhysicalGameState.load("maps/16x16/melee16x16Mixed12.xml", utt);
        } else if (i==3) {
            pgs = PhysicalGameState.load("maps/24x24/melee24x24Mixed16.xml", utt);
        } else if (i==4) {
        	pgs = PhysicalGameState.load("maps/24x24/melee24x24Mixed24.xml", utt);
        }else if (i==5) {
        	pgs = PhysicalGameState.load("maps/BWDistantResources32x32Mele.xml", utt);
        }
                        

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 2000;
        int PERIOD = 20;
        boolean gameover = false;
        

        //AI ai2 = new DynamicScripting(utt,enemy);        
        DynamicScripting aiAux = new DynamicScripting(utt,enemy);        

//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);

        //The next line is just for my Experiments!!!
        //ai2.getAction(1, gs);
        aiAux.getAction(1, gs);
        //AI ai2 = new PGSAI(utt);
        //AI ai2 = new DSPGSAI(utt,aiAux);
        //AI ai2 = new PGSmRTS(utt);
        AI ai2 = new DSPGSmRTS(utt,aiAux);
        //AI ai2 = new PGSmRTS(utt);
        //AI ai2=aiAux;
        
        
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do{
            if (System.currentTimeMillis()>=nextTimeToUpdate) {
                PlayerAction pa1 = ai1.getAction(0, gs);
                PlayerAction pa2 = ai2.getAction(1, gs);
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

                // simulate:
                gameover = gs.cycle();
                //w.repaint();
                nextTimeToUpdate+=PERIOD;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }while(!gameover && gs.getTime()<MAXCYCLES);
        
        System.out.print(gs.winner()+" ");
        //System.out.println("Game Over");
        }
        System.out.println("");
       
    
    }
}
