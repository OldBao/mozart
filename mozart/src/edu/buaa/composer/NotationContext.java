package edu.buaa.composer;


import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.MozartDataConstruct;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Local;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.mozart.notes.Conclude;
import edu.buaa.mozart.notes.Prelude;

public class NotationContext implements Cloneable{
	private final OWLIndividualList<ProcessVar> mVarOWLIndividualList;
    private MozartDataConstruct mCurConstruct;
	private Prelude mPrelude;
	private Conclude mConclude;
    public NotationContext(){
        mPrelude = new Prelude();
        mConclude = new Conclude();
    	mVarOWLIndividualList = OWLFactory.createIndividualList();
    }
    
	public int addInputs(final OWLIndividualList<Input> inputs)
	{
		return addValues(inputs);
	}

	public int addLocals(final OWLIndividualList<Local> locals)
	{
		return addValues(locals);
	}
	
	public int addOutputs(final OWLIndividualList<Output> inputs)
	{
		return addValues(inputs);
	}
    
    public int addOutput(final Output output) {
    	OWLIndividualList tmpList = OWLFactory.createIndividualList();
    	tmpList.add(output);
    	return addOutputs(tmpList);
    }

	public OWLIndividualList<Input> getInputs()
	{
		return getValues(Input.class);
	}

	public OWLIndividualList<Local> getLocals()
	{
		return getValues(Local.class);
	}

	public OWLIndividualList<Output> getOutputs()
	{
		return getValues(Output.class);
	}

	public OWLIndividualList<ProcessVar> getValues()
	{
		return mVarOWLIndividualList;
	}
	private <P extends ProcessVar>
	int addValues(final OWLIndividualList<P> vars)
	{
        for (P v : vars){
        	mVarOWLIndividualList.add(v);
        }
        return 0;
	}

	private <P extends ProcessVar> OWLIndividualList<P> getValues(final Class<P> paramType)
	{
		OWLIndividualList<P> result = OWLFactory.createIndividualList();
		for (ProcessVar entry : mVarOWLIndividualList)
		{
			if (paramType.isInstance(entry)) result.add(paramType.cast(entry));
		}
		return result;
	}

	public MozartDataConstruct getConstruct() {
		return mCurConstruct;
	}

	public void setConstruct(MozartDataConstruct mCurConstruct) {
		this.mCurConstruct = mCurConstruct;
	}

	public Prelude getPrelude() {
		return mPrelude;
	}

	public void setPrelude(Prelude mPrelude) {
		this.mPrelude = mPrelude;
	}

	public Conclude getConclude() {
		return mConclude;
	}

	public void setConclude(Conclude mConclude) {
		this.mConclude = mConclude;
	}
}
