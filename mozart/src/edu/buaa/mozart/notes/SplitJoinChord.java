package edu.buaa.mozart.notes;

import org.mindswap.owls.process.SplitJoin;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class SplitJoinChord extends StructChord {

	@Override
	boolean hasInputTransition() {
		return true;
	}

	@Override
	boolean hasOutputTransition() {
		return true;
	}

	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
		SplitJoin sj = (SplitJoin)mIndividual;
		composer.composeSplitJoin(sj, this, context);
	}

}
