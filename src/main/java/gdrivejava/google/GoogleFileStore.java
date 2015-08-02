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
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Changes;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import gdrivejava.common.INode;
import gdrivejava.common.INodeWalker;
import gdrivejava.common.ListenerMap;
import gdrivejava.event.listener.ModifiedListener;
import gdrivejava.main.DriveMain;

//import com.google.api.services.drive.model.File;

public class GoogleFileStore implements Serializable{


	transient Map<String, com.google.api.services.drive.model.File> fileMap = null; // need to be changed: id v.s. File
	Map<String, INode<com.google.api.services.drive.model.File>> nodesMap = null; // id, Inode
	Map<String, INode<com.google.api.services.drive.model.File>> pathMap =null;
	final static String FOLDER_MIME="application/vnd.google-apps.folder";
	final static String FILE_MIME="application/vnd.google-apps.file";
	long lastModifiedTime=0;
	long lastChangedTime=0;
	long largestChangedId = 0;
	INode<com.google.api.services.drive.model.File> rootNode =null;
	transient boolean dirty =false;

	
	


	public boolean isDirty() {
		return dirty;
	}



	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}



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



	public GoogleFileStore(GoogleFileSystem fs) throws Exception  {
		// TODO Auto-generated constructor stub
		
		
		fileMap = new HashMap<String, com.google.api.services.drive.model.File>();
		
		nodesMap = new HashMap<>(); 


		HashMap<String , INode<com.google.api.services.drive.model.File>> tpathMap = new HashMap<>();
		pathMap = new ListenerMap<>(tpathMap,new ModifiedListener<INode<com.google.api.services.drive.model.File>>() {

			@Override
			public void notifyModified(INode<com.google.api.services.drive.model.File> node) {
				if (lastModifiedTime < node.getLastModifiedTime()){
					lastModifiedTime = node.getLastModifiedTime();
				}
				
			}

		}); 
		
		
		
		googleFs = fs;
		drive = googleFs.getDrive();

		
		buildStore();
		
		
	}





	public Map<String, INode<com.google.api.services.drive.model.File>> getPathMap() {
		return pathMap;
	}






	public void uploadFile(String path) throws Exception{
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
					setDirty(true);
					
				}else{
					System.out.println(" upload === null"+ file.getName());
					throw new Exception("cannot upload");
				}

			}else{
				System.out.println("parent Folder cannnot be created");
				throw new Exception("cannot upload");

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
					setDirty(true);
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
		refresh(node);
		com.google.api.services.drive.model.File f  = node.getFile();
		///	String downloadUrl = f.getDownloadUrl();
		String surl = f.getDownloadUrl();
		File output = new File(FilenameUtils.concat(DriveMain.LocationPath , path));

		System.out.println(FilenameUtils.concat(DriveMain.LocationPath , path));
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

			//getAllRemoteDirs();
			getAllRemoteFiles();
			INodeWalker walker = new INodeWalker();
			//walker.printStructure(rootNode);
			System.out.println("find largest ID");;
			setLargeChangeId();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("cannot build Store");
		}	



	}



	public List<com.google.api.services.drive.model.File>  getAllRemoteFiles() throws IOException{

		fileMap=new HashMap<>();
		List<com.google.api.services.drive.model.File> result = listFiles("trashed=false");

	
		buildNode(result);
	
		
	
		
		for(INode<com.google.api.services.drive.model.File> temp: nodesMap.values()){
			pathMap.put(temp.getFullPathName(),(INode<com.google.api.services.drive.model.File>) temp);
		}
		
		
		return result;



	}


	
	public List<String> getUpdatedAndCreated() throws IOException{
		DateTime date = new DateTime (lastModifiedTime);
		
		List<String> pathList = new ArrayList<>();


		System.out.println(date.toStringRfc3339());
		fileMap =  new HashMap<>(); // need to be changed: id v.s. File
		List<com.google.api.services.drive.model.File> result = listFiles(String.format("trashed=false "+ " and modifiedDate > '%s'", date.toStringRfc3339()));
		
		buildNode(result);
		
	
		
		for (com.google.api.services.drive.model.File f : fileMap.values()){
			INode<com.google.api.services.drive.model.File> node =nodesMap.get(f.getId());
			try {
				pathMap.put(node.getFullPathName(),  node);
				pathList.add(node.getFullPathName());
				setDirty(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pathList;
	}

	public void getChanges(List<INode<com.google.api.services.drive.model.File>> removed,List<INode<com.google.api.services.drive.model.File>> recovered ) throws IOException{
		
		List<Change> result = new ArrayList<Change>();
	    Changes.List request = drive.changes().list();

	    
	    request.setStartChangeId(largestChangedId);
	    long tlarget =	largestChangedId;
	    do {
	      try {
	        ChangeList changes = request.execute();
	        tlarget= changes.getLargestChangeId();
	        result.addAll(changes.getItems());
	        request.setPageToken(changes.getNextPageToken());
	      } catch (IOException e) {
	        System.out.println("An error occurred: " + e);
	        request.setPageToken(null);
	      }
	    } while (request.getPageToken() != null &&
	             request.getPageToken().length() > 0);

	    for ( Change change : result){
	    	long time =change.getModificationDate().getValue();
	    	if (time > lastChangedTime){
		    	if (change.getDeleted()|| change.getFile().getLabels().getTrashed()){
	
		    		 INode<com.google.api.services.drive.model.File> node = nodesMap.get(change.getFileId());
		    		
		    		 removed.add(node);
		    		 nodesMap.remove(node.getId());
		    		 pathMap.remove(node.getFullPathName());
		    		 setDirty(true);
	
		    	}
		    	lastChangedTime =time;
	    	}
	    	
	    }
	    largestChangedId=tlarget;
	  
	}

	
	public void setLargeChangeId(){
		
		com.google.api.services.drive.Drive.Changes.List request=null;
		
		try {
			request = drive.changes().list().setIncludeDeleted(true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (request==null){
			
			return ;
		}
		request.setStartChangeId(999999999l);
		
		ChangeList changes;
		try {
			changes = request.execute();
			 largestChangedId= changes.getLargestChangeId();
			  System.out.println("largest==========");
		      System.out.println(changes.getLargestChangeId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
		
	}
	
	List<com.google.api.services.drive.model.File> listFiles(String query) throws IOException{
		List<com.google.api.services.drive.model.File> result = new ArrayList<com.google.api.services.drive.model.File>();
		Files.List request = drive.files().list().setQ(query);
		
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
		return result;
	}
	
	void buildNode(List<com.google.api.services.drive.model.File> result){
		for (com.google.api.services.drive.model.File f : result){
			if (f.getParents().size()==0){
				System.out.println("file with no parent : skip");
				continue;
			}

			INode<com.google.api.services.drive.model.File> node=null ;
			
			if (nodesMap.containsKey(f.getId())){
				node = nodesMap.get(f.getId());
				node.setFile(f);
			}else{
				node = new GoogleINode();
				node.setFile(f);
				nodesMap.put(f.getId(), node);
			}

			

			ParentReference parent = f.getParents().get(0);
			INode<com.google.api.services.drive.model.File> parNode=null;
			
			if (nodesMap.containsKey(parent.getId())){ // if exist parent node
				parNode = nodesMap.get(parent.getId());

			}else {
				parNode = new GoogleINode();
				nodesMap.put(parent.getId(),parNode);	
			}
			
			if (fileMap.get(parent.getId())!=null){
				parNode.setFile(fileMap.get(parent.getId()));
			}
			
			
			if (parent.getIsRoot()){			
				parNode.setRoot(true);
				rootNode=parNode;
			}
			
			node.setParent(parNode);
			parNode.addChild(node);
			parNode.setId(parent.getId());

		}
		
	}
	
	
	public void refresh(INode<com.google.api.services.drive.model.File> node) throws IOException{
		com.google.api.services.drive.model.File a = drive.files().get(node.getId()).execute();
		node.setFile(a);
		setDirty(true);

	}



	public void deleteIndex(String path) {
		pathMap.remove(path);
		
	}




}
