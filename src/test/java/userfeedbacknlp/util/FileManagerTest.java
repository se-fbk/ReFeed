/**
 * 
 */
package userfeedbacknlp.util;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.supersede.feedbackanalysis.ds.UserFeedback;
import junit.framework.TestCase;
import userfeedbacknlp.data.Feedback;
import userfeedbacknlp.data.Requirement;

/**
 * @author fitsum
 *
 */
public class FileManagerTest extends TestCase {

	/**
	 * Test method for {@link userfeedbacknlp.util.FileManager#readFeedbackPerRequirementFromCVSFile(java.lang.String)}.
	 * @throws IOException 
	 */
	public void testReadFeedbackPerRequirementFromCVSFile() throws IOException {
		String csvFile = "sample_feedback_oo.csv";
		Map<Requirement, Set<Feedback>> reqFeedback = FileManager.readFeedbackPerRequirementFromCVSFile(csvFile);
		for (Entry<Requirement, Set<Feedback>> entry : reqFeedback.entrySet()) {
			System.out.println(entry.getKey().getId() + "[ " + entry.getValue().size() + " ]");
			for (Feedback fb : entry.getValue()) {
				System.err.println(fb.getDescription());
			}
		}
	}

}
