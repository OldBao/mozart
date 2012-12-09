package edu.buaa.utils;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.Code;
import org.cpntools.accesscpn.model.Condition;
import org.cpntools.accesscpn.model.HLAnnotation;
import org.cpntools.accesscpn.model.HLDeclaration;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.Name;
import org.cpntools.accesscpn.model.Node;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Sort;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.cpntypes.CPNType;
import org.cpntools.accesscpn.model.declaration.DeclarationFactory;
import org.cpntools.accesscpn.model.declaration.TypeDeclaration;
import org.cpntools.accesscpn.model.impl.ModelFactoryImpl;

import edu.buaa.mozart.ML.CodeSegment;

public class QuickFactory {
    private static int sPlaceIndex = 1;
    private static int sTransitionIndex = 1;
	public static Place getPlace(Page page, String name ) {
		Place place = ModelFactoryImpl.eINSTANCE.createPlace();
		place.setId(IDFactory.getInstance().getRandomId());
		Name placeName = ModelFactoryImpl.eINSTANCE.createName();
        placeName.setText("P" + sPlaceIndex++);
		//placeName.setText(name);
		place.setName(placeName);
		place.setPage(page);	
		return place;
	}
	
    public static Condition getCondition(PetriNet net, String condition){
    	Condition cond = ModelFactoryImpl.eINSTANCE.createCondition();
    	cond.setText(condition);
    	cond.setParent(net);
    	return cond;
    }
	public static Transition getTransition(Page page, String name ) {
		Transition trans = ModelFactoryImpl.eINSTANCE.createTransition();
		trans.setId(IDFactory.getInstance().getRandomId());
		Name transName = ModelFactoryImpl.eINSTANCE.createName();
        transName.setText("T" + sTransitionIndex++);
		//transName.setText(name);
		trans.setName(transName);
		trans.setPage(page);	
		return trans;
	}
	
	public static Arc combine(Page page, Node source, Node target, HLAnnotation inArcAnno) {
		Arc arc = ModelFactoryImpl.eINSTANCE.createArc();
		arc.setId(IDFactory.getInstance().getRandomId());
		arc.setSource(source);
		arc.setTarget(target);
		arc.setPage(page);
        arc.setHlinscription(inArcAnno);
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
    
    public static void addTypeDeclaration(PetriNet net, CPNType type) {
		TypeDeclaration typeDecl = DeclarationFactory.INSTANCE.createTypeDeclaration();
		typeDecl.setTypeName("INT");
		typeDecl.setSort(type);	
		HLDeclaration hlyDecl = ModelFactory.INSTANCE.createHLDeclaration();
		hlyDecl.setStructure(typeDecl);
		hlyDecl.setId(IDFactory.getInstance().getRandomId());
		hlyDecl.setParent(net);
    }
    
    public static Sort getSort(PetriNet net, String text) {
		Sort sort = ModelFactory.INSTANCE.createSort();
		sort.setText(text);
		sort.setParent(net);
        return sort;
    }
    
    public static void addCode(PetriNet net, Transition trans, CodeSegment cs){
    	Code code = ModelFactoryImpl.eINSTANCE.createCode();
    	code.setText(cs.toString());
    	code.setParent(net);
    	trans.setCode(code);
    }
}
