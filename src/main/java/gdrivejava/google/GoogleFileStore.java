package gdrivejava.google;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
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
		//downloadFile();
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

		try {
			//mProxy.getAllRemoteFiles();
			mProxy.getAllRemoteDirs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	



	}







	class GoogleProxy{


		public boolean downFile(String fileName, String Path) throws IOException{
			com.google.api.services.drive.model.File f  = mFiles.get(fileName);
			///	String downloadUrl = f.getDownloadUrl();
			String surl = f.getDownloadUrl();
			File output = new File(LocationPath + fileName);
			System.out.println(LocationPath+fileName);
			if (f.getDownloadUrl() != null && f.getDownloadUrl().length() > 0) {


				HttpResponse resp = mDrive.getRequestFactory().buildGetRequest		        		
						(new GenericUrl(f.getDownloadUrl())).execute();
				InputStream in =resp.getContent();
				FileUtils.copyInputStreamToFile(in,output );

				return true;



			} else {
				// The file doesn't have any content stored on Drive.
				return false;
			}

		}


		public List<com.google.api.services.drive.model.File>  getAllRemoteFiles() throws IOException{

			List<com.google.api.services.drive.model.File> result = new ArrayList<com.google.api.services.drive.model.File>();
			//System.out.println(mDrive.files().list().getQ());
			Files.List request = mDrive.files().list().setQ("trashed=false and mimeType != 'application/vnd.google-apps.folder'");

			do {

				FileList files = request.execute();

				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());



			} while (request.getPageToken() != null &&
					request.getPageToken().length() > 0);

			for (com.google.api.services.drive.model.File f : result){
				System.out.println(f.getTitle());
			}

			return result;



		}


		public List<com.google.api.services.drive.model.File>  getAllRemoteDirs() throws IOException{
			List<com.google.api.services.drive.model.File> result = new ArrayList<com.google.api.services.drive.model.File>();
			//System.out.println(mDrive.files().list().getQ());
			Files.List request = mDrive.files().list().setQ("trashed=false and mimeType = 'application/vnd.google-apps.folder'");

			do {

				FileList files = request.execute();

				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());



			} while (request.getPageToken() != null &&
					request.getPageToken().length() > 0);

			for (com.google.api.services.drive.model.File f : result){
				System.out.println(f.getTitle());
			}

			return result;


		}
	}
}
