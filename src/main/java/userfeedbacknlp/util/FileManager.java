package userfeedbacknlp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.w3c.dom.Document;


import userfeedbacknlp.data.Feedback;
import userfeedbacknlp.data.Requirement;

public class FileManager {

	@SuppressWarnings("resource")
	public static void ChargeRequirementFromTXTFile(Requirement req) throws IOException{
		
		BufferedReader br = null;
        String line, nFile = "./data/" + req.getId() + ".txt";
        Map<Feedback, Double> auxFeed = req.getRelatedFeedback();
        int idnum=0;
        br = new BufferedReader(new FileReader(nFile));
        while ((line = br.readLine()) != null) {
            if (line != ""){            
            	line = line.replaceAll("(\\d)(.)", "$1");
            	idnum ++;
            	auxFeed.put(new Feedback(req.getId() + "_" + idnum, "", line), 1d);
            }
        }
	}
	
	public static void ChargeRequirementFromCVSFile(Requirement req) throws IOException{
		
		String nFile = "./data/" + req.getId() + ".csv";
		Map<Feedback, Double> auxFeed = req.getRelatedFeedback();

        Reader in = new FileReader(nFile);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("id", "user_id", "ticket_number", "user_statement", "feedback_creation_time", "feedback_type", "sentiment", "similarity_score").parse(in);
        for (CSVRecord record : records) {
        	if(!record.get("id").equals("id")){
        		auxFeed.put(new Feedback(req.getId() + "_" + record.get("id"), "", record.get("user_statement")), (record.get("similarity_score")=="")? 1: Double.parseDouble(record.get("similarity_score")));
        	}
        }
	}
	
	public static Map<Requirement, Set<Feedback>> readFeedbackPerRequirementFromCVSFile(String csvFile) throws IOException{
		
		Map<Requirement, Set<Feedback>> reqFeedback = new HashMap<Requirement, Set<Feedback>>();
		
		String nFile = "./data/" + csvFile;

        Reader in = new FileReader(nFile);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("id","user_statement","REQ_id").parse(in);
        for (CSVRecord record : records) {
        	if(!record.get("REQ_id").equals("REQ_id")){
        		String allReqIds = record.get("REQ_id");
        		if (allReqIds == null || allReqIds.isEmpty()) {
        			continue;
        		}
        		Feedback feedback = new Feedback(record.get("id"), "", record.get("user_statement")); // by default score 1
        		String[] reqIds = allReqIds.split(",");
        		for (String reqId : reqIds) {
        			reqId = reqId.trim();
        			Requirement req = new Requirement(reqId, "", "");
	        		if (!reqFeedback.containsKey(req)) {
	        			reqFeedback.put(req, new HashSet<Feedback>());
	        		}
	        		reqFeedback.get(req).add(feedback);
        		}
        	}
        }
        
        return reqFeedback;
	}
	
	public static void writeFile(String folderPath, String fileName, String lines){
		if(createFolder(folderPath.substring(0, folderPath.length()-1))==true)
		{
			try {
				byte[] out = UnicodeUtil.convert(lines.getBytes("UTF-16"), "UTF-8"); //Shanghai in Chinese  
				FileOutputStream fos = new FileOutputStream(folderPath  + fileName);  
				fos.write(out);  
				fos.close();  
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createFileProperties(){
		
	}
	
	private static boolean createFolder(String path){
		File folder = new File(path);
		boolean result=false;
		if (!folder.exists()) {
			System.out.println("creating folder: " + folder);
			result = folder.mkdir();  

			if(result) {    
				System.out.println("DIR created"); 
			}
		}
		if(folder.exists())
			result=true;
		return result;
	}
	
	public static Document convertStringToDocumentXML(String xmlStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder; 
		String xmlContent="";
		String XMLheader= "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
		xmlContent= XMLheader + "<Sentences>\n" + xmlStr + "</Sentences>";
		try 
		{  
			builder = factory.newDocumentBuilder();  
			Document document = builder.parse( new InputSource( new StringReader( xmlContent ) ) ); 
			return document;
		} catch (Exception e) {  
			e.printStackTrace();  
		} 
		return null;
	}
}
