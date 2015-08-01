package gdrivejava.google;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.model.File;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gdrivejava.common.INode;

public class GoogleINode extends INode<File>{

	
	
	public void setFile(File file) {
		
		super.setFile(file);
		setId(file.getId());
		this.fileStr="";
		this.name = file.getTitle();
		if (file.getMimeType().contains("application/vnd.google-apps.folder"))
			this.setDir(true);
		this.setLastModifiedTime(file.getModifiedDate().getValue());
		this.checkSum = file.getMd5Checksum();
	}
	
	
	
	/*
	protected void nullFileHandle()  {
		// TODO Auto-generated method stub
		/*
		com.google.api.client.json.GenericJson jf = new com.google.api.client.json.GenericJson();
		ObjectMapper mapper = 
			      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
		
		try {
			mapper.readValue(this.fileStr, File.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().setFieldNamingStrategy(new FieldNamingStrategy() {
			
			@Override
			public String translateName(java.lang.reflect.Field f) {
				// TODO Auto-generated method stub
				System.out.println(f.getName());
				return null;
			}
		}
			).setExclusionStrategies(new ExclusionStrategy(){

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				// TODO Auto-generated method stub
				if (f.getClass().equals(com.google.api.client.util.DateTime.class)){
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				// TODO Auto-generated method stub
				if (clazz.equals(com.google.api.client.util.DateTime.class))
					return true;
				return false;
			}
			
		}).create();
		
		if (this.fileStr !=null){
		
				System.out.println(this.fileStr);
				File temp =gson.fromJson(this.fileStr, File.class);
				if (temp!=null)
					setFile(temp);
		
		}
		
		
	}
*/
/*
 * 
 * new JsonDeserializer<File>() {
			
			private DateFormat df = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SZ");
			@Override
			public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				// TODO Auto-generated method stub
				Iterator<Entry<String, JsonElement>> itr = json.getAsJsonObject().entrySet().iterator();
				Entry<String, JsonElement> entry=null;
				while((entry = itr.next())!=null){
					
					switch(entry.getKey()){
					case "markedViewedByMeDate": 
						
						break;
					case "modifiedByMeDate":
						break;
					case  "modifiedDate":
						break;
					}
				}
				
				return null;
			}
			
			
		}
 */
}
