package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Place;
import org.mindswap.owls.process.Perform;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;
import edu.buaa.mozart.ML.CodeSegment;

public abstract class DataChord extends Chord{

    protected CodeSegment mCodeSegment;
    
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
	public CodeSegment getCodeSegment() {
		return mCodeSegment;
	}
	public void setCodeSegment(CodeSegment mCodeSegment) {
		this.mCodeSegment = mCodeSegment;
	}
}
