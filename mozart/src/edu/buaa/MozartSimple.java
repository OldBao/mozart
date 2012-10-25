package edu.buaa;

import java.io.IOException;
import java.net.URI;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.exporter.DOMGenerator;
import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import edu.buaa.composer.Composer;
import edu.buaa.composer.impl.Mozart;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.transformer.impl.MozartBuilderImpl;

public class MozartSimple {
	private OWLKnowledgeBase mKB;
	private OWLOntology mOnt;
	public void run(){
		try {
			mKB = OWLFactory.createKB();
			mOnt = mKB.createOntology(null);
			Service service = mKB.readService(URI.create("http://localhost:8089/Services/owls/getPrice.owl"));
			
			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();
			
			inputs.setValue(service.getProcess().getInput(), mKB.createDataValue("C Programming Language"));
			
		//	ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			//ValueMap<Output, OWLValue> outputs = exec.execute(service.getProcess(), inputs, mKB);
			
			//OWLValue output = outputs.getDataValue(service.getProcess().getOutput().getName());
			
			//System.out.println(output);
			
			//MozartBuilderImpl impl = new MozartBuilderImpl();
			//PetriNet genNet = impl.transform(service);
			Composer composer = new Mozart();
			PetriNet genNet = composer.Compose(service.getProcess());
			DOMGenerator.export(genNet, "/home/zhanggx/petrinets/atomic.cpn");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (ComposeException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args){
		new MozartSimple().run();
	}
}
