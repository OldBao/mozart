package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Transition;

public abstract class Chord extends Notation{
    protected Transition mInputTransition, mOutputTransition;
    
    abstract boolean hasInputTransition();
    abstract boolean hasOutputTransition();
    
    public Transition 
    getInputTransition() throws ComposeException 
    {
    	if (!hasInputTransition())
    		throw new ComposeException("has no input transition");
    	else {
    		if (mInputTransition == null)
    			throw new ComposeException("Please set input Trasition first");
            return mInputTransition;
    	}
    }
    
    public void setInputTransition(Transition transition) throws ComposeException{
    	if (!hasInputTransition()){
    		throw new ComposeException("has no input Transition");
    	} else {
    		mInputTransition = transition; 
    	}
    }
    public Transition getOutputTransition() throws ComposeException 
    {
    	if (!hasOutputTransition())
    		throw new ComposeException("has no output transition");
    	else {
    		if (mOutputTransition == null)
    			throw new ComposeException("Please set output Trasition first");
            return mOutputTransition;
    	}
    }
    
    public void setOutputTransition(Transition transition) throws ComposeException
    {
    	if (!hasOutputTransition()){
    		throw new ComposeException("has no output Transition");
    	} else {
    		mOutputTransition = transition; 
    	}
    }

}
