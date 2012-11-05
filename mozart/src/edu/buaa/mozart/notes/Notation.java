package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.Transition;
import org.mindswap.owl.OWLIndividual;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;
import edu.buaa.utils.QuickFactory;

/**
 * @author zhanggx
 *	 @description
 */
public abstract class Notation {

	protected Notation(){
	}
    
	protected Notation(Page cpnPage){
		setCPNPage(cpnPage);
	}
	
	protected Transition mStartTransition;
	protected Transition mEndTransition;
	protected Page           mCPNPage;
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
    
    public void setStartTransition(Transition trans) {
    	mStartTransition = trans;
    }

    public void setEndTransition(Transition trans) {
    	mEndTransition = trans;
    }
    
	public Transition getStartTransition() throws ComposeException {
        if (mCPNPage == null)
        	throw new ComposeException("Please set cpn page first"); 
        if (null == mStartTransition)
        	mStartTransition = QuickFactory.getTransition(mCPNPage, "InputBinding");
		return mStartTransition;
	}

	public Transition getEndTransition() throws ComposeException {
        if (mCPNPage == null)
        	throw new ComposeException("Please set cpn page first"); 
        if (mEndTransition == null)
        	mEndTransition = QuickFactory.getTransition(mCPNPage, "OutputBinding");
		return mEndTransition;
	}

	/**
	 * @return the mNotationName
	 */
	public String getNotationName() {
		return mNotationName;
	}

	/**
	 * @param mNotationName the mNotationName to set
	 */
	public void setNotationName(String mNotationName) {
		this.mNotationName = mNotationName; 
	}

	/**
	 * @return the mIndividual
	 */
	public OWLIndividual getIndividual() {
		return mIndividual;
	}

	/**
	 * @param mIndividual the mIndividual to set
	 */
	public void setIndividual(OWLIndividual mIndividual) {
		this.mIndividual = mIndividual;
	}
	/**
	 * @return the mCPNPage
	 */
	public Page getCPNPage() {
		return mCPNPage;
	}
	/**
	 * @param mCPNPage the mCPNPage to set
	 */
	public void setCPNPage(Page mCPNPage) {
		this.mCPNPage = mCPNPage;
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
