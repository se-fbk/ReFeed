/**
 * 
 */
package userfeedbacknlp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import eu.supersede.feedbackanalysis.clustering.FeedbackMessage;
import eu.supersede.feedbackanalysis.clustering.FeedbackSimilarity;
import eu.supersede.feedbackanalysis.clustering.Utils;
import eu.supersede.feedbackanalysis.ds.UserFeedback;
import eu.supersede.feedbackanalysis.preprocessing.utils.FileManager;
import userfeedbacknlp.data.Feedback;
import userfeedbacknlp.data.Requirement;

/**
 * @author fitsum
 *
 */
public class Similarity {

	static String wordnetDbPath = ReFeedProperties.getInstance().getProperty("wordnetdbpath");
	
	public Map<Requirement, Map<UserFeedback, Double>> computeSimilarFeedback(String requirementsFile, String annotatedFeedbackFile){
		Map<Requirement, Map<UserFeedback, Double>> similarFeedbacks = new LinkedHashMap<Requirement, Map<UserFeedback,Double>>();
    	
    	String ontologyFile = "SDO_ontology.ttl";
    	FeedbackSimilarity feedbackSimilarity = new FeedbackSimilarity(ontologyFile, wordnetDbPath);
    	
    	List<Requirement> requirements = loadRequirements (requirementsFile);
    	List<UserFeedback> allFeedback = loadUserFeedbacks(annotatedFeedbackFile);
    	
    	int N = allFeedback.size(); // 10;
    	for (Requirement req : requirements) {
    		UserFeedback r = new UserFeedback(req.getDescription());
    		Map<UserFeedback, Double> similarFeedback = feedbackSimilarity.getSimilarFeedback(allFeedback, r, N);
    		similarFeedbacks.put(req, similarFeedback);
    	}
    	
    	return similarFeedbacks;
	}

	
	public Map<Requirement, Map<UserFeedback, Double>> computeSimilarFeedback(String requirementsFile, String annotatedFeedbackFile, int numReq, int numFeedback){
		Map<Requirement, Map<UserFeedback, Double>> similarFeedbacks = new LinkedHashMap<Requirement, Map<UserFeedback,Double>>();
    	String ontologyFile = "SDO_ontology.ttl";
    	
    	FeedbackSimilarity feedbackSimilarity = new FeedbackSimilarity(ontologyFile, wordnetDbPath);
    	
    	List<Requirement> allRequirements = loadRequirements (requirementsFile);
    	List<Requirement> requirements = new ArrayList<Requirement>();
    	for (int i = 0; i < numReq; i++) {
    		requirements.add(ExpMaterialGenerator.pickRandom(allRequirements));
    	}
    	
    	List<UserFeedback> allFeedbacks = loadUserFeedbacks(annotatedFeedbackFile);
    	List<UserFeedback> allFeedback = new ArrayList<UserFeedback>();
    	for (int i = 0; i < numFeedback; i++) {
    		allFeedback.add(ExpMaterialGenerator.pickRandom(allFeedbacks));
    	}
    	
    	int N = allFeedback.size(); // 10;
    	for (Requirement req : requirements) {
    		UserFeedback r = new UserFeedback(req.getDescription());
    		Map<UserFeedback, Double> similarFeedback = feedbackSimilarity.getSimilarFeedback(allFeedback, r, N);
    		similarFeedbacks.put(req, similarFeedback);
    	}
    	
    	return similarFeedbacks;
	}
	
	public static void main(String[] args) throws IOException {
		Similarity similarity = new Similarity();

		String requirementsFile = "requirements.csv";
		String goldStandardFile = "sample_feedback_oo.csv";
		
		String similarFeedbackDir = "./data/similarity/";
		
		boolean recompute = false;
		if (recompute) {
			Map<Requirement, Map<UserFeedback, Double>> similarFeedback = similarity.computeSimilarFeedback(requirementsFile, goldStandardFile);
			similarity.writeSimilarFeedbackToFile(similarFeedback, similarFeedbackDir, 0d);
		}
		
		double similarityThreshold = 0.05;
		Map<Requirement, Map<Feedback, Double>> similarFeedbacks = similarity.loadComputedSimilarFeedbacks(similarFeedbackDir, similarityThreshold);
		
		// compute precision/recall wrt Itzel's gold standard
		Map<Requirement, Set<Feedback>> goldStandard = userfeedbacknlp.util.FileManager.readFeedbackPerRequirementFromCVSFile(goldStandardFile);
		
		computeScore (goldStandard, similarFeedbacks);
		
	}
	
	/**
	 * @param goldStandard
	 * @param similarFeedback
	 */
	public static void computeScore(Map<Requirement, Set<Feedback>> goldStandard,
			Map<Requirement, Map<Feedback, Double>> similarFeedbacks) {
		// for each requirement, see what percentage of the feedbacks in the gold standard are found by the similarity algorithm
		for (Entry<Requirement, Map<Feedback, Double>> similarityEntry : similarFeedbacks.entrySet()) {
			
			Set<Feedback> similar = similarityEntry.getValue().keySet();
			Set<Feedback> similarGold = goldStandard.get(similarityEntry.getKey());
			
			// handle case of missing gold standard
			if (similarGold == null) {
				similarGold = new HashSet<Feedback>();
			}
			
			double[] precisionRecall = computePrecisionRecall (similar, similarGold);
			
			System.out.println("ReqId: " + similarityEntry.getKey().getId() + " Prec: " + precisionRecall[0] + " Recall: " + precisionRecall[1]);
		}
		
	}

