package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Place;
import org.mindswap.owls.process.Process;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class Prelude extends DataChord {
	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
        Process process = (Process)mIndividual;
        composer.composePrelude(process, this, context);
	}
    
    @Override
    public String toString(){
    	return "Prelude";
    }
    
    public Place getPlace(){
    	return mDataPlace;
    }
    
    
	@Override
	boolean hasInputTransition() {
		return false;
	}

	@Override
	boolean hasOutputTransition() {
		return true;
	}

}
