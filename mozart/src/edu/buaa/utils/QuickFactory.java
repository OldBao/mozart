package edu.buaa.utils;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.Name;
import org.cpntools.accesscpn.model.Node;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.impl.ModelFactoryImpl;

public class QuickFactory {
	public static Place getPlace(Page page, String name ) {
		Place place = ModelFactoryImpl.eINSTANCE.createPlace();
		place.setId(IDFactory.getInstance().getRandomId());
		Name placeName = ModelFactoryImpl.eINSTANCE.createName();
		placeName.setText(name);
		place.setName(placeName);
		place.setPage(page);	
		return place;
	}
	
	public static Transition getTransition(Page page, String name ) {
		Transition trans = ModelFactoryImpl.eINSTANCE.createTransition();
		trans.setId(IDFactory.getInstance().getRandomId());
		Name transName = ModelFactoryImpl.eINSTANCE.createName();
		transName.setText(name);
		trans.setName(transName);
		trans.setPage(page);	
		return trans;
	}
	
	public static Arc combine(Page page, Node source, Node target) {
		Arc arc = ModelFactoryImpl.eINSTANCE.createArc();
		arc.setId(IDFactory.getInstance().getRandomId());
		arc.setSource(source);
		arc.setTarget(target);
		arc.setPage(page);
		return arc;
	}
	
	public static PetriNet getPetriNet() {
		PetriNet petriNet = ModelFactory.INSTANCE.createPetriNet();	
		return petriNet;
	}
	
	public static Page getPage(PetriNet net, String name){
		Page page = ModelFactoryImpl.eINSTANCE.createPage();
		Name pageName = ModelFactory.INSTANCE.createName();
		pageName.setText(name);
		page.setName(pageName);
		page.setId(IDFactory.getInstance().getRandomId());	
		page.setPetriNet(net);
		
		return page;
	}
}
