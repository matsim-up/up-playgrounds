
package playground.onnene.transitScheduleMaker;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Some utility methods for creating the transitSchedule.xml file 
 * 
 * @author Onnene
 *
 */
public class FileMakerUtils {
    
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

	          //System.out.println("The file was compressed successfully!");

	      } catch (IOException ex) {

	          ex.printStackTrace();
	      }

	  }
	 
	 
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

	          //System.out.println("The file was decompressed successfully!");

	      } catch (IOException ex) {

	          ex.printStackTrace();

	      }

	  }
	 
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
	 
	 
	 
}
