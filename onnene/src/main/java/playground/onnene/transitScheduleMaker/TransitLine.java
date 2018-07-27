package playground.onnene.transitScheduleMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.moeaframework.core.PRNG;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Helper class for the transit schedule maker that creates the
 * transit line component of the transitSchedule.xml file.
 * 
 * @author Onnene
 */
class TransitLine{
    
    private Document d;
    private Element e;
    private Element e0;
    private Element e00;
    
    String [] arr;
   
    //constructor
    TransitLine(Document d, Element e) {
        this.d = d;
        this.e = e;
    }
    
    //creating the transit line element
    public void transitLine(String id){
       
       e0 = d.createElement("transitLine");
       e.appendChild(e0);
       e0.setAttribute("id", id);       
    }
    
    //creating transit route element
    public void transitRoutes(String id, int num){
       e00 = d.createElement("transitRoute");
       e0.appendChild(e00);
       
       e00.setAttribute("id", id+"_"+num);
    }
    
    //creating transport mode element
    public void transportMode(){
       Element e000 = d.createElement("transportMode");
       e000.appendChild(d.createTextNode("bus"));
       e00.appendChild(e000);
    }
    
    //creating route profile
    public void routeProfile(String s, int i, int num, int flag, int numberOfLines){ 
       Element e000 = d.createElement("routeProfile");
       e00.appendChild(e000);
       
       //string slicing and appending routes to array
       String chop;
       //System.out.println(s);
       if(i == 0){
           chop = s.substring(2, s.length());
           //throw new StringIndexOutOfBoundsException();
       }
       else if(i == numberOfLines-1){
           chop = s.substring(2, s.length()-2);
           //throw new StringIndexOutOfBoundsException();
       }
       else{
           chop = s.substring(2, s.length());
           
       }
       arr = chop.split(", ");
 
     
       //Random r = new Random();
       //PRNG.nextInt(2);
       for(int j = 0; j<arr.length; j++){
           
            Element e0000 = d.createElement("stop");      
            e000.appendChild(e0000);

            //determines route order
            if(flag == 1){
                e0000.setAttribute("refId", arr[j]);
            }
            else{
                e0000.setAttribute("refId", arr[arr.length-1-j]);
            }
          
            int min = 3*j;
            int hr = 0;
            //random arrival/departure time
            if(min != 0){
                min = (PRNG.nextInt((2)+1)+min-2);  
            }
            
            //convert minutes to hours
            while(min>59){
                hr++;
                min -= 60;
            }
            String minstr = (min< 10) ? ("0"+min) : (""+min);
            String hrstr = (hr< 10) ? ("0"+hr) : (""+hr);
            
            //create to element
            e0000.setAttribute("arrivalOffset", hrstr+":"+minstr+":00");
            e0000.setAttribute("departureOffset", hrstr+":"+minstr+":00");
            e0000.setAttribute("awaitDeparture", "false");
       }
    }
    
    public void route(File f, int fl) throws FileNotFoundException{
       Element e000 = d.createElement("route");
       e00.appendChild(e000);
       
       for(int i = 0; i< arr.length; i++){
           Scanner s = new Scanner(new FileInputStream(f));
           while(s.hasNext()){
               String [] input = s.nextLine().split(", ");
               if(fl == 1){
                   if(input[0].equals(arr[i])){
                        Element e0000 = d.createElement("link");
                        e000.appendChild(e0000);
                        e0000.setAttribute("refId", input[3]);
                   
                   }
               }else{
                   if(input[0].equals(arr[arr.length-1-i])){
                        Element e0000 = d.createElement("link");
                        e000.appendChild(e0000);
                        e0000.setAttribute("refId", input[3]);
                   
                   }
               }
               
           }
           s.close();
       }
       
    }
    
    public Element departure(){
       Element e000 = d.createElement("departures");
       e00.appendChild(e000);
       
       return e000;
    }
    
    
}
