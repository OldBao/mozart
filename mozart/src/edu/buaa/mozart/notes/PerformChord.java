package edu.buaa.mozart.notes;

import org.mindswap.owls.process.Perform;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class PerformChord extends DataChord{

    public PerformChord(){}
    
    public String toString(){
    	return "Perform " + mIndividual;
    }
    
	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
		assert(mIndividual instanceof Perform);
        Perform perform = (Perform)mIndividual;
        
        context.setConsturct(perform);
        composer.composePerform(perform, this, context);
	}

	@Override
	boolean hasInputTransition() {
		return true;
	}

	@Override
	boolean hasOutputTransition() {
		return true;
	}

}
