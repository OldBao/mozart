package edu.buaa.mozart.stub.tests;

import java.io.IOException;
import java.net.URI;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import edu.buaa.mozart.stub.EncodeDecode;
import edu.buaa.mozart.stub.JavaCPN;

public class JavCPNWebServiceExample {

 
    public static void main(String[] args) throws Exception {
        int port = 9000;
        JavaCPN conn = new JavaCPN();
        // listen on port and accept connection
        conn.accept(port);
        while (true) {
        	String username  = EncodeDecode.decodeString(conn.receive());
            String password = EncodeDecode.decodeString(conn.receive());
            System.out.println("receiced from CPN: username:"+username + " password:" + password);
            boolean result  = valid(username,password);
            conn.send(EncodeDecode.encode(Boolean.toString(result)));
	}
    }

    private static boolean valid(String username, String password){
		try {
			OWLKnowledgeBase mKB = OWLFactory.createKB();
			mKB.createOntology(null);
			Service service = mKB.readService(URI.create("http://owls.buaa.edu.cn:8089/Services/owls/login.owl"));
			
			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();
			
			inputs.setValue(service.getProcess().getInput("username"), mKB.createDataValue(username));
			inputs.setValue(service.getProcess().getInput("password"), mKB.createDataValue(password));
            
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			ValueMap<Output, OWLValue> outputs;
			outputs = exec.execute(service.getProcess(), inputs, mKB);
		                
			OWLValue output = outputs.getDataValue(service.getProcess().getOutput().getName());
			System.out.println(output);
            return Boolean.parseBoolean(output.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        return false;
    }
    
    
}
