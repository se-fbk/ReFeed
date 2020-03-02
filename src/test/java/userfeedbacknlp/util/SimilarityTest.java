/**
 * 
 */
package userfeedbacknlp.util;

import java.util.Map;

import eu.supersede.feedbackanalysis.ds.UserFeedback;
import junit.framework.TestCase;
import userfeedbacknlp.data.Requirement;

/**
 * @author fitsum
 *
 */
public class SimilarityTest extends TestCase {
	
	public void testLoadRequirements() {
		Similarity similarity = new Similarity();
		String requirementsFile = "requirements.csv";
		String goldStandardFile = "sample_feedback_oo.csv";
		Map<Requirement, Map<UserFeedback, Double>> computeSimilarFeedback = similarity.computeSimilarFeedback(requirementsFile, goldStandardFile);
	}
}
