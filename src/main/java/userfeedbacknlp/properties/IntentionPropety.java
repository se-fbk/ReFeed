package userfeedbacknlp.properties;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.util.Err;
import gate.util.GateException;
import gate.util.Out;
import userfeedbacknlp.data.Annotation;
import userfeedbacknlp.data.Property;
import userfeedbacknlp.model.GATE;
import userfeedbacknlp.util.Preprocessing;
import userfeedbacknlp.util.FileManager;

public class IntentionPropety extends Property{

	//Attributes
	private GATE gateProcessing ;
	private Corpus corpus;
	private String sourceFolder="";
	private String fileName = "";
	private List<Annotation> lstAnnotations;
	private double valueIntention;
	private String annotations;

	//Properties get
	public double getValueIntention() {
		return valueIntention;
	}

	public String getAnnotations() {
		return annotations;
	}

	public IntentionPropety(String strLine, String idFeedbak, GATE gateProcessing, Corpus corpus){
		
		// Initialize plugins of GATE
		//System.setProperty("gate.home", "/home/denisse/GATE_Developer_8.4.1/");
		//System.setProperty("gate.plugins.home", "/home/denisse/GATE_Developer_8.4.1/plugins");
		//Gate.init();
		//Out.prln("GATE initialised...");
		
		// set folders
		sourceFolder = "./data/annotation/source/";
		fileName = "file_" + idFeedbak + ".txt";
		//intentionFolder =  "./data/annotation/intentions/";
		
		// Prepare the resources, processes and languages
		//gateProcessing = new GATE();
		//gateProcessing.registerResources();
		//gateProcessing.createProcessingResources();
		//gateProcessing.addProcessingResources();
	
		// Create the corpus to process the documents
		//corpus = Factory.newCorpus("corpus"); 
		//gateProcessing.setCorpus(corpus);
		
		this.gateProcessing = gateProcessing;
		this.corpus = corpus;

		// Setting the source text after pre-processing
		this.sourceText = Preprocessing.preProcessingText(strLine);
		sourceText = sourceText.replaceAll(" \\?", "? ");
		sourceText = sourceText.replaceAll("  \\?", "? ");
		sourceText = sourceText.replaceAll("  ", " ");
		sourceText = sourceText.replaceAll(" \\. ", "\\. ");
		sourceText = sourceText.replaceAll("\n", " ");
		sourceText = sourceText.replaceAll("\t", " ");
		
		// Initialize the values
		this.values = new HashMap<String, Map<String, Double>>();
		
		// Create the list of annotations <name, priority, weight>
		/*lstAnnotations = new ArrayList<Annotation>();
		lstAnnotations.add( new Annotation("Attachment", 1.1, 1.0));
		lstAnnotations.add( new Annotation("CODE_LINE", 1.2, 0.94));
		lstAnnotations.add( new Annotation("LOG_FILE", 1.3, 0.88));
		lstAnnotations.add( new Annotation("URL_link", 1.4, 0.82));

		lstAnnotations.add( new Annotation("Responsive", 2.1, 0.76));
		lstAnnotations.add( new Annotation("Suggestive", 2.2, 0.70));
		lstAnnotations.add( new Annotation("Confirmative", 2.3, 0.64));
		lstAnnotations.add( new Annotation("Suppositive", 2.4, 0.58));
		
		lstAnnotations.add( new Annotation("Requirement", 3.1, 0.52));
		lstAnnotations.add( new Annotation("Question", 3.2, 0.46));
		lstAnnotations.add( new Annotation("Requestive", 3.3, 0.40));

		lstAnnotations.add( new Annotation("Assertive", 4.1, 0.34));
		lstAnnotations.add( new Annotation("Concessive", 4.2, 0.28));
		lstAnnotations.add( new Annotation("Descriptive", 4.3, 0.22));

		lstAnnotations.add( new Annotation("Thank", 5.1, 0.16));
		lstAnnotations.add( new Annotation("Accept", 5.2, 0.10));
		lstAnnotations.add( new Annotation("Reject", 5.2, 0.10));
		lstAnnotations.add( new Annotation("NEG_opinion", 5.3, 0.04));
		lstAnnotations.add( new Annotation("POS_opinion", 5.3, 0.04));
		
		lstAnnotations.add( new Annotation("Informative", 6.0, 0.00));*/
		
		lstAnnotations = new ArrayList<Annotation>();

		//lstAnnotations.add( new Annotation("Thank", 1.1, 0.18));
		//lstAnnotations.add( new Annotation("Accept", 1.2, 0.16));
		//lstAnnotations.add( new Annotation("Reject", 1.2, 0.16));
		
		lstAnnotations.add( new Annotation("Responsive", 2.1, 0.10)); //responsive 2.1 (0.76)
		lstAnnotations.add( new Annotation("Question", 2.2, 0.13)); //question 3.2 (0.46)
		lstAnnotations.add( new Annotation("URL_link", 2.3, 0.16)); //urllink 1.4 (0.82)
		lstAnnotations.add( new Annotation("Descriptive", 2.3, 0.16)); //descriptive 4.3 (0.22)

		lstAnnotations.add( new Annotation("LOG_FILE", 4.1, 0.70)); //logfile 1.3 (0.88)
		lstAnnotations.add( new Annotation("Confirmative", 4.2, 0.73)); //confirmative 2.3 (0.64)
		lstAnnotations.add( new Annotation("Suppositive", 4.3, 0.76)); //suppositive 2.4 (0.58)
		lstAnnotations.add( new Annotation("Assertive", 4.3, 0.76)); //assertive 4.1 (0.34)
		lstAnnotations.add( new Annotation("Concessive", 4.3, 0.76)); //concessive 4.3 (0.34)
		lstAnnotations.add( new Annotation("Suggestive", 4.5, 0.82)); //suggestive 2.2 (0.70)			
		lstAnnotations.add( new Annotation("Requestive", 4.9, 0.94)); //requestive 3.3 (0.40)

		lstAnnotations.add( new Annotation("Attachment", 5.0, 0.97)); // attachment 1.1 (1.0)
		lstAnnotations.add( new Annotation("CODE_LINE", 5.1, 1.0)); // code 1.2 (0.94)
		lstAnnotations.add( new Annotation("Requirement", 5.1, 1.0)); //requirement 3.1 (0.52)
		
		//lstAnnotations.add( new Annotation("NEG_opinion", 1.3, 0.14));
		//lstAnnotations.add( new Annotation("POS_opinion", 1.3, 0.14));
		
		//lstAnnotations.add( new Annotation("Informative", 6.0, 0.00));
	}
	
