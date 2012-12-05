package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Place;

public abstract class Clef extends Notation{
    public Clef(){
    	
    }
    
    protected Place mInputPlace, mOutputPlace;
    
    public Place getInputPlace(){
    	return mInputPlace;
    }
    public Place getOutputPlace(){
    	return mOutputPlace;
    }
    
    public void setInputPlace(Place place) {
    	mInputPlace = place;
    }
    
    public void setOutputPlace(Place place) {
    	mOutputPlace = place;
    }
}
