package edu.buaa.tests;

import java.io.FileNotFoundException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.HLAnnotation;
import org.cpntools.accesscpn.model.HLDeclaration;
import org.cpntools.accesscpn.model.HLMarking;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.Name;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.PlaceNode;
import org.cpntools.accesscpn.model.Sort;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.TransitionNode;
import org.cpntools.accesscpn.model.cpntypes.CPNInt;
import org.cpntools.accesscpn.model.cpntypes.CpntypesFactory;
import org.cpntools.accesscpn.model.declaration.DeclarationFactory;
import org.cpntools.accesscpn.model.declaration.TypeDeclaration;
import org.cpntools.accesscpn.model.declaration.VariableDeclaration;
import org.cpntools.accesscpn.model.exporter.DOMGenerator;
import org.cpntools.accesscpn.model.impl.ModelFactoryImpl;
import org.cpntools.accesscpn.model.importer.DOMParser;

import junit.framework.TestCase;

public class CPNExporter extends TestCase{
	
	protected PetriNet getFixture(){
		PetriNet petrinet = ModelFactoryImpl.eINSTANCE.createPetriNet();
	
		Name pageName = ModelFactory.INSTANCE.createName();
		pageName.setText("fucking your sister");
		Page p = ModelFactory.INSTANCE.createPage();
		p.setId("ID111");
		p.setName(pageName);
		p.setPetriNet(petrinet);
		
		CPNInt intType = CpntypesFactory.INSTANCE.createCPNInt();
		TypeDeclaration typeDecl = DeclarationFactory.INSTANCE.createTypeDeclaration();
		typeDecl.setTypeName("INT");
		typeDecl.setSort(intType);	
		HLDeclaration hlyDecl = ModelFactory.INSTANCE.createHLDeclaration();
		hlyDecl.setStructure(typeDecl);
		hlyDecl.setId("ID12314");
		hlyDecl.setParent(petrinet);
		
		VariableDeclaration varDecl = DeclarationFactory.INSTANCE.createVariableDeclaration();
		varDecl.addVariable("n");
		varDecl.setTypeName("INT");
		HLDeclaration hlDecl = ModelFactory.INSTANCE.createHLDeclaration();
		hlDecl.setStructure(varDecl);
		hlDecl.setId("ID12313");
		hlDecl.setParent(petrinet);

		Sort intSort = ModelFactory.INSTANCE.createSort();
		intSort.setText("INT");
		intSort.setParent(petrinet);
		
		HLMarking initMarking = ModelFactory.INSTANCE.createHLMarking();
		initMarking.setText("1`1");
		Name placeName = ModelFactory.INSTANCE.createName();
		placeName.setText("fucking place 1");
		Place place = ModelFactory.INSTANCE.createPlace();
		place.setId("ID112");
		place.setName(placeName);
		PlaceNode placeNode = place;
		place.setInitialMarking(initMarking);
		place.setSort(intSort);
		placeNode.setPage(p);

		Name placeName2 = ModelFactory.INSTANCE.createName();
		placeName2.setText("fucking place 2");
		Place place2 = ModelFactory.INSTANCE.createPlace();
		place2.setId("ID113");
		place2.setName(placeName2);
		PlaceNode placeNode2 = place2;
		place2.setSort(intSort);
		placeNode2.setPage(p);
	
		Name transName = ModelFactory.INSTANCE.createName();
		transName.setText("fucking trans1");
		Transition trans = ModelFactory.INSTANCE.createTransition();
		trans.setName(transName);
		trans.setId("ID114");
		TransitionNode transNode = trans;
		transNode.setPage(p);
		
		Arc arc1 = ModelFactory.INSTANCE.createArc();
		arc1.setId("ID115");
		arc1.setSource(placeNode);
		arc1.setTarget(transNode);
		HLAnnotation arcAnno1 = ModelFactory.INSTANCE.createHLAnnotation();
		arcAnno1.setText("n");
		arcAnno1.setParent(petrinet);
		arc1.setHlinscription(arcAnno1);
		arc1.setPage(p);
		
		Arc arc2 = ModelFactory.INSTANCE.createArc();
		arc2.setId("ID116");
		arc2.setSource(transNode);
		arc2.setTarget(placeNode2);
		HLAnnotation arcAnno2 = ModelFactory.INSTANCE.createHLAnnotation();
		arcAnno2.setText("n+1");
		arcAnno2.setParent(petrinet);
		arc2.setHlinscription(arcAnno2);
		arc2.setPage(p);
		
		return petrinet;
	}
	
	public  void testSave(){
		PetriNet petrinet = getFixture();
		try {
			DOMGenerator.export(petrinet, "/home/zhanggx/testExport.cpn");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OperationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
