package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Page;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Sequence;

public class SequenceChrod extends Chord{

	public SequenceChrod(Page cpnPage) {
		super(cpnPage);
	}

	@Override
	public void compose(Process process)
			throws ComposeException {
		assert (process instanceof Sequence);
	}

}
