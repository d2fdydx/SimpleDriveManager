package gdrivejava.google;

import gdrivejava.auth.GoogleAuth;
import gdrivejava.common.FileSystem;
import gdrivejava.common.INode;

import java.io.IOException;
import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class GoogleFileSystem implements FileSystem {
	
	Drive mDrive=null;
	GoogleFileStore mStore =null;
	public GoogleFileSystem() {
		// TODO Auto-generated constructor stub
		try {
			mDrive =  GoogleAuth.getDriveService();
			
			mStore = new GoogleFileStore(this);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	

	@Override
	public List<INode> listFiles() {
		// TODO Auto-generated method stub
		return null;
	}



	public Drive getDrive() {
		return mDrive;
	}



}
