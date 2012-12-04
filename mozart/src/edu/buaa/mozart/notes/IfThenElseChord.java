package edu.buaa.mozart.notes;

import org.mindswap.owls.process.IfThenElse;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class IfThenElseChord extends StructChord {

	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
        IfThenElse ite = (IfThenElse) mIndividual;
        composer.composeIfThenElse(ite, this, context);
	}

}
