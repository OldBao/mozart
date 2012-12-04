package edu.buaa.mozart.stub;

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

import edu.buaa.composer.ComposerConfig;

public class ServiceStub {
	public static void main(String[] args) {
		int port = ComposerConfig.WS_STUB_PORT;
		JavaCPN conn = new JavaCPN();
		// listen on port and accept connection
		try {
			conn.accept(port);

			while (true) {
				String wsName = EncodeDecode.decodeString(conn.receive());
				Integer paramCnt = Integer.parseInt(EncodeDecode
						.decodeString(conn.receive()));
				String params[] = new String[paramCnt];
				for (int i = 0; i < paramCnt; i++) {
					params[i] = EncodeDecode.decodeString(conn.receive());
				}
				String result = internal_call(wsName, params);
				conn.send(EncodeDecode.encode(result));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String internal_call(String ws, String[] values) {
		System.out.println("Call web Service : " + ws);
		try {
			OWLKnowledgeBase mKB = OWLFactory.createKB();
			mKB.createOntology(null);
			Service service = mKB.readService(URI.create(ws));

			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();

			for (int i = 0; i < values.length; i++) {
				Input input = service.getProcess().getInputs().get(i);
				inputs.setValue(input, mKB.createDataValue(values[i]));
				System.out.println("\t" + input.getLocalName() + "  :  "
						+ values[i]);
			}

			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			ValueMap<Output, OWLValue> outputs;
			outputs = exec.execute(service.getProcess(), inputs, mKB);

			OWLValue output = outputs.getDataValue(service.getProcess()
					.getOutput().getName());
			System.out.println("Result : " + output);
			System.out.println();

			return output.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return "";
	}
}
