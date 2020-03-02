/**
 * 
 */
package userfeedbacknlp.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * @author fitsum
 *
 */
public class Stat {

	static String[] requirementIds = {"4183","4172","4301","4302","4303","4217","4181","4184","4177","4174","4178","4173","4179","4182"};

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		double threshold = 0.1d;
		// threshold as an optional parameter
		if (args.length > 0) {
			threshold = Double.parseDouble(args[0]);
		}
		
		String relatedFeedbackUserFileUnitn = "data/experiment_material/related-feedback-from-users_unitn.csv";
		String relatedFeedbackUserFileHannover = "data/experiment_material/related-feedback-from-users_hannover.csv";
		
		Map<String, Set<Integer>> relatedFeedbackPerRequirementUserUnitn = loadRelatedFeedbackUser (relatedFeedbackUserFileUnitn);
		Map<String, Set<Integer>> relatedFeedbackPerRequirementUserHannover = loadRelatedFeedbackUser (relatedFeedbackUserFileHannover);
		Map<String, Set<Integer>> relatedFeedbackPerRequirementTool = loadRelatedFeedbackTool(threshold);
//		String id = "4184";
//		System.out.println(relatedFeedbackPerRequirementTool.get(id));
//		System.out.println(relatedFeedbackPerRequirementUserUnitn.get(id));
		Stat stat = new Stat();
		System.out.println("Results for Uni. Hannover");
		double[] avgsHannover = stat.computeStats(relatedFeedbackPerRequirementTool, relatedFeedbackPerRequirementUserHannover);
//		System.out.println("HannoverFMeasure: " + avgFMeasureHannover);
		
		System.out.println("=====================================\nResults for Uni. TN");
		double[] avgsUnitn = stat.computeStats(relatedFeedbackPerRequirementTool, relatedFeedbackPerRequirementUserUnitn);
//		System.out.println("UnitnFMeasure: " + avgFMeasureUnitn);
		
		System.out.println("=====================================\nResults for Uni. Hannover vs Uni. TN");
		stat.computeStats(relatedFeedbackPerRequirementUserUnitn, relatedFeedbackPerRequirementUserHannover);
		
		Map<String, Set<Integer>> relatedFeedbackPerRequirementUserIntersection = stat.buildIntersectionSet(relatedFeedbackPerRequirementUserUnitn, relatedFeedbackPerRequirementUserHannover);
		System.out.println("Results for Hannover-UniTN Intersection");
		stat.computeStats(relatedFeedbackPerRequirementTool, relatedFeedbackPerRequirementUserIntersection);
		
