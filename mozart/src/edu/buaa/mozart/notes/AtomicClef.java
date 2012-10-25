package edu.buaa.mozart.notes;

import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;
import org.mindswap.owls.process.Process;

import edu.buaa.utils.QuickFactory;


public class AtomicClef extends Clef{

	public AtomicClef(Page cpnPage) {
		super(cpnPage);
	}

	@Override
	public void compose(Process process) throws ComposeException {
		if (mCPNPage == null)
			throw new ComposeException("set petri net first!");
	
		Transition wsTransition = 
				QuickFactory.getTransition(mCPNPage, process.getLocalName());
		
		//create web service input place
		Place atomicInputPlace =
				QuickFactory.getPlace(mCPNPage, "Input");
		
		//create web service output place
		Place atomicOutputPlace =
				QuickFactory.getPlace(mCPNPage, "Output");
		
		QuickFactory.combine(mCPNPage, atomicInputPlace, wsTransition);
		QuickFactory.combine(mCPNPage, wsTransition, atomicOutputPlace);
		QuickFactory.combine(mCPNPage, mStartTransition, atomicInputPlace);
		QuickFactory.combine(mCPNPage, atomicOutputPlace, mEndTransition);
	}
	
}
