package playground.onnene.exampleCode;


import java.io.File;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.jcabi.xml.XMLDocument;

public class MatsimNetwork2Csv {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		System.out.println("start of programme");
	
        File stylesheet = new File("C:\\Users\\AMAMIFE\\Desktop\\MOEA\\phd-onnene-matsim-project\\src\\main\\java\\customClasses\\outputFolder\\transitNetwork\\node_style.xsl");
        File xmlSource = new File("C:\\Users\\AMAMIFE\\Desktop\\MOEA\\phd-onnene-matsim-project\\src\\main\\java\\customClasses\\outputFolder\\transitNetwork\\transitNetwork_copy.xml");
       

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);

        System.out.println("mid of programme");
        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Source source = new DOMSource(document);
        Result outputTarget = new StreamResult(new File("C:\\Users\\AMAMIFE\\Desktop\\MOEA\\phd-onnene-matsim-project\\src\\main\\java\\customClasses\\outputFolder\\transitNetwork\\myciti_node.csv"));
        System.out.println(outputTarget);
        
        
     
        transformer.transform(source, outputTarget);
        
        
        /*System.out.println(source);*/
        
        /*String xml = new XMLDocument(source).toString();
        
        System.out.println(xml);*/
        File xmlSource1 = new File("C:\\Users\\AMAMIFE\\Desktop\\MOEA\\phd-onnene-matsim-project\\src\\main\\java\\customClasses\\outputFolder\\transitNetwork\\sample_network.xml");
        File stylesheet1 = new File("C:\\Users\\AMAMIFE\\Desktop\\MOEA\\phd-onnene-matsim-project\\src\\main\\java\\customClasses\\outputFolder\\transitNetwork\\graph.xsl");
        
        
        DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder1 = factory.newDocumentBuilder();
        Document document1 = builder.parse(xmlSource1);
        
        
        StreamSource stylesource1 = new StreamSource(stylesheet1);        
        Transformer transformer1 = TransformerFactory.newInstance().newTransformer(stylesource1);
        
        Source source1 = new DOMSource(document1);
        
       
        Result outputTarget1 = new StreamResult(new File("C:\\Users\\AMAMIFE\\Desktop\\MOEA\\phd-onnene-matsim-project\\src\\main\\java\\customClasses\\outputFolder\\transitNetwork\\output1.xml"));
        transformer.transform(source1, outputTarget1);
        
        
        String xml1 = new XMLDocument(source1).toString();
        System.out.println(xml1);
        
        
        System.out.println("end of programme");

	}

}
