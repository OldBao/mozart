package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.Transition;
import org.mindswap.owl.OWLIndividual;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;
import edu.buaa.utils.QuickFactory;

/**
 * @author zhanggx
 *	 @description Notation is the top class of compose unit
 *
 */
public abstract class Notation {

    public Notation(String notationName) {
    	mNotationName = notationName;
    }
    
    protected static int mIndex = 0;
	public Notation(){
        mNotationName = "" + mIndex++;
	}
	
	protected String        	 mNotationName;
    protected OWLIndividual mIndividual;
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Notation))
        	return false;
        if (obj.getClass() != this.getClass()) {
        	return false;
        }
        Notation notation = (Notation)obj;
        return this.mIndividual.equals(notation.getIndividual());
    }
    
    @Override
    public int hashCode(){
        if (mIndividual != null)
        	return mIndividual.hashCode();
        else 
        	return toString().hashCode();
    }
    
    
	public String getNotationName() {
		return mNotationName;
	}

	public void setNotationName(String mNotationName) {
		this.mNotationName = mNotationName; 
	}
    
	public OWLIndividual getIndividual() {
		return mIndividual;
	}

	public void setIndividual(OWLIndividual mIndividual) {
		this.mIndividual = mIndividual;
	}
    
    protected boolean hasInput(){
    	return true;
    }
    
    protected boolean hasOutput(){
    	return true;
    }
	abstract public void compose(Composer composer, NotationContext context)
			throws ComposeException;
}
