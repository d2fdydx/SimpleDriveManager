package gdrivejava.main;

import gdrivejava.common.FileSystem;
import gdrivejava.local.LocalFileSystem;

public class DriveMain {
	public static String LocationPath = System.getProperty("user.home") + "/driveTest";
	
	
	
	
	static FileSystem sGoogleFS;
	static FileSystem sLocalFS;
	static DriveMain sMain=null;
	public static FileSystem getGoogleFS() {
		return sGoogleFS;
	}
	public static void setGoogleFS(FileSystem googleFS) {
		sGoogleFS = googleFS;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//sMain = new DriveMain();
		//sGoogleFS = new GoogleFileSystem();
		
		sLocalFS=new LocalFileSystem();
		
		
	}
	
	
	

}
