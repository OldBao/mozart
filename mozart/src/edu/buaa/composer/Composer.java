package edu.buaa.composer;

import org.cpntools.accesscpn.model.PetriNet;
import org.mindswap.owls.process.Process;

import edu.buaa.mozart.notes.ComposeException;

public abstract class Composer implements ComposerAbility{
	abstract public PetriNet Compose(Process process) throws ComposeException;
}
