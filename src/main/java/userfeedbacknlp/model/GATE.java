/**
 * This class creates a complete GATE process 
 * by generating a corpus, the language resources and processing resources
 * Itzel Morales Ram�rez, December 2013
 */
package userfeedbacknlp.model;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import userfeedbacknlp.util.FileManager;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Gate;
import gate.Factory;
import gate.LanguageAnalyser;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.Err;
import gate.util.GateException;
import gate.util.Out;

public class GATE {
	Logger log = Logger.getLogger(GATE.class);
	private SerialAnalyserController pipeline;
	private ProcessingResource reset=null;
	private ProcessingResource tokeniser = null;
	private ProcessingResource splitter = null;
	private ProcessingResource gazetteer1 = null;
	private ProcessingResource gazetteer2 = null;
	private ProcessingResource postagger = null;
	private ProcessingResource morpher = null;
	private LanguageAnalyser jape = null;
	private List<String> annotaConversActs=null;
	private List<String> annotaFilteredConversActs=null;
	private List<String> annotaSentence=null;
	private List<String> annotaSentenceTerms=null;
	//Directory that contains the gazetteers
	//private static String DIR_GAZETTEER= "/Users/itzy_5/Documents/workspace/PrjIntentionExtraction/Caise2014_JAPE_Gaz_NOV-2013/gazetteer/mylists.def";
	private static String DIR_GAZETTEER= "./GATE_filesJuly2017/gazetteer/mylists.def";
	//Directory that contains the JAPE rules
	//private static String DIR_JAPE="/Users/itzy_5/Documents/workspace/PrjIntentionExtraction/Caise2014_JAPE_Gaz_NOV-2013/JAPE/main.jape";
	private static String DIR_JAPE="./GATE_filesJuly2017/JAPE/main.jape";

	/**
	 * Constructor to initialize the corpus pipeline
	 * 
	 */
	public GATE(){
		try {
			pipeline = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
			Out.print("Creating pipeline");
			log.info("Creating pipeline");
		} catch (ResourceInstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}
	}

