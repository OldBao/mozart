package edu.buaa.mozart.notes;

import org.mindswap.owls.process.CompositeProcess;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class CompositeClef extends Clef{

	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
		CompositeProcess process = (CompositeProcess) mIndividual;
		composer.composeCompositeChord(process, this, context);
	}

}
