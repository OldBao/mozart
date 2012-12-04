package edu.buaa.composer;

import java.util.Map.Entry;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Local;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.process.variable.ProcessVar;
import org.mindswap.query.ValueMap;

public class NotationContext {
	private final OWLIndividualList<ProcessVar> mVarOWLIndividualList;
    
    public NotationContext(){
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
}
