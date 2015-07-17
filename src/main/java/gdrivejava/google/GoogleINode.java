package gdrivejava.google;

import com.google.api.services.drive.model.File;

import gdrivejava.common.INode;

public class GoogleINode extends INode<File>{

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
	
		return getFile().getTitle();
	}

	@Override
	protected long getModifiedTime() {
		// TODO Auto-generated method stub
		return getFile().getModifiedDate().getValue();
	}

	@Override
	protected String getMd5CheckSum() {
		// TODO Auto-generated method stub
		return getFile().getMd5Checksum();
	}

}
