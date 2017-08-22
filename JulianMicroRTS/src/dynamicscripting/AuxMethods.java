package dynamicscripting;

import java.util.Random;

public class AuxMethods {

	public AuxMethods(){
		
	}
	
	public int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

}
