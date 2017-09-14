 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import ai.core.AI;
import ai.*;
import ai.abstraction.LightRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.minimax.ABCD.ABCD;
import ai.montecarlo.MonteCarlo;
import ai.portfolio.PortfolioAI;
import ai.portfolio.portfoliogreedysearch.PGSAI;
import ai.puppet.PuppetSearchMCTS;
import gui.PhysicalGameStatePanel;
import java.io.OutputStreamWriter;
import javax.swing.JFrame;

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
        int enemy=Integer.parseInt(args[0]);
        //pgs = PhysicalGameState.load("maps/24x24/melee24x24Mixed16.xml", utt);        
        
        if (enemy==1) {
        	AI ai1 = new WorkerRush(utt, new BFSPathFinding()); 
        } else if (enemy==2) {
        	AI ai1=new LightRush(utt);
        } else if (enemy==3) {
        	AI ai1= new PGSAI(utt);
        } else if (enemy==4) {        	
        	AI ai1=new ABCD(utt);
        } 
        
        for(int i=0;i<4;i++)
        {
        if (i==0) {
            pgs = PhysicalGameState.load("maps/8x8/melee8x8Mixed4.xml", utt);
        } else if (i==1) {
            pgs = PhysicalGameState.load("maps/16x16/melee16x16Mixed8.xml", utt);
        } else if (i==2) {
            pgs = PhysicalGameState.load("maps/16x16/melee16x16Mixed12.xml", utt);
        } else if (i==3) {
            pgs = PhysicalGameState.load("maps/24x24/melee24x24Mixed16.xml", utt);
        }

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 5000;
        int PERIOD = 20;
        boolean gameover = false;
        

        AI ai2 = new DynamicScripting(utt,enemy);

//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
 //       JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);

        //The next line is just for my Experiments!!!
        ai2.getAction(1, gs);
//        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
//        do{
//            if (System.currentTimeMillis()>=nextTimeToUpdate) {
//                PlayerAction pa1 = ai1.getAction(0, gs);
//                PlayerAction pa2 = ai2.getAction(1, gs);
//                gs.issueSafe(pa1);
//                gs.issueSafe(pa2);
//
//                // simulate:
//                gameover = gs.cycle();
////                w.repaint();
//                nextTimeToUpdate+=PERIOD;
//            } else {
//                try {
//                    Thread.sleep(1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }while(!gameover && gs.getTime()<MAXCYCLES);
        
        //System.out.println("Game Over");
        }
    }    
        
}
