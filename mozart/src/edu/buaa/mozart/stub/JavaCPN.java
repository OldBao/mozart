package edu.buaa.mozart.stub;

import java.net.*;
import java.io.*;

import org.apache.log4j.Logger;
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

public class JavaCPN implements JavaCPNInterface, Runnable {
    private static Logger logger = Logger.getLogger(JavaCPN.class);
	private Socket mSocket;
	private InputStream input;
	private OutputStream output;
	OWLKnowledgeBase mKB;
    private int index; 
    private static int s_index = 0;

	public JavaCPN(Socket socket) throws IOException {
        index = s_index++;
        logger.info("新的CPN模拟器#"+ index + "连入");
		mSocket = socket;
		input = socket.getInputStream();
		output = socket.getOutputStream();
	}

	public void send(ByteArrayInputStream sendBytes) throws SocketException {
		// A byte array representing a data packet
		byte[] packet;

		// While there are more than 127 bytes still to send ...
		while (sendBytes.available() > 127) {
			// ... create a 128 byte packet, ...
			packet = new byte[128];

			// ... set the header to 255, ...
			packet[0] = (byte) 255;

			// ... read 127 bytes from the sequence of bytes to send, ...
			sendBytes.read(packet, 1, 127);

			// ... and send the packet to the external process.
			try {
				output.write(packet);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Create a packet for any remaining data
		packet = new byte[sendBytes.available() + 1];

		// Set the header appropriately
		packet[0] = (byte) (sendBytes.available());

		// Read the remaining bytes into the packet
		sendBytes.read(packet, 1, sendBytes.available());

		// Send the packet to the external process
		try {
			output.write(packet);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ByteArrayOutputStream receive() throws SocketException {
		// The complete sequence of bytes received from the external process
		ByteArrayOutputStream receivedBytes = new ByteArrayOutputStream();

		// The header received from the external process
		int header = -1;

		// The number of payload bytes received from the external process for a
		// packet
		int numberRead = 0;

		// The total number of payload bytes received from the external process
		// for
		// a packet, if not all are received immediately.
		int totalNumberRead = 0;

		// The payload received from the external process for each packet
		byte[] payload;

		while (true) {
			// Read a header byte from the input stream
			try {
				header = (int) input.read();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

			// If the header shows that the socket has closed ...
			if (header == -1){
                logger.info("#" + index + "已经关闭");
                throw new SocketException("Closed");
			}

			// If the header indicates another packet to follow ...
			else if (header >= 127) {
				// ... create 127 bytes of payload storage ...
				payload = new byte[127];
			}

			// ... else create storage of the appropriate size
			else
				payload = new byte[header];

			// Read the payload bytes from the input stream

			// Reset the total bytes received to 0 for this iteration
			totalNumberRead = 0;

			// Loop until all data has been read for this packet.
			while (totalNumberRead < payload.length && numberRead != -1) {
				try {
					// Try to read all bytes in this packet
					numberRead = input.read(payload, totalNumberRead,
							payload.length - totalNumberRead);

					// If some bytes were read ...
					if (numberRead != -1)

						// ... record this many bytes as having been read
						totalNumberRead = totalNumberRead + numberRead;

				} catch (SocketException e) {
					throw new SocketException(
							"Socket closed while receiving data.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// If not all bytes could be read ...
			if ((totalNumberRead < header || numberRead == -1) && header != 255)

				// ... throw a SocketException
				throw new SocketException("Error receiving data.");

			// Write the payload data to the complete sequence of received bytes
			receivedBytes.write(payload, 0, payload.length);

			// If no more bytes to follow, break from the loop.
			if (header <= 127)
				break;
		}

		// Return the received bytes
		return receivedBytes;
	}

	@Override
	public void run() {
		while(true){
		try {
			String wsName = EncodeDecode.decodeString(receive());
			Integer paramCnt = Integer.parseInt(EncodeDecode
					.decodeString(receive()));
			String params[] = new String[paramCnt];
			for (int i = 0; i < paramCnt; i++) {
				params[i] = EncodeDecode.decodeString(receive());
			}
			String result = internal_call(wsName, params);
			send(EncodeDecode.encode(result));
		} catch (NumberFormatException e) {
            logger.info("number format error");
		} catch (SocketException e) {
            logger.info("socket 错误" + e.getMessage());
		}
		}
	}

	private String internal_call(String ws, String[] values) {
        logger.info("#"+index + "请求调用web service: " + ws);
		OWLValue output;
		try {
			mKB = OWLFactory.createKB();
			mKB.createOntology(null);
			Service service = mKB.readService(URI.create(ws));

			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();

			for (int i = 0; i < values.length; i++) {
				Input input = service.getProcess().getInputs().get(i);
				inputs.setValue(input, mKB.createDataValue(values[i]));
				logger.info("\t" + input.getLocalName() + "  :  "
						+ values[i]);
			}

			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			ValueMap<Output, OWLValue> outputs;
			outputs = exec.execute(service.getProcess(), inputs, mKB);

			output = outputs.getDataValue(service.getProcess().getOutput()
					.getName());
			logger.info("Result : " + output);
			return output.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return "";
	}
}
