package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Place;
import org.mindswap.owls.process.Process;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class Conclude extends DataChord{

    private Place mPlace;
    
    public Conclude(){
    }
	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {

        Process process = (Process)mIndividual;
		composer.composeConclude(process, this,context);
	}
    
       public Place getPlace(){
    	   return mPlace;
       }
	@Override
	boolean hasInputTransition() {
		return true;
	}
	@Override
	boolean hasOutputTransition() {
		return false;
	}
}
