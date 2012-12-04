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

public class MozartITE {
	public void run() {
		try {
			OWLKnowledgeBase mKB = OWLFactory.createKB();
			OWLIndividualList<Service> services = mKB
					.readAllServices(URI
							.create("http://owls.buaa.edu.cn:8089/Services/owls/buyBookIfThenElse.owl"));
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
					mKB.createDataValue("C Programming Language"));
            
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			ValueMap<Output, OWLValue> output;

			output = exec.execute(process, inputs, mKB);
			OWLValue outValue = output.getDataValue(process.getOutput());
			System.out.println(outValue);
            
            Composer composer = new Mozart();
            PetriNet net = composer.Compose(process);
            DOMGenerator.export(net, "/home/zhanggx/petrinets/ite.cpn");
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ComposeException e) {
			e.printStackTrace();
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
    
    public static void main(String[] args) {
        new MozartITE().
    	run();
    }
}