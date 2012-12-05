/*
 *  @Brief  chord means multiple clefs combination
 * 
 *  @Author zhanggx
 *  @Date 2012-10-26
 * 
 */
package edu.buaa.mozart.notes;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owls.process.CompositeProcess;

public abstract class StructChord extends Chord {
    	public StructChord(){
    		
    	}
        
		@Override
        public void setIndividual(OWLIndividual individual) {
        	if (individual instanceof CompositeProcess) {
        		mIndividual = ((CompositeProcess)individual).getComposedOf();
        	} else {
        		mIndividual = individual;
        	}
        }
}
