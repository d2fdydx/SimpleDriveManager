package gdrivejava.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import gdrivejava.common.DirtyHashMap;
import gdrivejava.common.INode;
import gdrivejava.event.listener.DirtyListener;
import gdrivejava.main.DriveMain;

//import com.google.api.services.drive.model.File;

public class GoogleFileStore implements Serializable{


	transient HashMap<String, com.google.api.services.drive.model.File> fileMap = null; // need to be changed: id v.s. File
	DirtyHashMap<com.google.api.services.drive.model.File> nodesMap = null; // id, Inode
	DirtyHashMap<com.google.api.services.drive.model.File> pathMap =null;
	final static String FOLDER_MIME="application/vnd.google-apps.folder";
	final static String FILE_MIME="application/vnd.google-apps.file";
	long lastModifiedTime=0;
	INode<com.google.api.services.drive.model.File> rootNode =null;
	

	
	


	transient Drive drive = null;


	transient GoogleFileSystem googleFs = null;


	public Drive getDrive() {
		return drive;
	}



	public void setDrive(Drive drive) {
		this.drive = drive;
	}



	public GoogleFileSystem getGoogleFs() {
		return googleFs;
	}



	public void setGoogleFs(GoogleFileSystem googleFs) {
		this.googleFs = googleFs;
	}



	public GoogleFileStore() {
		// TODO Auto-generated constructor stub
		System.out.println("deserializing");
		
	}