	/**
	 * @param similar
	 * @param similarGold
	 * @return
	 */
	private static double[] computePrecisionRecall(Set<Feedback> similar, Set<Feedback> similarGold) {
		Set<Feedback> intersection = new HashSet<Feedback>(similar);
		intersection.retainAll(similarGold);
		
		double prec = (double)intersection.size() / (double)similar.size();
		double recall = similarGold.isEmpty()?0d: (double)intersection.size() / (double)similarGold.size();
		
		double[] result = {prec, recall};
		
		return result;
	}

	public void writeSimilarFeedbackToFile (Map<Requirement, Map<UserFeedback, Double>> similarFeedbacks, String outputDir, double threshold) throws IOException {
		File outputFolder = new File(outputDir);
    	if (outputFolder.exists()) {
    		FileUtils.deleteDirectory(outputFolder);
    	}
    	outputFolder.mkdirs();
		for (Entry<Requirement, Map<UserFeedback, Double>> entry : similarFeedbacks.entrySet()) {
			String fileName = outputDir + File.separator + entry.getKey().getId() + ".csv";
			Writer writer = new FileWriter(fileName);
			
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withHeader("id", "feedback", "similarity_score"));
			
			for (Entry<UserFeedback, Double> feedbackEntry : entry.getValue().entrySet()) {
				if (feedbackEntry.getValue() > threshold) {
					csvPrinter.printRecord(feedbackEntry.getKey().getFeedbackId(), feedbackEntry.getKey().getFeedbackText(), feedbackEntry.getValue().toString());
				}
			}
			csvPrinter.close();
		}
//		return outputDir;
	}
	
	
	public Map<Requirement, Map<Feedback, Double>> loadComputedSimilarFeedbacks(String inputDir, double similarityThreshold) throws IOException{
		Map<Requirement, Map<Feedback, Double>> similarFeedbacks = new HashMap<Requirement, Map<Feedback,Double>>();
		
		File inputDirectory = new File(inputDir);
		if (inputDirectory.isDirectory()) {
			File[] csvFiles = inputDirectory.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(".csv");
			    }
			});
			for (File csvFile : csvFiles) {
				String reqId = csvFile.getName().replaceAll(".csv", "");
				Requirement requirement = new Requirement(reqId, "", "");
				similarFeedbacks.put(requirement, new HashMap<Feedback, Double>());
				Reader reader = new FileReader(csvFile);
				Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("id", "feedback", "similarity_score").parse(reader);
				for (CSVRecord record : records) {
					if(!record.get("id").equals("id")) {
						Feedback feedback = new Feedback(record.get("id"), "", record.get("feedback"));// 1 by default?
						double similarityScore = Double.parseDouble(record.get("similarity_score"));
						if (similarityScore >= similarityThreshold) {
							requirement.getRelatedFeedback().put(feedback, similarityScore);
							similarFeedbacks.get(requirement).put(feedback, similarityScore);
						}
					}
				}
			}
		}
		return similarFeedbacks;
	}
	
	/**
	 * @param feedbacksFile: a csv file containing a list of feedback messages. No header.
	 * @return a list of UserFeedback objects, each representing a user feedback message.
	 */
	public List<UserFeedback> loadUserFeedbacks(String feedbacksFile) {
		List<UserFeedback> userFeedbacks = new ArrayList<UserFeedback>();
		try {
			Reader reader = new FileReader(new File(FileManager.getResource(feedbacksFile, getClass().getClassLoader()).getFile()));
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("id","user_statement","REQ_id").parse(reader);
			for (CSVRecord record : records) {
				if (!record.get("id").equals("id")) {
					String id = record.get("id");
					String message = record.get("user_statement");
					UserFeedback userFeedback = new UserFeedback(message);
					userFeedback.setFeedbackId(id);
					userFeedbacks.add(userFeedback);
				}
			}
		} catch (IOException ioException) {
			throw new RuntimeException("Error reading csv file: " + feedbacksFile);
		}
		return userFeedbacks;
	}

	/**
	 * @param requirementsFile: a csv file containing a list of requirements. Header: id,title,description
	 * @return a list of Requirement objects, each representing a requirement.
	 */
	public List<Requirement> loadRequirements(String requirementsFile) {
		List<Requirement> requirements = new ArrayList<Requirement>();
		try {
			Reader reader = new FileReader(new File(FileManager.getResource(requirementsFile, getClass().getClassLoader()).getFile()));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> records = csvParser.getRecords();
			for (int i = 1; i < records.size(); i++) { // start from 1, skip header
				CSVRecord record = records.get(i);
				String id = record.get(0);
				String title = record.get(1);
				String description = record.get(2);
				Requirement requirement = new Requirement(id, title, description);
				requirements.add(requirement);
			}
			csvParser.close();
		} catch (IOException ioException) {
			throw new RuntimeException("Error reading csv file: " + requirementsFile);
		}
		return requirements;
	}
}