	/**
	 * Method that registers the plugins needed to process the text
	 *  
	 */
	public void registerResources(){
		try{
			Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "Tools").toURI().toURL()); 
			Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "ANNIE").toURI().toURL());
			Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "OpenNLP").toURI().toURL());
			Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "Stanford_CoreNLP").toURI().toURL());
		}
		catch(GateException gex) {
			Err.prln(gex.getMessage());
			gex.printStackTrace();
			log.error(gex);
			return;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e);
		}

	}

	/**
	 * Method that create the processing resources used in this application
	 * @throws MalformedURLException 
	 * 
	 */
	public void createProcessingResources() throws MalformedURLException
	{
		try{
			reset = (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR");
			tokeniser = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.DefaultTokeniser");
			//tokeniser = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpTokenizer"); //Stanford: gate.stanford.Tokenizer
			
			//splitter = (ProcessingResource) Factory.createResource("gate.creole.splitter.RegexSentenceSplitter");
			splitter = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpSentenceSplit");
			
			gazetteer1 = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer", 
					gate.Utils.featureMap("listsURL", new File(DIR_GAZETTEER).toURI().toURL()));
			gazetteer2 = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer");
			postagger = (ProcessingResource) Factory.createResource("gate.creole.POSTagger"); //Stanford: gate.stanford.Tagger | OpenNLP: gate.opennlp.OpenNlpPOS
			morpher = (ProcessingResource) Factory.createResource("gate.creole.morph.Morph");
			jape = (LanguageAnalyser)Factory.createResource("gate.creole.Transducer", 
					gate.Utils.featureMap("grammarURL",new File(DIR_JAPE).toURI().toURL() ,"encoding", "UTF-8"));
		}
		catch(GateException gex) {
			Err.prln(gex.getMessage());
			gex.printStackTrace();
			log.error(gex);
			return;
		} 

	}

	/**
	 * Method that adds the processing resources to the corpus pipeline
	 * 
	 */
	public void addProcessingResources() throws ResourceInstantiationException
	{
		pipeline.add(reset);
		pipeline.add(tokeniser);
		pipeline.add(splitter);
		pipeline.add(gazetteer1);
		pipeline.add(gazetteer2);
		pipeline.add(postagger);
		pipeline.add(morpher);
		pipeline.add(jape);
	}
	
	/**
	 * Method that sets the corpus to the corpus pipeline
	 * 
	 */
	public void setCorpus(Corpus corpus){
		pipeline.setCorpus(corpus);
	}
	
	/**
	 * Method that executes the corpus
	 * 
	 */
	public void execute() throws ExecutionException{
		pipeline.execute();
	}
	
	/**
	 * Method that adds the annotations for the convers-acts
	 * 
	 */
	/**
	 * @return
	 */
	public List<String> createAnnotConversActs(){
		annotaConversActs = new ArrayList<String>();
		annotaConversActs.add("Sentence");
		annotaConversActs.add("Suppositive");
		annotaConversActs.add("Questions");
		annotaConversActs.add("QuestionMark");
		annotaConversActs.add("Participant");
		annotaConversActs.add("TempSuggestive");
		annotaConversActs.add("TempNSuggestive");
		annotaConversActs.add("TempRequirement");
		annotaConversActs.add("TempSentP");
		annotaConversActs.add("TempSentN");
		annotaConversActs.add("TempNegative");
		annotaConversActs.add("Question");
		annotaConversActs.add("Requestive");
		annotaConversActs.add("Requirement");
		annotaConversActs.add("Informative");
		annotaConversActs.add("Suggestive");
		annotaConversActs.add("Assertive");
		annotaConversActs.add("Concessive");
		annotaConversActs.add("Descriptive");
		annotaConversActs.add("Responsive");
		annotaConversActs.add("Confirmative");
		annotaConversActs.add("Thank");
		annotaConversActs.add("Accept");
		annotaConversActs.add("Reject");
		annotaConversActs.add("NEG_opinion");
		annotaConversActs.add("POS_opinion");
		annotaConversActs.add("URL_link");
		annotaConversActs.add("LOG_FILE");
		annotaConversActs.add("CODE_LINE");
		annotaConversActs.add("Attachment");
		//annotaConversActs.add("NestedSuggestive");
		//annotaConversActs.add("FeatureIndicator");
		//annotaConversActs.add("BugIndicator");
		//annotaConversActs.add("Term");
		annotaConversActs.add("UrlPre");
		annotaConversActs.add("ABV");
		annotaConversActs.add("Others");
		return annotaConversActs;
	}
	
	
	/**
	 * Method that adds the filtered (selected )annotations for the convers-acts
	 * 
	 */
	/**
	 * @return
	 */
	public List<String> createFilteredAnnotConversActs(){
		annotaFilteredConversActs = new ArrayList<String>();
		annotaFilteredConversActs.add("Assertive");
		annotaFilteredConversActs.add("Confirmative");
		annotaFilteredConversActs.add("Concessive");
		annotaFilteredConversActs.add("Descriptive");
		annotaFilteredConversActs.add("Suggestive");
		annotaFilteredConversActs.add("Suppositive");
		annotaFilteredConversActs.add("Responsive");
		annotaFilteredConversActs.add("Requestive");
		annotaFilteredConversActs.add("Question");
		annotaFilteredConversActs.add("Requirement");
		annotaFilteredConversActs.add("Thank");
		annotaFilteredConversActs.add("Accept");
		annotaFilteredConversActs.add("Reject");
		annotaFilteredConversActs.add("NEG_opinion");
		annotaFilteredConversActs.add("POS_opinion");
		annotaFilteredConversActs.add("URL_link");
		annotaFilteredConversActs.add("CODE_LINE");
		annotaFilteredConversActs.add("LOG_FILE");
		annotaFilteredConversActs.add("Informative");
		annotaFilteredConversActs.add("Attachment");
		
		return annotaFilteredConversActs;
	}
	
	
	/**
	 * Method that adds only the annotations of sentences
	 * 
	 */
	public List<String> createAnnotSentence(){
		annotaSentence = new ArrayList<String>();
		annotaSentence.add("Sentence");
		annotaSentence.add("Participant");
		return annotaSentence;
	}
	
	/**
	 * Method that adds only the annotations of sentences and terms
	 * 
	 */
	public List<String> createAnnotSentenceTerms(){
		annotaSentenceTerms = new ArrayList<String>();
		annotaSentenceTerms.add("Sentence");
		annotaSentenceTerms.add("Term");
		annotaSentenceTerms.add("Participant");
		return annotaSentenceTerms;
	}
	
	/**
	 * Method that apply the annotations to a document
	 * 
	 */
	public String writeAnnotations(Document doc, List<String> annotations){
		Set<Annotation> annotationsToWrite = new HashSet<Annotation>();
		AnnotationSet defaultAnnots = doc.getAnnotations();
		Iterator<String> annotTypes = annotations.iterator();
		String docXMLString = null;
		while(annotTypes.hasNext()) {	
			// extract all the annotations of each requested type and add them to
			// the temporary set
			AnnotationSet annotsOfThisType = defaultAnnots.get((String)annotTypes.next());
			if(annotsOfThisType != null) {
				annotationsToWrite.addAll(annotsOfThisType);
			}
		}
		// create the XML string using these annotations
		docXMLString = doc.toXml(annotationsToWrite);
		return docXMLString;
	}
	
	public AnnotationSet annotationRequired(Document doc, List<String> annotations){
		AnnotationSet defaultAnnots = doc.getAnnotations();
		AnnotationSet annotsOfThisType=null;
		Iterator<String> annotTypes = annotations.iterator();
		//String docXMLString = null;
		while(annotTypes.hasNext()) {	
			// extract all the annotations of each requested type and add them to
			// the temporary set
			annotsOfThisType = defaultAnnots.get((String)annotTypes.next());
		}
		return annotsOfThisType;
	}
	
	public String filterAnnotations(String content){
		this.createFilteredAnnotConversActs(); // it should be derived to the constructor
		
		String sentences="";
		org.w3c.dom.Document document = FileManager.convertStringToDocumentXML(content);    
		NodeList nList = document.getElementsByTagName("Sentence");
		Node n = null;
		
		sentences+="<Participant name=\"\">";
		for (int i = 0; i < nList.getLength(); i++) {               
			n= (Element) nList.item(i);                            
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) n;

				sentences += "<Sentence id=\"" + eElement.getAttribute("gate:gateId") + "\">";
				boolean flag=false;
				Map<String,Integer> freqConversAct = new HashMap<String,Integer>();
				 
				for(String act: annotaFilteredConversActs ){
					NodeList nList1 = eElement.getElementsByTagName(act);
					Node n1 = null;
					for (int i1 = 0; i1 < nList1.getLength(); i1++) {               
						n1 = (Element) nList1.item(i1);  
						if (n1.getNodeType() == Node.ELEMENT_NODE) {
							if(freqConversAct.containsKey(act)){
								freqConversAct.put(act, freqConversAct.get(act) +1);// increase the frequency
							}
							else{
								freqConversAct.put(act, 1); //initialize the frequency of the speech act 
							}
							flag = true;
						}
					}
				}
				if(flag == true){

					//DMA: review if it is needed
					//freqConversAct = filterConversActs(freqConversAct);
					for(String act: freqConversAct.keySet()){
						if(eElement.getTextContent().trim().length() > 2){
							sentences += "<" + act + " freq=\"" + freqConversAct.get(act) + "\">" + eElement.getTextContent().trim().replaceAll("\n", " ") + "</"+ act +">"; //here throws null pointer exception after printing staff1 tag
						}
					}
				}else{
					if(eElement.getTextContent().trim().length() > 2){
						sentences += "<Informative freq=\"1\">" + eElement.getTextContent().trim().replaceAll("\n", " ") + "</Informative>"; //in case no other speech act is found
					}
				}
				sentences += "</Sentence>";
			}
		}
		sentences +="</Participant>";
		
		return sentences;
	}
	
}



