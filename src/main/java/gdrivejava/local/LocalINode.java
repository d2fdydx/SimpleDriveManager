package gdrivejava.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

import gdrivejava.common.INode;

public class LocalINode extends INode<File>{

	@Override
	public void setFile(File file) {
		
		super.setFile(file);
		this.name=file.getName();
		this.fileStr=file.getAbsolutePath();
		this.setLastModifiedTime(file.lastModified());
		if (!file.isDirectory()){
			try {
				this.checkSum = getMd5CheckSum();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.checkSum = "";
			}
		}else{
			setDir(true);
		}
		this.file=null;
	}

	protected String getMd5CheckSum() throws Exception {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(getFile());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String res="";
		try {
			res= DigestUtils.md5Hex(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	
}
