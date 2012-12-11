package edu.buaa.mozart.notes;

import java.util.List;

import org.cpntools.accesscpn.model.Transition;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Sequence;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class SequenceChord extends StructChord{

    public SequenceChord(){
    	
    }

    private List<ControlConstruct> mConstructs;
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
    
}
