package br.com.fernando.browsewords;

import static java.lang.System.out;

public class Test {

    public static void main(String[] args) {
	out.println("Hello");
	
	var sum = ((Operation)(o1, o2) -> o1 + o2).perform(0, 0);
	
    }
 
    
    interface Operation {
	int perform (int o1, int o2);
    }
}
