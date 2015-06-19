package gdrivejava.google;

import gdrivejava.main.DriveMain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

//import com.google.api.services.drive.model.File;

public class GoogleFileStore implements Serializable{

	public static String LocationPath = "C:\\Users\\at\\driveTest";
	HashMap<String, com.google.api.services.drive.model.File> mFiles = null; // need to be changed
	GoogleProxy mProxy = new GoogleProxy();
	Drive mDrive = null;
	GoogleFileSystem mGFS = null;
	public GoogleFileStore(GoogleFileSystem fs) throws Exception {
		// TODO Auto-generated constructor stub
		mFiles = new HashMap<String, com.google.api.services.drive.model.File>();
		mGFS = fs;
		mDrive = mGFS.getDrive();
		if (mDrive ==null || mGFS==null){
			System.out.println("google file system/drive null");
			throw new Exception();
		}
		buildStore();
		downloadFile();
	}
	
	
	
	
	
	public void downloadFile(){
		String fileName ="MiniWeather.apk";
		com.google.api.services.drive.model.File f  = mFiles.get(fileName);
		String downloadUrl = f.getDownloadUrl();
		try {
			URL url = new URL(downloadUrl);
			mProxy.downFile(url, fileName);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	void buildStore (){
		  FileList result;
			try {
				result = mDrive.files().list()
				     .setMaxResults(100)
				     .execute();
				List<com.google.api.services.drive.model.File> files = result.getItems();
		        if (files == null || files.size() == 0) {
		            System.out.println("No files found.");
		        } else {
		            System.out.println("Files:");
		            for (com.google.api.services.drive.model.File file : files) {
		                System.out.printf("%s (%s)\n", file.getTitle(), file.getId());
		                mFiles.put(file.getTitle(),file);
		                for (ParentReference parent : file.getParents()){
		                	System.out.printf("---parents: %s\n",parent.getId());
		                }
		            }
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			
		
	}
	
	
	
	
	
	class GoogleProxy{
		
		
		public File downFile(URL url , String path){
			File file = new File(LocationPath + path);
			try {
				FileUtils.copyURLToFile(url, file);
				System.out.println("downloaded");
				return file;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			
		}
		
	}
}
