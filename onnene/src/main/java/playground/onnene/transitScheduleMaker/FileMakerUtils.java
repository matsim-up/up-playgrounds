
package playground.onnene.transitScheduleMaker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;


/**
 * Some utility methods for creating the transitSchedule.xml file 
 * 
 * @author Onnene
 *
 */
public class FileMakerUtils {
	
	private static final Logger log = Logger.getLogger(FileMakerUtils.class);
    
    private Formatter filename;
   
    public void openFile(String text){
        try{
             filename = new Formatter(text+".txt");
             
        }
        catch(Exception e){
            
        }
    }
    public void addRecord(String record){
        filename.format("%s", record);
    }
    public void closeFile(){
        filename.close();
    }
    
    
    //create gzip
    public void gzipFile(String source_filepath, String destinaton_zip_filepath) {

	      byte[] buffer = new byte[1024];

	      try {

	          FileOutputStream fileOutputStream = new FileOutputStream(destinaton_zip_filepath);
	          GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);
	          FileInputStream fileInput = new FileInputStream(source_filepath);

	          int bytes_read;

	          while ((bytes_read = fileInput.read(buffer)) > 0) {

	              gzipOuputStream.write(buffer, 0, bytes_read);

	          }

	          fileInput.close();
	          gzipOuputStream.finish();
	          gzipOuputStream.close();

	          //log.info("The file was compressed successfully!");

	      } catch (IOException ex) {

	          ex.printStackTrace();
	      }

	  }
	 
	 
     //unzip gzip
	 public void unGunzipFile(String compressedFile, String decompressedFile) {

	      byte[] buffer = new byte[1024];

	      try {

	          FileInputStream fileIn = new FileInputStream(compressedFile);
	          GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
	          FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);

	          int bytes_read;

	          while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
	              fileOutputStream.write(buffer, 0, bytes_read);

	          }

	          gZIPInputStream.close();
	          fileOutputStream.close();

	          //log.info("The file was decompressed successfully!");

	      } catch (IOException ex) {

	          ex.printStackTrace();

	      }

	  }
	 
	 //count lines in a file
	 public static int count(String filename) throws IOException {
		    InputStream is = new BufferedInputStream(new FileInputStream(filename));
		    try {
		        byte[] c = new byte[1024];
		        int count = 0;
		        int readChars = 0;
		        boolean endsWithoutNewLine = false;
		        while ((readChars = is.read(c)) != -1) {
		            for (int i = 0; i < readChars; ++i) {
		                if (c[i] == '\n')
		                    ++count;
		            }
		            endsWithoutNewLine = (c[readChars - 1] != '\n');
		        }
		        if(endsWithoutNewLine) {
		            ++count;
		        } 
		        return count;
		    } finally {
		        is.close();
		    }
		}
	 
	// Sort an array of files
	    public static void sortArrayOfFiles(File[] files){
			
			Arrays.sort(files, new Comparator<File>()  {
			    
				@Override
	            public int compare(File f1, File f2) {
	                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
	                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
	                return n1 - n2;
	            }
		
			});
		
		}
		
	     //Sort a list of files
		 public static List<File> SortListOfFiles(List<File> files){
		    
	    	Collections.sort(files, new Comparator<File>(){

				@Override
				public int compare(File f1, File f2) {
					
					return Integer.compare(Integer.parseInt(f1.getName().replaceAll("\\D+", "")),Integer.parseInt(f2.getName().replaceAll("\\D+", "")));
				}

	    	});
	    	
	    
	    	return files;    	
		  }
	 
	 
	 
}
