package gdrivejava.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.beanutils.BeanUtils;

import gdrivejava.google.GoogleFileSystem;

public class ObjectStoreUtil {

	public static String EXTENSION="drivemandb";
	private static String OS = System.getProperty("os.name").toLowerCase();
	static public void saveIndex(String path, Serializable obj) throws IOException{
		path +="."+EXTENSION;
		
		
		
		
		
		FileOutputStream fos = new FileOutputStream(path);
	     ObjectOutputStream oos = new ObjectOutputStream(fos);
	     oos.writeObject(obj);

	     oos.close();
	     if (OS.indexOf("win")>=0){
	    	 Runtime.getRuntime().exec("attrib +H "+path);
	     }
	      
	}
	
	static public Serializable readIndex(String path) throws ClassNotFoundException, IOException{
		path +="."+EXTENSION;
		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Serializable s = (Serializable) ois.readObject();
		ois.close();
		return s;
	}
	
	
	
	
}
