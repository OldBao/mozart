package edu.buaa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.exporter.DOMGenerator;
import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.grounding.WSDLGrounding;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.query.ValueMap;
import org.mindswap.utils.URIUtils;

import edu.buaa.composer.Composer;
import edu.buaa.composer.impl.Mozart;
import edu.buaa.mozart.notes.ComposeException;

public class MozartSequence {
	private static String SERVER = "localhost:8089";
	private static String PREFIX = "http://buaa.edu.cn/bookSequence.owl";
	private static String GET_BOOK_LIST_SERVICE = "http://" + SERVER
			+ "/Services/owls/getPrice.owl";
	private static String GET_DISCOUNT_SERVICE = "http://" + SERVER
			+ "/Services/owls/getDiscount.owl";
	private static String GET_FINAL_PRICE_SERVICE = "http://" + SERVER
			+ "/Services/owls/getFinalPrice.owl";

	OWLOntology mOnt;
	URI mBaseURI;
	OWLKnowledgeBase mKB;

	public MozartSequence() {
		try {
			mBaseURI = new URI(PREFIX);
			mKB = OWLFactory.createKB();
			mOnt = mKB.createOntology(mBaseURI);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			mKB = OWLFactory.createKB();
			//mOnt = mKB.createOntology(null);
			OWLIndividualList<Service> services = mKB.readAllServices(URI.create("http://owls.buaa.edu.cn:8089/Services/owls/queryBookSequence.owl"));
           Service service = null;
            for (Service s : services) {
            	if (s.getProcess() instanceof CompositeProcess) {
                    service  = s;
            		break;
            	}
            }
			org.mindswap.owls.process.Process process = service.getProcess();

			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();
			inputs.setValue(process.getInput(),
					mKB.createDataValue("C Programming Language"));
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			ValueMap<Output, OWLValue> output = 
					exec.execute(process, inputs,mKB);
			OWLValue outValue = output.getDataValue(process.getOutput());
			System.out.println(outValue);

			Composer composer = new Mozart();
			PetriNet genNet = composer.Compose(process);
			DOMGenerator.export(genNet, "/home/zhanggx/petrinets/sequence.cpn");
		} catch (ComposeException e) {
			e.printStackTrace();
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MozartSequence sequence = new MozartSequence();
		sequence.run();
	}
}
