package edu.buaa.mozart.notes;


import org.mindswap.owls.process.AtomicProcess;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class AtomicClef extends Clef{
    public AtomicClef(){
    	
    }

	@Override
    public String toString(){
		return "Atomic Clef  " +  ((AtomicProcess)mIndividual).getLocalName();
    }
    
	@Override
	public void compose(Composer composer, NotationContext context)throws ComposeException {
        assert(mIndividual instanceof AtomicProcess);
        AtomicProcess process = (AtomicProcess)mIndividual;
        composer.composeAtomicClef(process, this, context);
	}
	
}
