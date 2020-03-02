package userfeedbacknlp.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import eu.supersede.feedbackanalysis.ds.UserFeedback;
import gate.Corpus;
import gate.Factory;
import gate.Gate;
import gate.util.Out;
import userfeedbacknlp.data.Feedback;
import userfeedbacknlp.data.Requirement;
import userfeedbacknlp.data.TypeProperty;
import userfeedbacknlp.model.GATE;
import userfeedbacknlp.properties.IntentionPropety;
import userfeedbacknlp.properties.SentimentProperty;
import userfeedbacknlp.properties.SeverityProperty;

/**
 * Run Experiment
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try{
    		//=======Initialize objects for NLP===
    		//Sentiment Analysis
    		Properties props = new Properties();
    		props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
    		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    		
    		//Speech Acts Analysis
    		// Initialize plugins of GATE
    		
			System.setProperty("gate.home", ReFeedProperties.getInstance().getProperty("gate.home")); 
			System.setProperty("gate.plugins.home", ReFeedProperties.getInstance().getProperty("gate.plugins.home"));
			Gate.init();
			Out.prln("GATE initialised...");
			
			// Prepare the resources, processes and languages
			GATE gateProcessing = new GATE();
			gateProcessing.registerResources();
			gateProcessing.createProcessingResources();
			gateProcessing.addProcessingResources();
		
			// Create the corpus to process the documents
			Corpus corpus = Factory.newCorpus("corpus"); 
			gateProcessing.setCorpus(corpus);
    		
    		//Read requirement 
    		Similarity similarity = new Similarity();
    		
    		// file containing the requirements
    		String requirementsFile = "requirements.csv";
    		
    		// file containing the user feedback messages
    		String goldStandardFile = "sample_feedback_oo.csv";    		
    		
    		// folder where feedback similar to each requirements will be written
    		String similarFeedbackDir = "./data/similarity/";
    		
    		//Create similarity
    		boolean recompute = true;
    		if (recompute) {
    			Map<Requirement, Map<UserFeedback, Double>> similarFeedback = similarity.computeSimilarFeedback(requirementsFile, goldStandardFile);
    			similarity.writeSimilarFeedbackToFile(similarFeedback, similarFeedbackDir, 0d);
    		}
    		
    		double similarityThreshold = 0d;
    		Map<Requirement, Map<Feedback, Double>> similarFeedbacks = similarity.loadComputedSimilarFeedbacks(similarFeedbackDir, similarityThreshold);
    		
    		// compute precision/recall wrt Itzel's gold standard (internal check)
    		Map<Requirement, Set<Feedback>> goldStandard = userfeedbacknlp.util.FileManager.readFeedbackPerRequirementFromCVSFile(goldStandardFile);
    		
    		Similarity.computeScore(goldStandard, similarFeedbacks);
    		
    		if (false)
    			System.exit(0);
    		// Created requirements and related user feedback from previous Map
    		List<Requirement> lstReq = new ArrayList<Requirement>();
    		for(Requirement req : similarFeedbacks.keySet()){
    			for(Feedback feed : similarFeedbacks.get(req).keySet()){
//    				feed.setScore(similarFeedbacks.get(req).get(feed).doubleValue());
    				req.getRelatedFeedback().put(feed, similarFeedbacks.get(req).get(feed).doubleValue());
    			}
    			lstReq.add(req);
    		}
    		
    		
    		// Create Requirements
    		//lstReq.add( new Requirement("req1_1_1", "title", "description"));
    		//lstReq.add( new Requirement("req1_2", "title", "description"));
    		
    		Writer writerReq = new FileWriter("./data/results_properties/requirements_result.csv");
    		CSVPrinter csvPrinterReq = new CSVPrinter(writerReq, CSVFormat.RFC4180.withHeader("id", "requirement", "totalScore"));
    		
    		for(Requirement req : lstReq){
        		// Charger related feedback to the requirement from a CSV file (Before was already calculated)
        		//FileManager.ChargeRequirementFromCVSFile(req);
        		
        		Writer writer = new FileWriter("./data/results_properties/" + req.getId() + "_result.csv");
        		CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withHeader("id", "feedback", "number_positive", 
        				"positive_sentiment", "number_negative", "negative_sentiment", 
        				"annotation", "intention", 
        				"total_lenght", "negative_lengt", "severity", 
        				"similarity_score", "total_score"));
        		double totaltemp = 0; 
        				
        		// Create and calculate properties for each related feedback
        		for(Entry<Feedback, Double> entry: req.getRelatedFeedback().entrySet()){
        			Feedback feed = entry.getKey();
        			Double similarityScore = entry.getValue();
        			// Sentiment
        			SentimentProperty sent = new SentimentProperty(feed.getDescription(), pipeline);
        			sent.calculateValues();
        			feed.getProperties().put(TypeProperty.sentiment, sent);
        			
        			// Intention
        			IntentionPropety inten = new IntentionPropety(feed.getDescription(), feed.getId(), 
        					gateProcessing, corpus);
        			inten.calculateValues();
        			feed.getProperties().put(TypeProperty.intention, inten);
        			
        			// Severity (missing)
        			SeverityProperty sever = new SeverityProperty(feed.getDescription().length(), sent.getLenghtNegative());
        			sever.calculateValues();
        			
        			// Total Value
        			feed.setTotalValueProperties(similarityScore *
        					(sent.getPositiveSentiment() + Math.abs(sent.getNegativeSentiment()) 
        					+ inten.getValueIntention()
        					+ sever.getValueSeverity()));
        			
        			// Register properties in file 
        			//Total value = (positive sentiment + ABS(negative sentiment) + intention value) * score
        			/*fileResult = fileResult + "\n" + feed.getDescription() + " ~ " + 
        						sent.getPositiveSentiment() + " ~ " + //between [0,1]
        						sent.getNegativeSentiment() + " ~ " + //between [0,1]
        						inten.getValueIntention() + " ~ " + //between [0,1]
        						feed.getScore()+ " ~ " + 
        						feed.getTotalValueProperties();*/

            		csvPrinter.printRecord(feed.getId(), feed.getDescription(), sent.getNumberPositiveSentences(),
            				sent.getPositiveSentiment(), sent.getNumberNegativeSentences(), sent.getNegativeSentiment(), 
            				inten.getAnnotations(), inten.getValueIntention(), 
            				feed.getDescription().length(), sent.getLenghtNegative(), sever.getValueSeverity(),
            				similarityScore, feed.getTotalValueProperties());
        			
        			totaltemp += feed.getTotalValueProperties();
        		}
        		csvPrinter.close();
        		
        		// Aggregate properties for the requirement - average value?
        		if(req.getRelatedFeedback().isEmpty()){
        			req.setTotalValueProperties(0.0);
        		}
        		else{
        			req.setTotalValueProperties(totaltemp / req.getRelatedFeedback().size());
        			System.out.println("Total requirement value: " + req.getTotalValueProperties());
        			System.out.println("========================================================");
        		}    
        		csvPrinterReq.printRecord(req.getId(), req.getDescription(), req.getTotalValueProperties());
    		}
    		csvPrinterReq.close();
    		
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
}
