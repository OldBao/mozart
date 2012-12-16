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
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import edu.buaa.composer.Composer;
import edu.buaa.composer.impl.Mozart;
import edu.buaa.mozart.notes.ComposeException;

public class MozartSplitJoin{
	public static void main(String[] args){
		try {
			OWLOntology mOnt;
			URI mBaseURI;
			OWLKnowledgeBase mKB;
			mKB = OWLFactory.createKB();
			mOnt = mKB.createOntology(null);
			OWLIndividualList<Service> services = mKB
					.readAllServices(URI
							.create("file:///home/zhanggx/owls/split+join.owl"));
			Service service = null;
			for (Service s : services) {
				if (s.getProcess() instanceof CompositeProcess) {
					service = s;
					break;
				}
			}
			org.mindswap.owls.process.Process process = service.getProcess();

			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();
			inputs.setValue(process.getInput(),
					mKB.createDataValue("STL Source"));
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			ValueMap<Output, OWLValue> output = exec.execute(process, inputs,
					mKB);
			OWLValue outValue = output.getDataValue(process.getOutput());
			System.out.println(outValue);
            
            Composer composer = new Mozart();
            try {
				PetriNet net  = composer.Compose(process);
                DOMGenerator.export(net, "/home/zhanggx/petrinets/full/test/splitjoin.owl");
			} catch (ComposeException e) {
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
            
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
