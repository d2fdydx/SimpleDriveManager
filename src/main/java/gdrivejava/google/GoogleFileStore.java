package gdrivejava.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

//import com.google.api.services.drive.model.File;

public class GoogleFileStore implements Serializable{

	public String LocationPath = System.getProperty("user.home") + "/driveTest/";
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
		String fileName ="Beginning AngularJS.pdf";
		com.google.api.services.drive.model.File f  = mFiles.get(fileName);
	///	String downloadUrl = f.getDownloadUrl();
		String surl = f.getDownloadUrl();
		File output = new File(LocationPath + fileName);
		System.out.println(LocationPath+fileName);
		if (f.getDownloadUrl() != null && f.getDownloadUrl().length() > 0) {
			try 
		      {
		        HttpResponse resp = mDrive.getRequestFactory().buildGetRequest		        		
		                     (new GenericUrl(f.getDownloadUrl())).execute();
		        InputStream in =resp.getContent();
		        FileUtils.copyInputStreamToFile(in,output );
		        
		      }
		      catch (IOException e) 
		      {
		        e.printStackTrace();
		      }
		    } else {
		      // The file doesn't have any content stored on Drive.
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
		               // 	System.out.println("download "+file.getDownloadUrl());
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
