package edu.buaa.composer;

import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Choice;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.IfThenElse;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Produce;
import org.mindswap.owls.process.Sequence;

import edu.buaa.mozart.notes.AtomicClef;
import edu.buaa.mozart.notes.ChoiceChord;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.notes.CompositeClef;
import edu.buaa.mozart.notes.Conclude;
import edu.buaa.mozart.notes.IfThenElseChord;
import edu.buaa.mozart.notes.PerformChord;
import edu.buaa.mozart.notes.Prelude;
import edu.buaa.mozart.notes.ProduceChord;
import edu.buaa.mozart.notes.SequenceChord;

public interface ComposerAbility {
	abstract public void composeAtomicClef(AtomicProcess process,
			AtomicClef clef, NotationContext context);

	abstract public void composeSequenceChrod(Sequence sequenceProcess,
			SequenceChord chrod, NotationContext context);

	abstract public void composeCompositeChord(CompositeProcess process,
			CompositeClef chord, NotationContext context);

	abstract public void composePerform(Perform perform, PerformChord chord,
			NotationContext context) throws ComposeException;

    abstract public void composePrelude(Process process, Prelude prelude, NotationContext context);
	abstract public void composeConclude(Process process, Conclude conclude, NotationContext context)
			throws ComposeException;

	abstract public void composeProduce(Produce produce, ProduceChord chrod,
			NotationContext context) throws ComposeException;

	abstract public void composeChoice(Choice choice, ChoiceChord choiceChord,
			NotationContext context) throws ComposeException;

	abstract public void composeIfThenElse(IfThenElse ite,
			IfThenElseChord iteChord, NotationContext context)
			throws ComposeException;
}
