
package playground.onnene.transitScheduleMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Helper class for the transit schedule maker that creates the
 * transit stops component of the transitSchedule.xml file.
 * 
 * @author Onnene
 *
 */
public class TransitStops {
    
    private Document d;
    private Element e;
    private File f;
    
    
    public TransitStops(Document d, Element e, File f){
        this.e = e;
        this.d = d;
        this.f = f;
    }
    
    public void stopFacility() throws IOException, FileNotFoundException{
        
        Scanner s = new Scanner(new FileInputStream(f));
        
        while(s.hasNext()){
            String [] a = s.nextLine().split(", ");
            
            Element e0 = d.createElement("stopFacility");
            e.appendChild(e0);
            
            e0.setAttribute("id", a[0]);
            e0.setAttribute("x", a[1]);
            e0.setAttribute("y", a[2]);
            e0.setAttribute("linkRefId", a[3]);
            e0.setAttribute("name", a[4]);
            e0.setAttribute("isBlocking", a[5]); 
        }
        s.close();
    }
    
}
