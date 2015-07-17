package gdrivejava.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

import gdrivejava.common.INode;

public class LocalINode extends INode<File>{

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return getFile().getName();
	}

	@Override
	protected long getModifiedTime() {
		// TODO Auto-generated method stub
		return getFile().lastModified();
	}

	@Override
	protected String getMd5CheckSum() {
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
