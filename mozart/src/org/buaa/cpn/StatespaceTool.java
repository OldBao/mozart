package org.buaa.cpn;

import org.cpntools.accesscpn.engine.highlevel.HighLevelSimulator;
import org.buaa.cpn.MLResourceManager;

public class StatespaceTool {

	private static MLResourceManager rm = new MLResourceManager();
	/**
	 * @param args
	 */
	private void setStatespaceEnv() {
		if(!rm.prepareStateSpaceFiles()) {
			System.out.println("Statespace environment setup failed");
			System.exit(1);
		}
	}
	public boolean enterStatespace(HighLevelSimulator s) {
		this.setStatespaceEnv();
		try {
			String sml = "";
			for(int i = 1; i <= 8; i++) {
				String switchFile = "../statespacefiles/switch"+String.valueOf(i)+".sml";
				 sml += "use \""+switchFile+"\";";
			}
			System.out
	        .println(s
	                .evaluate(sml));
		} catch (Exception e) {
			System.out.println("enter statespace failed");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean calculateStatespace(HighLevelSimulator s) {
		this.setStatespaceEnv();
		try {
			System.out.println(s
			        .evaluate("CalculateOccGraph()"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("calcualte statespace failed");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean calculateSccGraph(HighLevelSimulator s) {
		this.setStatespaceEnv();
		try {
			System.out.println(s
			        .evaluate("CalculateSccGraph()"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("calcualte statespace failed");
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
