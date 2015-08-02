package gdrivejava.main;

import gdrivejava.common.FileSystem;
import gdrivejava.common.Synchronizer;
import gdrivejava.google.GoogleFileSystem;
import gdrivejava.local.LocalFileSystem;

public class DriveMain {
	public static String LocationPath = System.getProperty("user.home") + "/driveTest2";
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Synchronizer sync = new Synchronizer();
		sync.start();
		
	}
	
	
	

}