	public GoogleFileStore(GoogleFileSystem fs)  {
		// TODO Auto-generated constructor stub
		
		fileMap = new HashMap<String, com.google.api.services.drive.model.File>();
		
		nodesMap = new DirtyHashMap<>();
		
		pathMap=  new DirtyHashMap<>();
		pathMap= (DirtyHashMap<com.google.api.services.drive.model.File>) Collections.synchronizedMap(nodesMap);
		pathMap.setListener(new DirtyListener() {

			@Override
			public void notifyDirty(INode<?> node) {
				// TODO Auto-generated method stub
				
			}
			
	
		});
		
		googleFs = fs;
		drive = googleFs.getDrive();

		try {
			buildStore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}





	public Map<String, INode<com.google.api.services.drive.model.File>> getPathMap() {
		return pathMap;
	}






	public void uploadFile(String path){
		//
		File file =new File(this.googleFs.getRootPath()+path);
		if (!file.exists()){
			System.out.println(this.googleFs.getRootPath()+path + " not exists");
		}else{
			String parentPath = "/"+FilenameUtils.getPathNoEndSeparator(path);
			//check if parent folder created
			if (!pathMap.containsKey(parentPath)){

				uploadFile("/"+FilenameUtils.getPathNoEndSeparator(path));
			}

			//start insert
			if (pathMap.containsKey(parentPath)){
				System.out.println("remote upload: "+ path);
				INode<com.google.api.services.drive.model.File> temp = new GoogleINode();
				INode<com.google.api.services.drive.model.File> parNode =pathMap.get(parentPath);
				com.google.api.services.drive.model.File created=null;
				if (file.isDirectory()){
					//create folder
					created =insertFile(drive, parNode.getId(), FOLDER_MIME,file);
					temp.setDir(true);
				}else{
					//upload file
					created =insertFile(drive, parNode.getId(),null,file);
				}
				if (created !=null){
					System.out.println(created.getTitle());
					temp.setId(created.getId());
					temp.setParent(parNode);
					temp.setFile(created);
					pathMap.put(temp.getFullPathName(),temp);
					parNode.addChild(temp);
					
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
	public void updateFile(String path) throws Exception{ 
		//
		File file =new File(this.googleFs.getRootPath()+path);
		if (!file.exists()){
			System.out.println(this.googleFs.getRootPath()+path + " not exists");
		}else{
			if (!file.isDirectory()){
				System.out.println("remote update: " + path);
				INode<com.google.api.services.drive.model.File> remoteNode=  pathMap.get(path);
				FileContent mediaContent = new FileContent(null, file);
				try {
					com.google.api.services.drive.model.File updatedFile = drive.files().update(remoteNode.getId(), remoteNode.getFile(),mediaContent).execute();
					remoteNode.setFile(updatedFile);
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





	public void downloadFile(String path) throws Exception{
		INode<com.google.api.services.drive.model.File> node =pathMap.get(path);
		com.google.api.services.drive.model.File f  = node.getFile();
		///	String downloadUrl = f.getDownloadUrl();
		String surl = f.getDownloadUrl();
		File output = new File(DriveMain.LocationPath + path);

		if (f.getDownloadUrl() != null && f.getDownloadUrl().length() > 0) {
			try 
			{
				HttpResponse resp = drive.getRequestFactory().buildGetRequest		        		
						(new GenericUrl(f.getDownloadUrl())).execute();
				InputStream in =resp.getContent();
				FileUtils.copyInputStreamToFile(in,output );
				FileInputStream fis = new FileInputStream(output);
				String md5 =DigestUtils.md5Hex(fis);
				fis.close();

				System.out.println(md5);
				System.out.println(node.getCheckSum());
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



	void buildStore () throws Exception{

		try {

			getAllRemoteDirs();
			getAllRemoteFiles();
			for(String id : nodesMap.keySet()){
				INode temp = nodesMap.get(id);
				if (temp.getLastModifiedTime() > this.lastModifiedTime){
					this.lastModifiedTime = temp.getLastModifiedTime();
				}
				pathMap.put(temp.getFullPathName(),temp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	



	}



	public List<com.google.api.services.drive.model.File>  getAllRemoteFiles() throws IOException{

		List<com.google.api.services.drive.model.File> result = new ArrayList<com.google.api.services.drive.model.File>();
		//System.out.println(mDrive.files().list().getQ());
		Files.List request = drive.files().list().setQ("trashed=false and mimeType != 'application/vnd.google-apps.folder'");

		do {

			com.google.api.services.drive.model.File a =null;

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

				continue;
			}
			ParentReference parent = f.getParents().get(0);
			INode<com.google.api.services.drive.model.File> node ;
			if (nodesMap.containsKey(f.getId())){
				//update node
				node = nodesMap.get(f.getId());
				node.setFile(f);
			}else{
				//create new node
				node = new GoogleINode();
				node.setFile(f);
				nodesMap.put(f.getId(), node);
			}
			node.setId(f.getId());

			INode<com.google.api.services.drive.model.File> parNode=null;
			if (nodesMap.containsKey(parent.getId())){
				parNode = nodesMap.get(parent.getId());

			}else {
				parNode = new GoogleINode();
				if (fileMap.get(parent.getId())!=null){
					parNode.setFile(fileMap.get(parent.getId()));
				}
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
		Files.List request = drive.files().list().setQ("trashed=false and mimeType = 'application/vnd.google-apps.folder'");

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
			INode<com.google.api.services.drive.model.File> node ;
			if (nodesMap.containsKey(f.getId())){
				node = nodesMap.get(f.getId());
			}else{
				node = new GoogleINode();
				node.setFile(f);
				node.setDir(true);
				nodesMap.put(f.getId(), node);
			}
			node.setId(f.getId());
			INode<com.google.api.services.drive.model.File> parNode;
			if (nodesMap.containsKey(parent.getId())){
				parNode = nodesMap.get(parent.getId());

			}else {
				parNode = new GoogleINode();
				parNode.setDir(true);
				if (fileMap.get(parent.getId())!=null){
					parNode.setFile(fileMap.get(parent.getId()));
				}
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

	public List<String> refreshIndex() throws IOException{
		DateTime date = new DateTime (lastModifiedTime);
		
		List<String> pathList = new ArrayList<>();
		List<com.google.api.services.drive.model.File> result = new ArrayList<com.google.api.services.drive.model.File>();
		//System.out.println(mDrive.files().list().getQ());
		if (drive ==null){
			System.out.println("wt");
		}
		System.out.println(date.toStringRfc3339());
		Files.List request = drive.files().list().setQ(String.format("trashed=false "+ " and modifiedDate > '%s'", date.toStringRfc3339()));

		
		fileMap =  new HashMap<>(); // need to be changed: id v.s. File
		
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

		for (com.google.api.services.drive.model.File f : result){
			List<ParentReference> tpr = f.getParents();
			if(tpr==null||tpr.size()==0){

				continue;
			}

			INode<com.google.api.services.drive.model.File> node ;
			if (nodesMap.containsKey(f.getId())){
				node = nodesMap.get(f.getId());
				node.setFile(f);
			}else{
				node = new GoogleINode();
				node.setFile(f);
				nodesMap.put(f.getId(), node);
			}
			node.setId(f.getId());
			if (f.getAppDataContents()){
				node.setDir(true);
			}

			ParentReference parent = f.getParents().get(0);
			INode<com.google.api.services.drive.model.File> parNode=null;
			if (nodesMap.containsKey(parent.getId())){
				parNode = nodesMap.get(parent.getId());

			}else {
				parNode = new GoogleINode();
				// new 
				if (fileMap.get(parent.getId())!=null){
					parNode.setFile(fileMap.get(parent.getId()));
				}
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
		
		for (com.google.api.services.drive.model.File f : result){
			INode<com.google.api.services.drive.model.File> node =nodesMap.get(f.getId());
			try {
				pathMap.put(node.getFullPathName(),  node);
				pathList.add(node.getFullPathName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pathList;
	}

	public void refresh(INode<com.google.api.services.drive.model.File> node) throws IOException{
		com.google.api.services.drive.model.File a = drive.files().get(node.getFileStr()).execute();
		node.setFile(a);


	}

}