	@Override
	public void calculateValues() {
		String docXMLConversActs="";
		String encoding = "UTF-8";
		
		try{
			// Create a file for the source text of feedback 
			FileManager.writeFile(sourceFolder, fileName, sourceText);
			File file = new File(sourceFolder + fileName);
			
			//Read the file for each feedback
			Document doc = Factory.newDocument(file.toURI().toURL(), encoding);

			// put the document in the corpus
			corpus.add(doc);

			// run the application
			gateProcessing.execute();

			// remove the document from the corpus again
			corpus.clear();

			// Create annotations including the temporal annotations
			docXMLConversActs = gateProcessing.writeAnnotations(doc,gateProcessing.createAnnotConversActs()) ;

			// Filter the annotations and add frequencies
			docXMLConversActs = gateProcessing.filterAnnotations(docXMLConversActs);
			
			Factory.deleteResource(doc);
			
			
			//Print the result
			System.out.println(docXMLConversActs);
			this.annotations = docXMLConversActs;
			
			// Fill the structure of the Property
			org.w3c.dom.Document docToDerive = FileManager.convertStringToDocumentXML(docXMLConversActs);
			NodeList nList = docToDerive.getElementsByTagName("Sentence");
			for (int i=0; i<nList.getLength(); i++){
				Node sentence = (Node) nList.item(i); 
				if(sentence.getNodeType() == Node.ELEMENT_NODE){
					NodeList actLst = sentence.getChildNodes();
					if(actLst.getLength() > 0){
						Map<String, Double> obj = new HashMap<String, Double>();
						Node act = null; //(Element) actLst.item(0); //first speech act
						String sent = "";
						//obj.put(act.getNodeName(), Double.valueOf(act.getAttributes().getNamedItem("freq").getTextContent()));
						
						for(int j=0; j<actLst.getLength(); j++){
							act = (Node) actLst.item(j); 
							if(act.getNodeType() == Node.ELEMENT_NODE){
								obj.put(act.getNodeName(), Double.valueOf(act.getAttributes().getNamedItem("freq").getTextContent()));
								sent = act.getTextContent().trim();
							}
						}
						
						//the last "act" contains the same sentence for the previous ones
						this.values.put(sent, obj);
					}
				}// end the if
			}
			
			// Calculate the aggregate values for the feedback
			// agreg = (weight * freq) / total freq
			int totalAnnotations = 0;
			valueIntention = 0;
			for(String key: values.keySet()){
				System.out.println("Sentence: " + key + "\n length: " + key.length() );
							
				for(String keyAnnotator: values.get(key).keySet()){
					for(Annotation anObj: lstAnnotations){
						if (keyAnnotator.equals(anObj.getName())){
							System.out.println("Annotation: " + keyAnnotator + " Weight: " + anObj.getWeight() + " Frequency: " + values.get(key).get(keyAnnotator) );
							valueIntention += anObj.getWeight(); // * values.get(key).get(keyAnnotator);
							totalAnnotations ++; //+= values.get(key).get(keyAnnotator);
							break;
						}
					}
				}
			}
			valueIntention = ((totalAnnotations!=0)? valueIntention / totalAnnotations : 0);
			System.out.println("Value Intention: " + valueIntention);
			
		}
		catch (MalformedURLException ex){
			ex.printStackTrace();
			return;
		}
		catch(GateException gex) {
			Err.prln(gex.getMessage());
			gex.printStackTrace();
			return;
		}
	}
	

}
