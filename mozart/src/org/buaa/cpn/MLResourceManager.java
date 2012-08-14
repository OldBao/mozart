package org.buaa.cpn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MLResourceManager {

	final String accesscpnDirName = "accesscpn"; 	// accesscpn use this dir as working dir, it contains some resource needby accesscpn
	final String statespaceFileDirName = "statespacefiles"; // we should copy statespacefiles to the accesscpn folder

	public String getAccessCPNPath() {
		String tempdir = System.getProperty("java.io.tmpdir");
		return new  File((tempdir), accesscpnDirName).toString();
	}
	
	public String getStatespaceFilePath() {
		return this.getAccessCPNPath()+"/"+statespaceFileDirName;
	}

	public boolean copyFolder(String oldPath, String newPath) {
		try {
			(new File(newPath)).mkdirs();
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("copy dirs error");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean prepareStateSpaceFiles () {
		String fromPath = MLResourceManager.class.getResource("/"+statespaceFileDirName).getPath();
		String toPath = this.getStatespaceFilePath();
		File tmp = new File(toPath);
		if (tmp.exists()) {
			return true;
		}
		return this.copyFolder(fromPath, toPath);
	}

}
