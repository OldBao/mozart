package edu.buaa.composer.impl;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.Name;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;

import edu.buaa.composer.Composer;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.notes.Notation;
import edu.buaa.utils.IDFactory;
import edu.buaa.utils.QuickFactory;

import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.variable.Binding;

public class Mozart extends Composer{
	public Mozart(){
	}
	
	@Override
	public PetriNet Compose(Process process) throws ComposeException {
		PetriNet net 		   = QuickFactory.getPetriNet();
		Page        page        = QuickFactory.getPage(net, process.getLocalName());
	
		Notation notation = Notation.getNotationFromProcess(process, page);
		notation.compose(process);
		process.setNotation(notation);
		
		Place gStartPlace = QuickFactory.getPlace(page, "Start");
		Place gEndPlace  = QuickFactory.getPlace(page, "Finish");
		Arc startArc = 
		QuickFactory.combine(page, gStartPlace, process.getNotation().getStartTransition());
		Arc finishArc = 
		QuickFactory.combine(page, process.getNotation().getEndTransition(), gEndPlace);
		
		//TODO tuple for all inputs and outputs
		
		return net;
	}
}
