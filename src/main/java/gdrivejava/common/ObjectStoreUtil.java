package gdrivejava.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	static public boolean saveIndex(String path, Serializable obj) {
		path +="."+EXTENSION;
		boolean res = true;
		
		File file = new File (path);
		if (file.exists() && file.isFile()){
			if (file.delete()){
				System.out.println("deleted ori index");
			}
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
		     oos.writeObject(obj);

		     oos.close();
		     if (OS.indexOf("win")>=0){
		    	 Runtime.getRuntime().exec("attrib +H "+path);
		     }
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res =false;
		}finally{
			try {
				if (fos!=null)
						fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	     return res;
	      
	}
	
	static public Serializable readIndex(String path) {
		path +="."+EXTENSION;
		FileInputStream fis = null;
		Serializable s=null;
		try {
			fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			 s = (Serializable) ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if (fis!=null)
					fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
		return s;
	}
	
	
	
	
}
