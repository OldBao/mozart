package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Page;
import org.mindswap.owls.process.Sequence;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class SequenceChord extends StructChord{

    public SequenceChord(){
    	
    }
	public SequenceChord(Page cpnPage) {
		super(cpnPage);
	}

	@Override
	public void compose(final Composer composer, NotationContext context)	throws ComposeException {
			assert(mIndividual instanceof Sequence);
            Sequence sequence = (Sequence)mIndividual;
        	composer.composeSequenceChrod(sequence, this, context);
	}
    
}
