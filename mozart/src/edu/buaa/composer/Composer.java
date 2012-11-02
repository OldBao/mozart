package edu.buaa.composer;

import org.cpntools.accesscpn.model.PetriNet;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Sequence;

import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.notes.PerformChord;
import edu.buaa.mozart.notes.SequenceChord;

public abstract class Composer implements ComposerAbility{
	abstract public PetriNet Compose(Process process) throws ComposeException;

}
