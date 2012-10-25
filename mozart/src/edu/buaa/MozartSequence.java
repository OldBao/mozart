package edu.buaa;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
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

public class MozartSequence {
	private static String SERVER = "localhost:8089";
	private static String PREFIX = "http://buaa.edu.cn/bookSequence.owl";
	private static  String GET_BOOK_LIST_SERVICE = "http://" + SERVER + "/Services/owls/getPrice.owl";
	private static String  GET_DISCOUNT_SERVICE = "http://" + SERVER + "/Services/owls/getDiscount.owl";
	private static String GET_FINAL_PRICE_SERVICE = "http://" + SERVER + "/Services/owls/getFinalPrice.owl";
	
	OWLOntology mOnt;
	URI mBaseURI;
	OWLKnowledgeBase mKB;
	public MozartSequence(){
		try {
			mBaseURI = new URI(PREFIX);
			mKB = OWLFactory.createKB();
			mOnt = mKB.createOntology(mBaseURI);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		Service bookPriceService = mOnt.createService(URIUtils.createURI(mBaseURI, "bookPriceService"));
		Profile bookPriceProfile  = mOnt.createProfile(URIUtils.createURI(mBaseURI, "bookPriceProfile"));
		CompositeProcess finalProcess = mOnt.createCompositeProcess(URIUtils.createURI(mBaseURI, "bookPriceProcess"));
		WSDLGrounding grounding = mOnt.createWSDLGrounding(URIUtils.createURI(mBaseURI, "bookPriceGrounding"));
		
		try {
			Service service1 = mKB.readService(URI.create(GET_BOOK_LIST_SERVICE));
			Service service2 = mKB.readService(URI.create(GET_DISCOUNT_SERVICE));
			Service service3= mKB.readService(URI.create(GET_FINAL_PRICE_SERVICE));
			
			Sequence sequence = mOnt.createSequence(null);
			finalProcess.setComposedOf(sequence);
			
			Perform perform1 = mOnt.createPerform(null);
			perform1.setProcess(service1.getProcess());
			sequence.addComponent(perform1);
			Output fullPriceOutput = perform1.getProcess().getOutput();
			Input bookNameInput = perform1.getProcess().getInput();
			
			Perform perform2 = mOnt.createPerform(null);
			perform2.setProcess(service2.getProcess());
			sequence.addComponent(perform2);
			Input fullPriceInput = perform2.getProcess().getInput();
			Output discountOutput = perform2.getProcess().getOutput();
			perform2.addBinding(fullPriceInput, perform1, fullPriceOutput);
			
			Perform perform3 = mOnt.createPerform(null);
			perform3.setProcess(service3.getProcess());
			sequence.addComponent(perform3);
			Input discountInput = perform3.getProcess().getInputs().getIndividual("discount");
			Input totalPriceInput = perform3.getProcess().getInputs().getIndividual("price");
			Output finalPriceOutput = perform3.getProcess().getOutput();
		
			perform3.addBinding(discountInput, perform2, discountOutput);
			perform3.addBinding(totalPriceInput, perform1, fullPriceOutput);
			
			Input processInput = mOnt.createInput(URIUtils.createURI(mBaseURI, "getFinalPrice"));
			processInput.setLabel("fucking input", null);
			processInput.setProcess(finalProcess);
			processInput.setParamType(bookNameInput.getParamType());
			perform1.addBinding(bookNameInput, OWLS.Process.ThisPerform, processInput);
			
			Output processOutput = mOnt.createOutput(URIUtils.createURI(mBaseURI, "TEST_OUTPUT"));
			processOutput.setLabel("fucking output", null);
			processOutput.setProcess(finalProcess);
			processOutput.setParamType(finalPriceOutput.getParamType());
			
			Result result = mOnt.createResult(null);
			result.addBinding(processOutput, perform3, finalPriceOutput);		
			finalProcess.addResult(result);
			
			ValueMap<Input, OWLValue> inputMap = new ValueMap<Input, OWLValue>();
			inputMap.setValue(finalProcess.getInput(), mKB.createDataValue("C Programming Language"));
		
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			ValueMap<Output, OWLValue> output = exec.execute(finalProcess, inputMap, mKB);
			OWLValue outValue = output.getDataValue(finalProcess.getOutput());
			System.out.println(outValue);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		MozartSequence sequence = new MozartSequence();
		sequence.run();
	}
	}
