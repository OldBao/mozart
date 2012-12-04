package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;
import org.mindswap.owls.process.Process;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class ConcludeNotation extends Notation {

    private Place mPlace;
    
    public ConcludeNotation(Place place){
    	mPlace = place;
    }
	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {

        Process process = (Process)mIndividual;
		composer.composeConclude(process.getResults(),process.getOutputs(),context);
	}
    
       public Place getPlace(){
    	   return mPlace;
       }
}
