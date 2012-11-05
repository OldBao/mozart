/*
 *  @Brief  chord means multiple clefs combination
 * 
 *  @Author zhanggx
 *  @Date 2012-10-26
 * 
 */
package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Page;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owls.process.CompositeProcess;

public abstract class StructChord extends Notation {
    	public StructChord(){
    		
    	}
		public StructChord(Page cpnPage) {
			super(cpnPage);
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
