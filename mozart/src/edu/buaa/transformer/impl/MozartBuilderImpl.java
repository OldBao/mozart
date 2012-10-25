package edu.buaa.transformer.impl;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.Name;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.impl.ModelFactoryImpl;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;

import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.buaa.transformer.MozartBuilder;
import edu.buaa.utils.IDFactory;

public class MozartBuilderImpl implements MozartBuilder{

	@Override
	public PetriNet transform(Service service) {	
		Process process = service.getProcess();
		if (process instanceof AtomicProcess){
			return getPetriNetFromAtomicProcess((AtomicProcess) process);
		}
			
		return null;
	}

	private PetriNet getPetriNetFromAtomicProcess(AtomicProcess process){
		PetriNet net = ModelFactoryImpl.eINSTANCE.createPetriNet();
		
		Page page = ModelFactoryImpl.eINSTANCE.createPage();
		
		page.setId(IDFactory.getInstance().getRandomId());
		Name pageName = ModelFactoryImpl.eINSTANCE.createName(); 
		pageName.setText(process.getLocalName());
		page.setName(pageName);
		page.setPetriNet(net);
		
		//create start and action transistion
		Transition atomicStartTransition = ModelFactoryImpl.eINSTANCE.createTransition();
		atomicStartTransition.setId(IDFactory.getInstance().getRandomId());
		Name startName = ModelFactoryImpl.eINSTANCE.createName();
		startName.setText("atomic service start");
		atomicStartTransition.setName(startName);
		atomicStartTransition.setPage(page);
		
		Transition atomicActionTransition = ModelFactoryImpl.eINSTANCE.createTransition();
		atomicActionTransition.setId(IDFactory.getInstance().getRandomId());
		Name actionName = ModelFactoryImpl.eINSTANCE.createName();
		actionName.setText("atomic service end");
		atomicActionTransition.setName(actionName);
		atomicActionTransition.setPage(page);
		
		//create web service place
		Place atomicReadyPlace = ModelFactoryImpl.eINSTANCE.createPlace();
		atomicReadyPlace.setId(IDFactory.getInstance().getRandomId());
		Name readyPlaceName = ModelFactoryImpl.eINSTANCE.createName();
		readyPlaceName.setText("ready to fire");
		atomicReadyPlace.setName(readyPlaceName);
		atomicReadyPlace.setPage(page);

		Arc startArc = ModelFactoryImpl.eINSTANCE.createArc();
		startArc.setId(IDFactory.getInstance().getRandomId());
		startArc.setSource(atomicStartTransition);
		startArc.setTarget(atomicReadyPlace);
		startArc.setPage(page);
		
		Arc endArc = ModelFactoryImpl.eINSTANCE.createArc();
		endArc.setId(IDFactory.getInstance().getRandomId());
		endArc.setSource(atomicReadyPlace);
		endArc.setTarget(atomicActionTransition);
		endArc.setPage(page);
		
		//create input arcs
		for (Input input : process.getInputs()){
			Place fakePlace = ModelFactoryImpl.eINSTANCE.createPlace();
			fakePlace.setId(IDFactory.getInstance().getRandomId());
			Name fakePlaceName = ModelFactoryImpl.eINSTANCE.createName();
		   fakePlaceName.setText(input.getLocalName());
			fakePlace.setName(fakePlaceName);
			fakePlace.setPage(page);
			
			Arc arc = ModelFactoryImpl.eINSTANCE.createArc();
			arc.setId(IDFactory.getInstance().getRandomId());
			arc.setTarget(atomicStartTransition);
			arc.setSource(fakePlace);
			arc.setPage(page);
		}
		
		//create output arcs
		for (Output output : process.getOutputs()){
			Place fakePlace = ModelFactoryImpl.eINSTANCE.createPlace();
			fakePlace.setId(IDFactory.getInstance().getRandomId());
			Name fakePlaceName = ModelFactoryImpl.eINSTANCE.createName();
			fakePlaceName.setText(output.getLocalName());
			fakePlace.setName(fakePlaceName);
			fakePlace.setPage(page);
			
			Arc arc = ModelFactoryImpl.eINSTANCE.createArc();
			arc.setId(IDFactory.getInstance().getRandomId());
			arc.setSource(atomicActionTransition);
			arc.setTarget(fakePlace);
			arc.setPage(page);
		}
		
		return net;
	}
	
	PetriNet getPetriNetFromCompositeProcess(CompositeProcess process){
		PetriNet net = ModelFactoryImpl.eINSTANCE.createPetriNet();
		
		Page page = ModelFactoryImpl.eINSTANCE.createPage();
		
		page.setId(IDFactory.getInstance().getRandomId());
		Name pageName = ModelFactoryImpl.eINSTANCE.createName(); 
		pageName.setText(process.getLocalName());
		page.setName(pageName);
		page.setPetriNet(net);
		
		ControlConstruct construct = process.getComposedOf();
	
		return net;
	}
	
	void setConstruct(Page page, ControlConstruct construct, Place inPlace, Place outPlace){
		if (construct instanceof Sequence){
			OWLIndividualList<ControlConstruct> constructs = 
					construct.getConstructs();
			for (ControlConstruct subConstruct: constructs){
				if (subConstruct instanceof AtomicProcess){
					
				}
			}
		}
	}
	
}
