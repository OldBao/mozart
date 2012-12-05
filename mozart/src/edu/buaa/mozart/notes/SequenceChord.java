package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Transition;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Sequence;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class SequenceChord extends StructChord{

    public SequenceChord(){
    	
    }

	@Override
	public void compose(final Composer composer, NotationContext context)	throws ComposeException {
			assert(mIndividual instanceof Sequence);
            Sequence sequence = (Sequence)mIndividual;
        	composer.composeSequenceChrod(sequence, this, context);
	}

	@Override
	boolean hasInputTransition() {
		return true;
	}

	@Override
	boolean hasOutputTransition() {
		return true;
	}
    
    @Override
	public 
    Transition getInputTransition() throws ComposeException{
    	Sequence sequence = (Sequence)mIndividual;
        ControlConstruct first = sequence.getConstructs().get(0);
    	DataChord dc = (DataChord) first.getMozartNotation();
        return dc.getInputTransition();
    }
    
    public 
    Transition getOutputTransition() throws ComposeException{
    	Sequence sequence = (Sequence)mIndividual;
        OWLIndividualList<ControlConstruct> list = sequence.getConstructs();
        ControlConstruct last = list.get(list.size() - 1); 
    	DataChord dc = (DataChord) last.getMozartNotation();
        return dc.getInputTransition();
    }
    
}
