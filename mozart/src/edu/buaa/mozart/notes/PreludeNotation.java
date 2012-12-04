package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class PreludeNotation extends Notation {

    private Place mPlace;
    
    public PreludeNotation(Place place) {
    	mPlace = place;
    }
	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
	}
    
    
    @Override
    public String toString(){
    	return "Prelude";
    }
    
    public Place getPlace(){
    	return mPlace;
    }

}
