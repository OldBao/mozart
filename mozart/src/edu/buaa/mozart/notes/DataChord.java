package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Place;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public abstract class DataChord extends Chord{

    public DataChord(){}
    
    protected Place mDataPlace;
 
    public void setDataPlace(Place place){
    	mDataPlace = place;
    }
    public Place getDataplace(){
    	return mDataPlace;
    }
    
	abstract public void compose(Composer composer, NotationContext context)
			throws ComposeException;
}
