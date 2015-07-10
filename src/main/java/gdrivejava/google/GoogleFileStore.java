package gdrivejava.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import gdrivejava.common.INode;
import gdrivejava.main.DriveMain;

//import com.google.api.services.drive.model.File;

public class GoogleFileStore implements Serializable{

	
	HashMap<String, com.google.api.services.drive.model.File> mFiles = null; // need to be changed
	HashMap<String, INode> nodesMap = null; // id, Inode
	HashMap<String, INode> pathMap =null;
	INode rootNode =null;
	
	
	
	transient GoogleProxy mProxy = new GoogleProxy();
	transient Drive mDrive = null;
	transient GoogleFileSystem mGFS = null;
	
	
	
	
	
	public GoogleFileStore(GoogleFileSystem fs) throws Exception {
		// TODO Auto-generated constructor stub
		mFiles = new HashMap<String, com.google.api.services.drive.model.File>();
		nodesMap = new HashMap<String, INode>();
		pathMap= new HashMap<String, INode>();
		mGFS = fs;
		mDrive = mGFS.getDrive();
		if (mDrive ==null || mGFS==null){
			System.out.println("google file system/drive null");
			throw new Exception();
		}
		buildStore();
		//downloadFile();
	}





	public HashMap<String, INode> getPathMap() {
		return pathMap;
	}





	public void setPathMap(HashMap<String, INode> pathMap) {
		this.pathMap = pathMap;
	}





	public void downloadFile(String path){
		INode node =pathMap.get(path);
		com.google.api.services.drive.model.File f  = node.getRemoteFile();
		///	String downloadUrl = f.getDownloadUrl();
		String surl = f.getDownloadUrl();
		File output = new File(DriveMain.LocationPath + path);
		System.out.println(DriveMain.LocationPath+path);
		if (f.getDownloadUrl() != null && f.getDownloadUrl().length() > 0) {
			try 
			{
				HttpResponse resp = mDrive.getRequestFactory().buildGetRequest		        		
						(new GenericUrl(f.getDownloadUrl())).execute();
				InputStream in =resp.getContent();
				FileUtils.copyInputStreamToFile(in,output );
				FileInputStream fis = new FileInputStream(output);
				String md5 =DigestUtils.md5Hex(fis);
				fis.close();
			
				System.out.println(md5);
				System.out.println(node.getRemoteFile().getMd5Checksum());
				System.out.println(path + "  downloaded");
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
		
			mProxy.getAllRemoteDirs();
			mProxy.getAllRemoteFiles();
			for(String id : nodesMap.keySet()){
				INode temp = nodesMap.get(id);
				pathMap.put(temp.getFullPathName(),temp);
			}
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
			File output = new File(DriveMain.LocationPath + fileName);
			System.out.println(DriveMain.LocationPath+fileName);
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
				mFiles.put(f.getId(),f);
			
			}

			for (com.google.api.services.drive.model.File f : result){
				List<ParentReference> tpr = f.getParents();
				if(tpr==null||tpr.size()==0){
					System.out.println(f.getId());
					System.out.println(rootNode.getId());
					continue;
				}
				ParentReference parent = f.getParents().get(0);
				INode node ;
				if (nodesMap.containsKey(f.getId())){
					node = nodesMap.get(f.getId());
					node.setRemoteFile(f);
				}else{
					node = new INode(f);
					nodesMap.put(f.getId(), node);
				}
				node.setId(f.getId());
				
				INode parNode=null;
				if (nodesMap.containsKey(parent.getId())){
					parNode = nodesMap.get(parent.getId());
					
				}else {
					parNode = new INode();
					parNode.setRemoteFile(mFiles.get(parent.getId()));
					nodesMap.put(parent.getId(),parNode);	
				}
			
				node.setParent(parNode);
				parNode.addChild(node);
				parNode.setId(parent.getId());
				
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
				//System.out.println(f.getTitle());
				mFiles.put(f.getId(),f);
				
			}

			for (com.google.api.services.drive.model.File f : result){
				ParentReference parent = f.getParents().get(0);
				INode node ;
				if (nodesMap.containsKey(f.getId())){
					node = nodesMap.get(f.getId());
				}else{
					node = new INode(f);
					node.setDir(true);
					nodesMap.put(f.getId(), node);
				}
				node.setId(f.getId());
				INode parNode;
				if (nodesMap.containsKey(parent.getId())){
					parNode = nodesMap.get(parent.getId());
					
				}else {
					parNode = new INode();
					parNode.setDir(true);
					parNode.setRemoteFile(mFiles.get(parent.getId()));
					nodesMap.put(parent.getId(),parNode);	
				}
				if (parent.getIsRoot()){
					//System.out.println("find root");
					//System.out.println(parent.getId());
					parNode.setRoot(true);
					rootNode=parNode;
				}
				node.setParent(parNode);
				parNode.addChild(node);
				parNode.setId(parent.getId());
			}
			
			
			
			return result;


		}
	}
}
