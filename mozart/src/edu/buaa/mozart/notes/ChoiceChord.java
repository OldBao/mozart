package edu.buaa.mozart.notes;

import org.mindswap.owls.process.Choice;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public class ChoiceChord extends StructChord {

	@Override
	public void compose(Composer composer, NotationContext context)
			throws ComposeException {
		Choice choice = (Choice) mIndividual;
        composer.composeChoice(choice, this, context);
	}

}
