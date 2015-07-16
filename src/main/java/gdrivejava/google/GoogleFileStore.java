package gdrivejava.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.api.client.http.FileContent;
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


	HashMap<String, com.google.api.services.drive.model.File> fileMap = null; // need to be changed
	HashMap<String, INode> nodesMap = null; // id, Inode
	HashMap<String, INode> pathMap =null;
	final static String FOLDER_MIME="application/vnd.google-apps.folder";
	final static String FILE_MIME="application/vnd.google-apps.file";
	INode rootNode =null;
	
	

	transient GoogleProxy mProxy = new GoogleProxy();
	transient Drive mDrive = null;
	transient GoogleFileSystem mGFS = null;





	public GoogleFileStore(GoogleFileSystem fs) throws Exception {
		// TODO Auto-generated constructor stub
		fileMap = new HashMap<String, com.google.api.services.drive.model.File>();
		nodesMap = new HashMap<String, INode>();
		pathMap= new HashMap<String, INode>();
		mGFS = fs;
		mDrive = mGFS.getDrive();
		if (mDrive ==null || mGFS==null){

			throw new Exception("google file system/drive null");
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



	public void uploadFile(String Path){
		//
		File file =new File(this.mGFS.getRootPath()+Path);
		if (!file.exists()){
			System.out.println(this.mGFS.getRootPath()+Path + " not exists");
		}else{
			String parentPath = "/"+FilenameUtils.getPathNoEndSeparator(Path);
			//check if parent folder created
			if (!pathMap.containsKey(parentPath)){
				System.out.println("remote: not contains folder /" + FilenameUtils.getPathNoEndSeparator(Path));
				uploadFile("/"+FilenameUtils.getPathNoEndSeparator(Path));
			}

			//insert
			if (pathMap.containsKey(parentPath)){
				System.out.println("remote: contains " + parentPath);
				INode temp = new INode();
				INode parNode =pathMap.get(parentPath);
				com.google.api.services.drive.model.File created=null;
				if (file.isDirectory()){
					created =insertFile(mDrive, parNode.getId(), FOLDER_MIME,file);
					temp.setDir(true);
				}else{
					created =insertFile(mDrive, parNode.getId(),null,file);
				}
				if (created !=null){
					System.out.println(created.getTitle());
					temp.setId(created.getId());
					temp.setParent(parNode);
					parNode.addChild(temp);
					parNode.setRemoteFile(created);
				}else{
					System.out.println(" upload === null"+ file.getName());
				}

			}else{
				System.out.println("parent Folder cannnot be created");

			}

			return ;

		}

	}

	// update file -> 
	public void updateFile(String path){ 
		//
		File file =new File(this.mGFS.getRootPath()+path);
		if (!file.exists()){
			System.out.println(this.mGFS.getRootPath()+path + " not exists");
		}else{
			if (!file.isDirectory()){
				INode remoteNode=  pathMap.get(path);
				FileContent mediaContent = new FileContent(null, file);
				try {
					com.google.api.services.drive.model.File updatedFile = mDrive.files().update(remoteNode.getId(), remoteNode.getRemoteFile(),mediaContent).execute();
					remoteNode.setRemoteFile(updatedFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

		}

	}

	
	
	
	private  com.google.api.services.drive.model.File insertFile(Drive service, 
			String parentId, String mimeType, File fileContent) {
		// File's metadata.
		com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
		body.setTitle(fileContent.getName());
		// body.setDescription(description);
		if (mimeType!=null){
			body.setMimeType(mimeType);
		}

		// Set the parent folder.
		if (parentId != null && parentId.length() > 0) {
			body.setParents(
					Arrays.asList(new ParentReference().setId(parentId)));
		}

		// File's content.


		try {
			com.google.api.services.drive.model.File file= null;
			if (!fileContent.isDirectory()){
				FileContent mediaContent = new FileContent(mimeType,fileContent);
				file = service.files().insert(body, mediaContent).execute();
			}else{
				file = service.files().insert(body).execute();
			}

			// Uncomment the following line to print the File ID.
			// System.out.println("File ID: " + file.getId());

			return file;
		} catch (IOException e) {
			System.out.println("An error occured: " + e);
			return null;
		}
	}

	



	public void downloadFile(String path){
		INode node =pathMap.get(path);
		com.google.api.services.drive.model.File f  = node.getRemoteFile();
		///	String downloadUrl = f.getDownloadUrl();
		String surl = f.getDownloadUrl();
		File output = new File(DriveMain.LocationPath + path);
		
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
			com.google.api.services.drive.model.File f  = pathMap.get(fileName).getRemoteFile();
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
				if (f.getDownloadUrl() != null && f.getDownloadUrl().length() > 0){
					fileMap.put(f.getId(),f);
				}

			}

			for (com.google.api.services.drive.model.File f : fileMap.values()){
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
					parNode.setRemoteFile(fileMap.get(parent.getId()));
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
				fileMap.put(f.getId(),f);

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
					parNode.setRemoteFile(fileMap.get(parent.getId()));
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
