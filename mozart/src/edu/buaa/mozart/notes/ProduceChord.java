package edu.buaa.mozart.notes;


import org.mindswap.owls.process.Produce;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class ProduceChord extends DataChord {

	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
		Produce produce = (Produce)mIndividual;
        context.setConsturct(produce);
        composer.composeProduce(produce, this, context);
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
