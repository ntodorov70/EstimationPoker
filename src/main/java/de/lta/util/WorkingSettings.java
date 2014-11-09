/*
 * Copyright 2013 Nikolay Todorov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.lta.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class WorkingSettings{
	File workingSettingsFile = null;
	String workingSettingsPath = ".";
	String workingSettingsFileName="WorkingSettings.properties";
	Properties workingSettings = new Properties();
	File defaultFile= new File(".");
	
	public WorkingSettings(Class mainClass){
		if(mainClass!=null)
			workingSettingsFileName = mainClass.getSimpleName() +".properties";
		
		loadSettings();
	}
	
	public File getFile(String key){
		String fileName = getWorkingProperty( key);
		if(fileName==null)
        	return defaultFile;
        File file = new File(fileName);
        return file;
	}
	
	public void setFile(String key, File file){
		if(file!=null /*&& file.exists()*/){
            workingSettings.setProperty(key,file.getAbsolutePath());
        }else{
        	workingSettings.remove(key);
        }
	}
	
    public String getWorkingProperty(String key){
    	return workingSettings.getProperty(key);
    }
    
    public void setWorkingProperty(String key, String value){
    	workingSettings.setProperty(key,value);
    }
	
	
    public void save(){
    	FileOutputStream out = null;
        try{
        	out = new FileOutputStream(workingSettingsFile); 
        	// put some comments on the top 
            workingSettings.store(out,"Working Settings");
        }catch(IOException e){
            //e.printStackTrace();
        }finally {
        	if(out != null){
        		try {
					out.close();
				} catch (IOException e1) {}
        	}
        }
    }

    private void loadSettings(){
		workingSettingsFile = new File(workingSettingsPath,workingSettingsFileName);
		FileInputStream wSettingIn = null;
        try {
            workingSettings.load(wSettingIn = new FileInputStream(workingSettingsFile));
        } catch (IOException e) {
        }finally{
            try{wSettingIn.close();}catch (Exception e){}
        }
	}

}
