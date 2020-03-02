package userfeedbacknlp.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import eu.supersede.feedbackanalysis.clustering.FeedbackSimilarity;
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
public class ExpMaterialGenerator 
{
	static Random random;
	StanfordCoreNLP pipeline;
	GATE gateProcessing;
	Corpus corpus;
	/**
	 * 
	 */
	public ExpMaterialGenerator() {
		long seed = System.currentTimeMillis();
		random = new Random();
		System.out.println(seed);
		
		//=======Initialize objects for NLP===
		//Sentiment Analysis
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
		
		//Speech Acts Analysis
		// Initialize plugins of GATE
		try {
			System.setProperty("gate.home", ReFeedProperties.getInstance().getProperty("gate.home")); 
			System.setProperty("gate.plugins.home", ReFeedProperties.getInstance().getProperty("gate.plugins.home"));
			Gate.init();
			Out.prln("GATE initialised...");
			
			// Prepare the resources, processes and languages
			gateProcessing = new GATE();
			gateProcessing.registerResources();
			gateProcessing.createProcessingResources();
			gateProcessing.addProcessingResources();
		
			// Create the corpus to process the documents
			corpus = Factory.newCorpus("corpus"); 
			gateProcessing.setCorpus(corpus);
			
			generateSets();
    		
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void generateSets() throws Exception {
		//Read requirement 
		Similarity similarity = new Similarity();
		String requirementsFile = "requirements.csv";
		String goldStandardFile = "sample_feedback_oo.csv";    		
//		String similarFeedbackDir = "./data/experiment_material/tmp/";
		
		int numReq = 7;
		int numFeedback = 30;
		
		String ontologyFile = "SDO_ontology.ttl";
    	String wordnetDbPath = ReFeedProperties.getInstance().getProperty("wordnetdbpath");
    	FeedbackSimilarity feedbackSimilarity = new FeedbackSimilarity(ontologyFile, wordnetDbPath);
    	
    	List<Requirement> allRequirements = similarity.loadRequirements (requirementsFile);
    	List<UserFeedback> allFeedbacks = similarity.loadUserFeedbacks(goldStandardFile);

    	for (int set = 1; set <= 2; set++) {
    		String outputDir = "./data/experiment_material/set" + set + "/";
	    	List<Requirement> requirements = new ArrayList<Requirement>();
	    	for (int i = 0; i < numReq; i++) {
	    		Requirement r = ExpMaterialGenerator.pickRandom(allRequirements);
				requirements.add(r);
				allRequirements.remove(r);
	    	}
	    	
	    	List<UserFeedback> allFeedback = new ArrayList<UserFeedback>();
	    	for (int i = 0; i < numFeedback; i++) {
	    		UserFeedback f = ExpMaterialGenerator.pickRandom(allFeedbacks);
				allFeedback.add(f);
				allFeedbacks.remove(f);
	    	}
	    	
	    	Map<Requirement, Map<Feedback, Double>> similarFeedbacks = new HashMap<Requirement, Map<Feedback,Double>>();
	    	int N = allFeedback.size(); // 10;
	    	for (Requirement req : requirements) {
	    		UserFeedback r = new UserFeedback(req.getDescription());
	    		Map<UserFeedback, Double> similarFeedback = feedbackSimilarity.getSimilarFeedback(allFeedback, r, N);
	    		Map<Feedback, Double> feedbacks = new HashMap<Feedback, Double>();
	    		for (Entry<UserFeedback, Double> entry : similarFeedback.entrySet()) {
	    			Feedback fb = new Feedback(entry.getKey().getFeedbackId(), "", entry.getKey().getFeedbackText());
	    			feedbacks.put(fb, entry.getValue());
	    		}
	    		req.getRelatedFeedback().putAll(feedbacks);
	    		similarFeedbacks.put(req, feedbacks);
	    	}
			
	//		double similarityThreshold = 0.05;
	//		Map<Requirement, Map<Feedback, Double>> similarFeedbacks = similarity.loadComputedSimilarFeedbacks(similarFeedbackDir, similarityThreshold);
			
	    	exportRequirementScores(similarFeedbacks, outputDir);
    	}
//		return similarFeedbacks;
	}
	
	public static <T> T pickRandom (List<T> list) {
		Collections.shuffle(list);
		int size = list.size();
		int item = random.nextInt(size); 
		return list.get(item);
	}
	
    public static void main( String[] args ) {
    	new ExpMaterialGenerator();
    }
    
    public void exportRequirementScores (Map<Requirement, Map<Feedback, Double>> similarFeedbacks, String outputDir) throws Exception {
    	File outputFolder = new File(outputDir);
    	if (outputFolder.exists()) {
    		FileUtils.deleteDirectory(outputFolder);
    	}
    	outputFolder.mkdirs();
    	
    	Writer writerReq = new FileWriter(outputDir + "/requirements_result.csv");
		CSVPrinter csvPrinterReq = new CSVPrinter(writerReq, CSVFormat.RFC4180.withHeader("id", "requirement", "totalScore"));
		
		Set<Feedback> allFeedback = new HashSet<Feedback>();
		for(Entry<Requirement, Map<Feedback, Double>> outerEntry : similarFeedbacks.entrySet()){
			Requirement req = outerEntry.getKey();
    		allFeedback.addAll(outerEntry.getValue().keySet());
			// Charger related feedback to the requirement from a CSV file (Before was already calculated)
    		//FileManager.ChargeRequirementFromCVSFile(req);
    		
    		Writer writer = new FileWriter(outputDir + "/" + req.getId() + "_result.csv");
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
		
		// write the feedbacks
		writeFeedbacks (allFeedback, outputDir);
    }

	/**
	 * @param allFeedback
	 * @param outputDir
	 */
	private void writeFeedbacks(Set<Feedback> allFeedback, String outputDir) throws Exception {
		String outputFile = outputDir + "/feedbacks.csv";
		Writer writer = new FileWriter(outputFile);
		CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withHeader("id", "feedback"));
		for (Feedback fb : allFeedback) {
			csvPrinter.printRecord(fb.getId(), fb.getDescription());
		}
		csvPrinter.close();
	}
}
