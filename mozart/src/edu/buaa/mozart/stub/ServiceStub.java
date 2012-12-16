package edu.buaa.mozart.stub;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import edu.buaa.composer.ComposerConfig;

public class ServiceStub implements Runnable {
    Logger logger = Logger.getLogger(ServiceStub.class);
	private boolean isRun;
	private ServerSocket mServerSocket;

    public ServiceStub() throws IOException{
		int port = ComposerConfig.WS_STUB_PORT;
		mServerSocket = new ServerSocket(port);
        logger.info("服务实例启动，绑定至 "  + port + "端口");
    }
	public void start(){
		isRun = true;
		while (isRun) {
			Socket socket;
			try {
				socket = mServerSocket.accept();
				new Thread(new JavaCPN(socket)).run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		if (!isRun || mServerSocket == null)
			return;
		try {
			mServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			isRun = false;
		}
	}

	@Override
	public void run() {
		start();
	}

	public boolean isRun() {
		return isRun;
	}
}