		// compute average fmeasure score for different values of threashold
		stat.tuneThreshold(relatedFeedbackPerRequirementUserHannover, relatedFeedbackPerRequirementUserUnitn);
	}
	
	private void tuneThreshold (Map<String, Set<Integer>> relatedFeedbackPerRequirementUserHannover, 
			Map<String, Set<Integer>> relatedFeedbackPerRequirementUserUnitn) throws IOException {
		double delta = 0.05;
		double min = 0d, max = 1d;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("threshold, prec_hannover, rec_hannover, fm_hannover, prec_unitn, rec_unitn, fm_unitn\n");
		for (double threshold = min; threshold <= max+delta; threshold += delta) {
			Map<String, Set<Integer>> relatedFeedbackPerRequirementTool = loadRelatedFeedbackTool(threshold);
			double[] avgsHannover = computeStats(relatedFeedbackPerRequirementTool, relatedFeedbackPerRequirementUserHannover);
			double[] avgsUnitn = computeStats(relatedFeedbackPerRequirementTool, relatedFeedbackPerRequirementUserUnitn);
			buffer.append(threshold + "," + avgsHannover[0] + "," + avgsHannover[1] + "," + avgsHannover[2] + "," + avgsUnitn[0] + "," + avgsUnitn[1] + "," + avgsUnitn[2] + "\n");
		}
		System.out.println(buffer.toString());
	}

	/**
	 * @param relatedFeedbackPerRequirementUserUnitn
	 * @param relatedFeedbackPerRequirementUserHannover
	 * @return
	 */
	private Map<String, Set<Integer>> buildIntersectionSet(
			Map<String, Set<Integer>> relatedFeedbackPerRequirementUser1,
			Map<String, Set<Integer>> relatedFeedbackPerRequirementUser2) {
		Map<String, Set<Integer>> intersection = new HashMap<String, Set<Integer>>();
		for (String reqId : requirementIds) {
			Set<Integer> relatedFeedbacksIntersect = relatedFeedbackPerRequirementUser1.get(reqId);
			Set<Integer> relatedFeedbacksUser2 = relatedFeedbackPerRequirementUser2.get(reqId);
			relatedFeedbacksIntersect.retainAll(relatedFeedbacksUser2);
			intersection.put(reqId, relatedFeedbacksIntersect);
		}
		return intersection;
	}

	private double[] computeStats (Map<String, Set<Integer>> relatedFeedbackPerRequirementTool, 
			Map<String, Set<Integer>> relatedFeedbackPerRequirementUser) {
		double minFMeasure = 2d;
		double totalFMeasure = 0d;
		double totalPrecision = 0d;
		double totalRecall = 0d;
		System.out.println("reqId, siz. tool, siz. user, intersection, jaccardIndex, precision, recall, fmeasure");
		for (String reqId : requirementIds) {
			Set<Integer> relatedFeedbacksTool = relatedFeedbackPerRequirementTool.get(reqId);
			Set<Integer> relatedFeedbacksUser = relatedFeedbackPerRequirementUser.get(reqId);
			
			int intersection = computeSetIntersection(relatedFeedbacksTool, relatedFeedbacksUser);
			double jaccard = computeJaccardIndex(relatedFeedbacksTool, relatedFeedbacksUser);
			
			double precision = computePrecision(relatedFeedbacksTool, relatedFeedbacksUser);
			double recall = computeRcall(relatedFeedbacksTool, relatedFeedbacksUser);
			double fmeasure = computeFMeasure(precision, recall);
			totalFMeasure += (Double.isNaN(fmeasure)? 0 : fmeasure);
			totalPrecision += (Double.isNaN(precision)? 0 : precision);
			totalRecall += (Double.isNaN(recall)? 0 : recall);
			if (fmeasure < minFMeasure) {
				minFMeasure = fmeasure;
			}
			System.out.println(reqId + ", " + relatedFeedbacksTool.size() + ", " + relatedFeedbacksUser.size() + ", " + intersection + " , " + jaccard + " , " + precision + " , " + recall + " , " + fmeasure);
		}
		double avgFM = totalFMeasure / (double) requirementIds.length;
		double avgPrec = totalPrecision / (double) requirementIds.length;
		double avgRec = totalRecall / (double) requirementIds.length;
		double[] ret = {avgPrec, avgRec, avgFM};
		return ret;
	}
	
	/**
	 * @param precision
	 * @param recall
	 * @return
	 */
	private static double computeFMeasure(double precision, double recall) {
		double fscore = 2 * precision * recall / (precision + recall);
		return fscore;
	}

	/**
	 * @param resultSet
	 * @param truthSet
	 * @return
	 */
	private static double computeRcall(final Set<Integer> resultSet, final Set<Integer> truthSet) {
		double recall = 0;
		recall = (double)computeSetIntersection(resultSet, truthSet) / (double)truthSet.size();
		return recall;
	}

	/**
	 * @param resultSet
	 * @param truthSet
	 * @return
	 */
	private static double computePrecision(final Set<Integer> resultSet, final Set<Integer> truthSet) {
		double precision = 0;
		precision = (double)computeSetIntersection(resultSet, truthSet) / (double)resultSet.size();
		return precision;
	}

	/**
	 * @param relatedFeedbacksTool
	 * @param relatedFeedbacksUser
	 * @return
	 */
	private static double computeJaccardIndex(final Set<Integer> set1, final Set<Integer> set2) {
		Set<Integer> union = new HashSet<Integer>();
		union.addAll(set1);
		union.addAll(set2);
		double jaccard = (double)computeSetIntersection(set1, set2) / ((double)union.size());
		return jaccard;
	}

	/**
	 * @param relatedFeedbacksTool
	 * @param relatedFeedbacksUser
	 * @return
	 */
	private static int computeSetIntersection(final Set<Integer> set1, final Set<Integer> set2) {
		Set<Integer> set1Copy = new HashSet<Integer>();
		set1Copy.addAll(set1);
		set1Copy.retainAll(set2);
		return set1Copy.size();
	}

	/**
	 * @return
	 * @throws IOException 
	 */
	private static Map<String, Set<Integer>> loadRelatedFeedbackTool(double threshold) throws IOException {
		Map<String, Set<Integer>> relatedFeedbackPerRequirement = new HashMap<String, Set<Integer>>();
		
		String baseDir = "data/experiment_material";
		IOFileFilter fileFilter = new RegexFileFilter("4.*_result.csv");
		IOFileFilter dirFilter = new RegexFileFilter("set.");
		Collection<File> reqFiles = FileUtils.listFiles(new File(baseDir), fileFilter, dirFilter);
		
		for (File reqFile : reqFiles) {
			Map<String, Set<Integer>> relatedFeedback = loadRelatedFeedbackForRequirement (reqFile, threshold);
			relatedFeedbackPerRequirement.putAll(relatedFeedback);
		}
		
		return relatedFeedbackPerRequirement;
	}

	/**
	 * @param reqFile
	 * @return
	 * @throws IOException 
	 */
	private static Map<String, Set<Integer>> loadRelatedFeedbackForRequirement(File reqFile, double threshold) throws IOException {
		Map<String, Set<Integer>> relatedFeedback = new HashMap<String, Set<Integer>>();
		String reqId = reqFile.getName().substring(0, 4);
		Reader in = new FileReader(reqFile);
		String[] header = {"id","feedback","number_positive","positive_sentiment","number_negative","negative_sentiment","annotation","intention","total_lenght","negative_lengt","severity","similarity_score","total_score"};
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withSkipHeaderRecord().withHeader(header).parse(in);
        Set<Integer> feedIds = new HashSet<Integer>();
        for (CSVRecord record : records) {
        	double simScore = Double.parseDouble(record.get("similarity_score"));
        	if (simScore > threshold) {
        		int feedbackId = Integer.parseInt(record.get("id"));
        		feedIds.add(feedbackId);
        	}
        }
        relatedFeedback.put(reqId, feedIds);
		return relatedFeedback;
	}

	/**
	 * @param relatedFeedbackUserFile
	 * @return
	 * @throws IOException 
	 */
	private static Map<String, Set<Integer>> loadRelatedFeedbackUser(String relatedFeedbackUserFile) throws IOException {
		Map<String, Set<Integer>> relatedFeedbackPerRequirement = new HashMap<String, Set<Integer>>();
		
		Reader in = new FileReader(relatedFeedbackUserFile);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withSkipHeaderRecord().withHeader(requirementIds).parse(in);
        for (CSVRecord record : records) {
//        	if(!record.get("4183").equals("User")){
        		for (String reqId : requirementIds) {
//        			String reqId = header[i];  // e.g., 4183
	        		String req = record.get(reqId);  
	        		if (req == null || req.isEmpty()) {
	        			continue;
	        		}
	        		if (!relatedFeedbackPerRequirement.containsKey(reqId)) {
	        			relatedFeedbackPerRequirement.put(reqId, new HashSet<Integer>());
	        		}
	        		String[] feedbackIds = req.split(";");  // here we're sure there's at least one id
	        		for (String feedbackId : feedbackIds) {
	        			if (feedbackId.trim().isEmpty()) continue;
	        			relatedFeedbackPerRequirement.get(reqId).add(Integer.parseInt(feedbackId.trim()));
	        		}
        		}
//        	}
        }
		return relatedFeedbackPerRequirement;
	}
}
