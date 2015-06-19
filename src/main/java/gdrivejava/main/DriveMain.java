package gdrivejava.main;

import gdrivejava.common.FileSystem;
import gdrivejava.google.GoogleFileStore;
import gdrivejava.google.GoogleFileSystem;

public class DriveMain {

	static FileSystem sGoogleFS;
	public static FileSystem getGoogleFS() {
		return sGoogleFS;
	}
	public static void setGoogleFS(FileSystem googleFS) {
		sGoogleFS = googleFS;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		sGoogleFS = new GoogleFileSystem();
		
		
		
	}

}
