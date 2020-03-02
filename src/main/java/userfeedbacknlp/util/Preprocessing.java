package userfeedbacknlp.util;

import edu.stanford.nlp.util.StringUtils;

public class Preprocessing {

	// Static Methods
	public static String preProcessingText(String strLine){
			String lines = "";
			
			int length = strLine.split(" ").length;
			if (length>0){
strLine=strLine.replaceAll("&lt;[-a-zA-Z0-9_]*.at.[-a-zA-Z0-9_]*\\.[-a-zA-Z]*&gt;", "&gt;");
				
				if(strLine.trim().compareTo("")!=0)
				{
					if (strLine.split(" ")[0].matches("\\d{4}/\\d{1,2}/\\d{1,2}"))
					{
						String temp="";
						if(strLine.contains("&gt;"))
						{
							temp=strLine.substring(0,strLine.indexOf("&gt;"));
							strLine=strLine.replace(temp, "");
						}
					}



					strLine=strLine.replaceAll("\\?\\s\\d{2,4}/\\d{1,2}/\\d{1,2}", "DATE ");

					strLine=strLine.replaceAll("Am(.+?)\\d{2,2}.\\d{2,2}.\\d{4,4}", "DATE ");
					strLine=strLine.replaceAll("DATE(.+?)schrieb","");

					strLine=strLine.replaceAll("\\s((\\w)*\\s){2}(\\?\\?:){1}", " DATE1");
					strLine=strLine.replaceAll("DATE(.+?)DATE1","");

					strLine=strLine.replaceAll("\\d{2,4}/\\d{1,2}/\\d{1,2}\\s((\\w)*\\s){2}", "DATE ");
					strLine=strLine.replaceAll("DATE(.*?)&gt;","");




					strLine=strLine.replaceAll("On.*?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Oct|Sep|Nov|Dec).*?\\d{1,2}.*?\\d{4}","DATE ");
					strLine=strLine.replaceAll("On.*?\\d{1,2}.*?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Oct|Sep|Nov|Dec).*?\\d{4}","DATE ");


					strLine=strLine.replaceAll("On.*?(Mon|Tue|Wed|Thu|Fri|Sat|Sun).*?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Oct|Sep|Nov|Dec).*?\\d{1,2}","DATE ");


					strLine=strLine.replaceAll("(Mon|Tue|Wed|Thu|Fri|Sat|Sun).*?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Oct|Sep|Nov|Dec).*?\\d{1,2}","DATE ");

					//French date format
					strLine=strLine.replaceAll("Le.*?(Lundi|Mardi|Mercredi|Jeudi|Vendredi|Samedi|Dimanche).*?\\d{2}.*?(Janvier|F�vrier|F\\?vrier|Mars|Avril|Mai|Juin|Juillet|Ao�t|Aout|Ao\\?t|Octobre|Septembre|Novembre|D�cembre|D\\?cembre).*?\\d{4}", "DATE ");
					strLine=strLine.replaceAll("Le.*?(Lundi|Mardi|Mercredi|Jeudi|Vendredi|Samedi|Dimanche).*?\\d{2}.*?(Jan|F�v|F\\?v|Mar|Avr|Mai|Juin|Juil|Ao�t|Aout|Ao\\?t|Oct|Sep|Nov|D�c|D\\?c).*?\\d{4}", "DATE ");
					strLine=strLine.replaceAll("Le.*?(lundi|mardi|mercredi|jeudi|vendredi|samedi|dimanche).*?\\d{2}.*?(Jan|F�v|F\\?v|Mar|Avr|Mai|Juin|Juil|Ao�t|Aout|Ao\\?t|Oct|Sep|Nov|D�c|D\\?c).*?\\d{4}", "DATE ");
					strLine=strLine.replaceAll("Le.*?(Lun|Mar|Mer|Jeu|Ven|Sam|Dim).*?\\d{2}.*?(Jan|F�v|F\\?v|Mar|Avr|Mai|Juin|Juil|Ao�t|Aout|Ao\\?t|Oct|Sep|Nov|D�c|D\\?c).*?\\d{4}", "DATE ");
					strLine=strLine.replaceAll("DATE(.+?)crit\\?", "");
					strLine=strLine.replaceAll("DATE(.+?)crit :", "");
					strLine=strLine.replaceAll("crit\\?", "");
					strLine=strLine.replaceAll("crit :", "");
					strLine=strLine.replaceAll("\\d{2}.\\?\\?\\?\\?\\?\\?\\?.\\d{4},.\\d{2}:\\d{2}.\\?\\?(.*?)&gt;$", "");
					strLine=strLine.replaceAll("\\d{2}.\\?\\?\\?\\?\\?\\?.\\d{4},.\\d{2}:\\d{2}.\\?\\?(.*?)&gt;$", "");
					strLine=strLine.replaceAll("\\d{2}.\\?\\?\\?\\?\\?\\?\\?.\\d{4},.\\d{2}:\\d{2}.\\?\\?", "");
					strLine=strLine.replaceAll("\\d{2}.\\?\\?\\?\\?\\?\\?.\\d{4},.\\d{2}:\\d{2}.\\?\\?", "");
					//14 ??????? 2012, 16:11 ??
					//27?????? 2012, 13:46??
					//French date format


					strLine=strLine.replaceAll("On.*?\\d{1,2}/\\d{1,2}/\\d{2,4}", "DATE ");
					strLine=strLine.replaceAll("DATE(.+?)wrote:","");
					strLine=strLine.replaceAll("DATE(.+?)(AM|PM),","");
					strLine=strLine.replaceAll("DATE.at.\\d{2}:\\d{2}.(.*?)wrote:","");
					strLine=strLine.replaceAll("DATE.at.\\d{2}:\\d{2}.(.*?)&lt;","");
					strLine=strLine.replaceAll("DATE.at.\\d{2}:\\d{2},","");
					strLine=strLine.replaceAll("DATE(.+?)\\d{2}:\\d{2},","");
					
					if(strLine.trim().compareTo("")!=0)
						if(strLine.split(" ")[0].matches("\\d{2,4}/\\d{1,2}/\\d{1,2}"))
							strLine="";

					//Commented on Macr 20 2015
					if(strLine.split(" ")[0].compareTo("Subject:")==0 ||strLine.split(" ")[0].compareTo("To:")==0 ||strLine.split(" ")[0].compareTo("Sent:")==0 || strLine.split(" ")[0].compareTo("From:")==0 || strLine.split(" ")[0].compareTo("@")==0 || strLine.split(" ")[0].compareTo("skype:")==0 || strLine.split(" ")[0].compareTo("Skype:")==0 || strLine.split(" ")[0].compareTo("gpgp-fp:")==0 || strLine.split(" ")[0].compareTo("gpgp-key")==0 || strLine.split(" ")[0].compareTo("Name:")==0||strLine.split(" ")[0].compareTo("Type:")==0 || strLine.split(" ")[0].compareTo("Size:")==0||strLine.split(" ")[0].compareTo("Desc:")==0)
						if(strLine.trim().compareTo("")!=0){
							if(strLine.split(" ")[0].compareTo("To:")==0 ||strLine.split(" ")[0].compareTo("Sent:")==0 || strLine.split(" ")[0].compareTo("From:")==0 || strLine.split(" ")[0].compareTo("@")==0 || strLine.split(" ")[0].compareTo("skype:")==0 || strLine.split(" ")[0].compareTo("Skype:")==0 || strLine.split(" ")[0].compareTo("gpgp-fp:")==0 || strLine.split(" ")[0].compareTo("gpgp-key")==0 || strLine.split(" ")[0].compareTo("Name:")==0||strLine.split(" ")[0].compareTo("Type:")==0 || strLine.split(" ")[0].compareTo("Size:")==0||strLine.split(" ")[0].compareTo("Desc:")==0)
							{
								strLine="";
							}
						}

					if(strLine.trim().split(" ").length==1)
					{
						if(strLine.compareTo("at")==0 || strLine.compareTo("\\d{4}") ==0 || strLine.compareTo("\\d{1}") ==0)
							strLine="";
					}


					//Commented on August 18 2014, for comparing with gold standard
					strLine=strLine.replaceAll("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]","http://www.");
					strLine=strLine.replaceAll("www\\.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]","http://www.");
					
					strLine=strLine.replaceAll("\\[http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]","log file");

					strLine=strLine.replaceAll("exception trace","log file");
					strLine=strLine.replaceAll("error stack","log file");
					strLine=strLine.replaceAll("stacktrace","log file");
					strLine=strLine.replaceAll("trace"," log file");
					strLine=strLine.replaceAll("stacklog","log file");
					strLine=strLine.replaceAll("Wrapped Exception:","log file");



					strLine=strLine.replaceAll("[a-z0-9_]*\\.[A-Z0-9_]*\\sas\\s"," CODELINE ");
					strLine=strLine.replaceAll("[a-z0-9_]*\\.[A-Z0-9_]*="," CODELINE ");
					strLine=strLine.replaceAll("\\$\\{[-a-zA-Z0-9_.]*\\}"," CODELINE ");
					
				

					strLine=strLine.replaceAll("[a-z0-9_]*\\.[a-z0-9_]*\\sas\\s"," CODELINE ");
					strLine=strLine.replaceAll("[a-z0-9_]*\\.[a-z0-9_]*="," CODELINE ");


					strLine=strLine.replaceAll("[a-z0-9_]*\\.[A-Z0-9_]*\\sCODELINE"," CODELINE ");
					strLine=strLine.replaceAll("[a-z0-9_]*\\.[a-z0-9_]*\\sCODELINE"," CODELINE ");
					strLine=strLine.replaceAll("CODE_LINE"," CODELINE ");

					strLine=strLine.replaceAll("&lt;"," STARTCODE ");
					strLine=strLine.replaceAll("STARTCODE(.+?)&gt;"," CODELINE ");

					strLine=strLine.replaceAll("CODELINE(.*?)wrote:","");


					if(strLine.trim().compareTo("")!=0)
						if(strLine.split(" ")[0].compareTo("&lt;div")==0||strLine.split(" ")[0].compareTo("&lt;%")==0||strLine.split(" ")[0].startsWith("#")||strLine.split(" ")[0].startsWith("$"))
						{
							strLine.split(" ")[0]=" CODELINE ";
							//lines+=strLine;
							//lines+= "\n";
						}
					strLine=strLine.replaceAll("STARTCODE","");
					strLine=strLine.replaceAll("DeLisle","");
					

					strLine=strLine.replaceAll("&gt;&lt;","");
					strLine=strLine.replaceAll("e\\.g\\.","example");
					strLine=strLine.replaceAll("e\\.g","example");
					strLine=strLine.replaceAll("e\\. g\\.","example");
					strLine=strLine.replaceAll("E\\.g\\.", "For example");
					strLine=strLine.replaceAll("gt;","");
					strLine=strLine.replaceAll("gt;:","");

					strLine=StringUtils.capitalize(strLine);
					//SPELL check
					strLine=strLine.replaceAll("can\\?t", "can not");
					strLine=strLine.replaceAll("Can\\?t", "can not");
					strLine=strLine.replaceAll("can't", "can not");
					strLine=strLine.replaceAll("Can't", "can not");
					strLine=strLine.replaceAll("can`t", "can not");
					strLine=strLine.replaceAll("Can`t", "can not");
					strLine=strLine.replaceAll("can´not", "can not");
					strLine=strLine.replaceAll("Can´not", "can not");
					strLine=strLine.replaceAll("cannot", "can not");
					strLine=strLine.replaceAll("Cannot", "can not");
					
					strLine=strLine.replaceAll("couldn\\?t", "could not");
					strLine=strLine.replaceAll("Couldn\\?t", "Could not");
					strLine=strLine.replaceAll("couldn't", "could not");
					strLine=strLine.replaceAll("Couldn't", "Could not");
					strLine=strLine.replaceAll("couldn`t", "could not");
					strLine=strLine.replaceAll("Couldn`t", "Could not");
					strLine=strLine.replaceAll("couldnt", "could not");
					strLine=strLine.replaceAll("Couldnt", "Could not");
					strLine=strLine.replaceAll("couldn´t", "could not");
					strLine=strLine.replaceAll("Couldn´t", "could not");
					
					strLine=strLine.replaceAll("shouldn\\?t", "should not");
					strLine=strLine.replaceAll("Shouldn\\?t", "Should not");
					strLine=strLine.replaceAll("shouldn't", "should not");
					strLine=strLine.replaceAll("Shouldn't", "Should not");
					strLine=strLine.replaceAll("shouldn`t", "should not");
					strLine=strLine.replaceAll("Shouldn`t", "Should not");
					strLine=strLine.replaceAll("shouldnt", "should not");
					strLine=strLine.replaceAll("Shouldnt", "Should not");
					strLine=strLine.replaceAll("shouldn´t", "should not");
					strLine=strLine.replaceAll("Shouldn´t", "Should not");
					
					strLine=strLine.replaceAll("wouldn\\?t", "would not");
					strLine=strLine.replaceAll("Wouldn\\?t", "Would not");
					strLine=strLine.replaceAll("wouldn't", "would not");
					strLine=strLine.replaceAll("Wouldn't", "Would not");
					strLine=strLine.replaceAll("wouldn`t", "would not");
					strLine=strLine.replaceAll("Wouldn`t", "Would not");
					strLine=strLine.replaceAll("wouldnt", "would not");
					strLine=strLine.replaceAll("Wouldnt", "Would not");
					strLine=strLine.replaceAll("wouldn´t", "would not");
					strLine=strLine.replaceAll("Wouldn´t", "Would not");
					
					strLine=strLine.replaceAll("wasn\\?t", "was not");
					strLine=strLine.replaceAll("Wasn\\?t", "Was not");
					strLine=strLine.replaceAll("wasn't", "was not");
					strLine=strLine.replaceAll("Wasn't", "Was not");
					strLine=strLine.replaceAll("wasn`t", "was not");
					strLine=strLine.replaceAll("Wasn`t", "Was not");
					strLine=strLine.replaceAll("wasnt", "was not");
					strLine=strLine.replaceAll("Wasnt", "Was not");
					strLine=strLine.replaceAll("wasn´t", "was not");
					strLine=strLine.replaceAll("Wasn´t", "Was not");
					
					strLine=strLine.replaceAll("weren\\?t", "were not");
					strLine=strLine.replaceAll("Weren\\?t", "Were not");
					strLine=strLine.replaceAll("weren't", "were not");
					strLine=strLine.replaceAll("Weren't", "Were not");
					strLine=strLine.replaceAll("weren`t", "were not");
					strLine=strLine.replaceAll("Weren`t", "Were not");
					strLine=strLine.replaceAll(" werent ", " were not ");
					strLine=strLine.replaceAll(" Werent ", " Were not ");
					strLine=strLine.replaceAll("weren´t", "were not");
					strLine=strLine.replaceAll("Weren´t", "Were not");
					
					strLine=strLine.replaceAll("aren\\?t", "are not");
					strLine=strLine.replaceAll("Aren\\?t", "Are not");
					strLine=strLine.replaceAll("aren't", "are not");
					strLine=strLine.replaceAll("Aren't", "Are not");
					strLine=strLine.replaceAll("aren`t", "are not");
					strLine=strLine.replaceAll("Aren`t", "Are not");
					strLine=strLine.replaceAll(" arent ", " are not ");
					strLine=strLine.replaceAll(" Arent ", " Are not ");
					strLine=strLine.replaceAll("aren´t", "are not");
					strLine=strLine.replaceAll("Aren´t", "Are not");
					
					strLine=strLine.replaceAll("isn\\?t", "is not");
					strLine=strLine.replaceAll("Isn\\?t", "Is not");
					strLine=strLine.replaceAll("isn't", "is not");
					strLine=strLine.replaceAll("Isn't", "Is not");
					strLine=strLine.replaceAll("isn`t", "is not");
					strLine=strLine.replaceAll("Isn`t", "Is not");
					strLine=strLine.replaceAll(" isnt ", " is not ");
					strLine=strLine.replaceAll(" Isnt ", " Is not ");
					strLine=strLine.replaceAll("isn´t", "is not");
					strLine=strLine.replaceAll("Isn´t", "Is not");
					
					
					strLine=strLine.replaceAll("i\\?d", "I would");
					strLine=strLine.replaceAll("I\\?d", "I would");
					strLine=strLine.replaceAll("i'd", "I would");
					strLine=strLine.replaceAll("i`d", "I would");
					strLine=strLine.replaceAll("I'd", "I would");
					strLine=strLine.replaceAll("I`d", "I would");
					strLine=strLine.replaceAll("i´d", "I would");
					strLine=strLine.replaceAll("I´d", "I would");
					
					strLine=strLine.replaceAll("she\\?d", "she would");
					strLine=strLine.replaceAll("She\\?d", "She would");
					strLine=strLine.replaceAll("she'd", "she would");
					strLine=strLine.replaceAll("she`d", "she would");
					strLine=strLine.replaceAll("She'd", "She would");
					strLine=strLine.replaceAll("She`d", "She would");
					strLine=strLine.replaceAll("she´d", "she would");
					strLine=strLine.replaceAll("She´d", "She would");
					
					strLine=strLine.replaceAll("he\\?d", "he would");
					strLine=strLine.replaceAll("He\\?d", "He would");
					strLine=strLine.replaceAll("he'd", "he would");
					strLine=strLine.replaceAll("he`d", "he would");
					strLine=strLine.replaceAll("He'd", "He would");
					strLine=strLine.replaceAll("He`d", "He would");
					strLine=strLine.replaceAll("he´d", "he would");
					strLine=strLine.replaceAll("He´d", "He would");
					
					strLine=strLine.replaceAll("it\\?d", "it would");
					strLine=strLine.replaceAll("It\\?d", "It would");
					strLine=strLine.replaceAll("it'd", "it would");
					strLine=strLine.replaceAll("it`d", "it would");
					strLine=strLine.replaceAll("It'd", "It would");
					strLine=strLine.replaceAll("It`d", "It would");
					strLine=strLine.replaceAll("it´d", "it would");
					strLine=strLine.replaceAll("It´d", "It would");
					
					strLine=strLine.replaceAll("you\\?d", "you would");
					strLine=strLine.replaceAll("You\\?d", "You would");
					strLine=strLine.replaceAll("you'd", "you would");
					strLine=strLine.replaceAll("You'd", "You would");
					strLine=strLine.replaceAll("you`d", "you would");
					strLine=strLine.replaceAll("You`d", "You would");
					strLine=strLine.replaceAll("youd", "you would");
					strLine=strLine.replaceAll("Youd", "You would");
					strLine=strLine.replaceAll("you´d", "you would");
					strLine=strLine.replaceAll("You´d", "You would");
					
					strLine=strLine.replaceAll("what\\?s", "what is");
					strLine=strLine.replaceAll("What\\?s", "What is");
					strLine=strLine.replaceAll("what's", "what is");
					strLine=strLine.replaceAll("What's", "What is");
					strLine=strLine.replaceAll("what`s", "what is");
					strLine=strLine.replaceAll("What`s", "What is");
					strLine=strLine.replaceAll("whats", "what is");
					strLine=strLine.replaceAll("Whats", "What is");
					strLine=strLine.replaceAll("what´s", "what is");
					strLine=strLine.replaceAll("What´s", "What is");
					strLine=strLine.replaceAll("what s ", "what is");
					strLine=strLine.replaceAll("What s ", "What is");
					
					strLine=strLine.replaceAll("you\\?re", "you are");
					strLine=strLine.replaceAll("You\\?re", "You are");
					strLine=strLine.replaceAll("you're", "you are");
					strLine=strLine.replaceAll("You're", "You are");
					strLine=strLine.replaceAll("you`re", "you are");
					strLine=strLine.replaceAll("You`re", "You are");
					strLine=strLine.replaceAll(" youre ", " you are ");
					strLine=strLine.replaceAll(" Youre ", " You are ");
					strLine=strLine.replaceAll("you´re", "you are");
					strLine=strLine.replaceAll("You´re", "You are");
					strLine=strLine.replaceAll("you re ", "you are");
					strLine=strLine.replaceAll("You re ", "You are");
					
					strLine=strLine.replaceAll("it\\?s", "it is");
					strLine=strLine.replaceAll("It\\?s", "It is");
					strLine=strLine.replaceAll("it's", "it is");
					strLine=strLine.replaceAll("It's", "It is");
					strLine=strLine.replaceAll("it`s", "it is");
					strLine=strLine.replaceAll("It`s", "It is");
					strLine=strLine.replaceAll("it s ", "it is");
					strLine=strLine.replaceAll("It s ", "It is");
					strLine=strLine.replaceAll("it´s", "it is");
					strLine=strLine.replaceAll("It´s", "It is");
					
				
					
					strLine=strLine.replaceAll("i\\?m", "I am");
					strLine=strLine.replaceAll("I\\?m", "I am");
					strLine=strLine.replaceAll("i'm", "I am");
					strLine=strLine.replaceAll("I'm", "I am");
					strLine=strLine.replaceAll("i`m", "I am");
					strLine=strLine.replaceAll("I`m", "I am");
					strLine=strLine.replaceAll("im ", "I am");
					strLine=strLine.replaceAll("Im ", "I am");
					strLine=strLine.replaceAll("i´m ", "I am");
					strLine=strLine.replaceAll("I´m ", "I am");
					
					strLine=strLine.replaceAll("i\\?ll", "I will");
					strLine=strLine.replaceAll("I\\?ll", "I will");
					strLine=strLine.replaceAll("i'll", "I will");
					strLine=strLine.replaceAll("i`ll", "I will");
					strLine=strLine.replaceAll("I'll", "I will");
					strLine=strLine.replaceAll("I`ll", "I will");
					strLine=strLine.replaceAll("i´ll", "I will");
					strLine=strLine.replaceAll("I´ll", "I will");
					
					
					strLine=strLine.replaceAll("we\\?ll", "we will");
					strLine=strLine.replaceAll("We\\?ll", "We will");
					strLine=strLine.replaceAll("we'll", "we will");
					strLine=strLine.replaceAll("we`ll", "we will");
					strLine=strLine.replaceAll("We'll", "We will");
					strLine=strLine.replaceAll("We`ll", "We will");
					strLine=strLine.replaceAll("we´ll", "We will");
					strLine=strLine.replaceAll("We´ll", "We will");

					strLine=strLine.replaceAll("it\\?ll", "it will");
					strLine=strLine.replaceAll("It\\?ll", "It will");
					strLine=strLine.replaceAll("it'll", "it will");
					strLine=strLine.replaceAll("it`ll", "it will");
					strLine=strLine.replaceAll("It'll", "It will");
					strLine=strLine.replaceAll("It`ll", "It will");
					strLine=strLine.replaceAll("it´ll", "it will");
					strLine=strLine.replaceAll("It´ll", "It will");

					strLine=strLine.replaceAll("he\\?ll", "he will");
					strLine=strLine.replaceAll("He\\?ll", "He will");
					strLine=strLine.replaceAll("he'll", "he will");
					strLine=strLine.replaceAll("he`ll", "he will");
					strLine=strLine.replaceAll("He'll", "He will");
					strLine=strLine.replaceAll("He`ll", "He will");
					strLine=strLine.replaceAll("he´ll", "He will");
					strLine=strLine.replaceAll("He´ll", "He will");

					strLine=strLine.replaceAll("she\\?ll", "she will");
					strLine=strLine.replaceAll("She\\?ll", "She will");
					strLine=strLine.replaceAll("she'll", "she will");
					strLine=strLine.replaceAll("she`ll", "she will");
					strLine=strLine.replaceAll("She'll", "She will");
					strLine=strLine.replaceAll("She`ll", "She will");
					strLine=strLine.replaceAll("she´ll", "she will");
					strLine=strLine.replaceAll("She´ll", "She will");

					strLine=strLine.replaceAll("you\\?ll", "you will");
					strLine=strLine.replaceAll("You\\?ll", "You will");
					strLine=strLine.replaceAll("you'll", "you will");
					strLine=strLine.replaceAll("you`ll", "you will");
					strLine=strLine.replaceAll("You'll", "You will");
					strLine=strLine.replaceAll("You`ll", "You will");
					strLine=strLine.replaceAll("you´ll", "you will");
					strLine=strLine.replaceAll("You´ll", "You will");


					strLine=strLine.replaceAll("didn\\?t", "did not");
					strLine=strLine.replaceAll("Didn\\?t", "Did not");
					strLine=strLine.replaceAll("didn't", "did not");
					strLine=strLine.replaceAll("Didn't", "Did not");
					strLine=strLine.replaceAll("didn`t", "did not");
					strLine=strLine.replaceAll("Didn`t", "Did not");
					strLine=strLine.replaceAll("didnt", "did not");
					strLine=strLine.replaceAll("Didnt", "Did not");
					strLine=strLine.replaceAll("did´t", "did not");
					strLine=strLine.replaceAll("Didn´t", "Did not");
					
					strLine=strLine.replaceAll("don\\?t", "do not");
					strLine=strLine.replaceAll("Don\\?t", "Do not");
					strLine=strLine.replaceAll("don't", "do not");
					strLine=strLine.replaceAll("Don't", "Do not");
					strLine=strLine.replaceAll("don`t", "do not");
					strLine=strLine.replaceAll("Don`t", "Do not");
					strLine=strLine.replaceAll("dont", "do not");
					strLine=strLine.replaceAll("Dont", "Do not");
					strLine=strLine.replaceAll("don´t", "do not");
					strLine=strLine.replaceAll("Don´t", "Do not");
					
					strLine=strLine.replaceAll("doesn\\?t", "does not");
					strLine=strLine.replaceAll("Doesn\\?t", "Does not");
					strLine=strLine.replaceAll("doesn't", "does not");
					strLine=strLine.replaceAll("Doesn't", "Does not");
					strLine=strLine.replaceAll("doesn`t", "does not");
					strLine=strLine.replaceAll("Doesn`t", "Does not");
					strLine=strLine.replaceAll("doesnt", "does not");
					strLine=strLine.replaceAll("Doesnt", "Does not");
					strLine=strLine.replaceAll("doesn´t", "does not");
					strLine=strLine.replaceAll("Doesn´t", "Does not");
					
					strLine=strLine.replaceAll("haven\\?t", "have not");
					strLine=strLine.replaceAll("Haven\\?t", "Have not");
					strLine=strLine.replaceAll("havent", "have not");
					strLine=strLine.replaceAll("Havent", "Have not");
					strLine=strLine.replaceAll("haven't", "have not");
					strLine=strLine.replaceAll("Haven't", "Have not");
					strLine=strLine.replaceAll("haven`t", "have not");
					strLine=strLine.replaceAll("Haven`t", "Have not");
					strLine=strLine.replaceAll("haven´t", "have not");
					strLine=strLine.replaceAll("Haven´t", "Have not");
					
					
					strLine=strLine.replaceAll("hadn\\?t", "had not");
					strLine=strLine.replaceAll("Hadn\\?t", "Had not");
					strLine=strLine.replaceAll("hadn't", "had not");
					strLine=strLine.replaceAll("Hadn't", "Had not");
					strLine=strLine.replaceAll("hadn`t", "had not");
					strLine=strLine.replaceAll("Hadn`t", "Had not");
					strLine=strLine.replaceAll("hadnt", "had not");
					strLine=strLine.replaceAll("Hadnt", "Had not");
					strLine=strLine.replaceAll("hadn´t", "had not");
					strLine=strLine.replaceAll("Hadn´t", "Had not");
					
					strLine=strLine.replaceAll("I\\?ve", "I have");
					strLine=strLine.replaceAll("i\\?ve", "I have");
					strLine=strLine.replaceAll("I've", "I have");
					strLine=strLine.replaceAll("I`ve", "I have");
					strLine=strLine.replaceAll("i've", "I have");
					strLine=strLine.replaceAll("i`ve", "I have");
					strLine=strLine.replaceAll("I´ve", "I have");
					strLine=strLine.replaceAll("i´ve", "I have");
					
					strLine=strLine.replaceAll("won\\?t", "will not");
					strLine=strLine.replaceAll("Won\\?t", "Will not");
					strLine=strLine.replaceAll("won't", "will not");
					strLine=strLine.replaceAll("Won't", "Will not");
					strLine=strLine.replaceAll("won`t", "will not");
					strLine=strLine.replaceAll("Won`t", "Will not");
					strLine=strLine.replaceAll("wont", "will not");
					strLine=strLine.replaceAll("Wont", "Will not");
					strLine=strLine.replaceAll("won´t", "will not");
					strLine=strLine.replaceAll("Won´t", "Will not");
					
					strLine=strLine.replaceAll("that\\?s", "that is");
					strLine=strLine.replaceAll("That\\?s", "That is");
					strLine=strLine.replaceAll("that's", "that is");
					strLine=strLine.replaceAll("That's", "that is");
					strLine=strLine.replaceAll("that`s", "that is");
					strLine=strLine.replaceAll("That`s", "that is");
					strLine=strLine.replaceAll("that´s", "that is");
					strLine=strLine.replaceAll("That´s", "that is");
					
					strLine=strLine.replaceAll("There\\?s", "there is");
					strLine=strLine.replaceAll("there\\?s", "there is");
					strLine=strLine.replaceAll("There`s", "there is");
					strLine=strLine.replaceAll("there`s", "there is");
					strLine=strLine.replaceAll("There's", "there is");
					strLine=strLine.replaceAll("there's", "there is");
					strLine=strLine.replaceAll("There´s", "there is");
					strLine=strLine.replaceAll("there´s", "there is");
					
					strLine=strLine.replaceAll("we\\?ve", "we have");
					strLine=strLine.replaceAll("We\\?ve", "we have");
					strLine=strLine.replaceAll("we've", "we have");
					strLine=strLine.replaceAll("We've", "we have");
					strLine=strLine.replaceAll("we`ve", "we have");
					strLine=strLine.replaceAll("We`ve", "we have");
					strLine=strLine.replaceAll("we´ve", "we have");
					strLine=strLine.replaceAll("We´ve", "we have");
					
					strLine=strLine.replaceAll(" it wrong", " it is wrong");



					strLine=strLine.replaceAll(" wrote:","");
					strLine=strLine.replaceAll("\\.xar"," xar").replaceAll("\\.xml"," xml").replaceAll("\\.txt"," txt").replaceAll("\\.war"," war").replaceAll("\\.jar"," jar").replaceAll("\\.png"," png").replaceAll("\\.jpg"," jpg").replaceAll("\\.gif"," gif").replaceAll("\\.java"," java").replaceAll("\\.sh"," sh").replaceAll("\\.vm"," vm").replaceAll("\\.avi"," avi").replaceAll("\\.bat"," bat").replaceAll("\\.cmd"," cmd").replaceAll("\\.odt"," odt").replaceAll("\\.zip"," zip").replaceAll("\\.doc"," doc").replaceAll("\\.csv"," csv").replaceAll("\\.ppt"," ppt");


					if(strLine.trim().compareTo("")!=0)
						if(strLine.split(" ")[0].compareTo("&gt;")!=0 && strLine.compareTo("")!=0)
						{
							strLine=strLine.replaceAll(","," ");
							strLine=strLine.replaceAll("-"," ");
							strLine=strLine.replaceAll("``"," ");
							strLine=strLine.replaceAll("\""," ");
							strLine=strLine.replaceAll("="," ");
							strLine=strLine.replaceAll(":\\("," SAD ");
							strLine=strLine.replaceAll(":-\\("," SAD ");
							strLine=strLine.replaceAll("\\(","");
							strLine=strLine.replaceAll(":\\)"," SMILEY ");
							strLine=strLine.replaceAll(":D"," SMILEY ");
							strLine=strLine.replaceAll(";\\)"," SMILEY ");
							strLine=strLine.replaceAll(";D"," SMILEY ");
							strLine=strLine.replaceAll(":-\\)"," SMILEY ");
							strLine=strLine.replaceAll(":-D"," SMILEY ");
							strLine=strLine.replaceAll(";-\\)"," SMILEY ");
							strLine=strLine.replaceAll(";-D"," SMILEY ");
							strLine=strLine.replaceAll("\\)","");
							strLine=strLine.replaceAll("\\*","");
							strLine=strLine.replaceAll(">","");
							strLine=strLine.replaceAll("<","");
							strLine=strLine.replace("Comments", "");
							strLine=strLine.replaceAll("&gt>","");
							strLine=strLine.replaceAll("&","");
							strLine=strLine.replaceAll("&gt","");
							strLine=strLine.replaceAll("&amp","");
							strLine=strLine.replaceAll("--","");
							strLine=strLine.replaceAll("=\\-","");
							strLine=strLine.replaceAll("=-","");
							strLine=strLine.replaceAll("\\-..\\-", "");
							strLine=strLine.replaceAll("gt:","");
							strLine=strLine.replaceAll("-gt","");
							strLine=strLine.replaceAll("&quot;","");
							strLine=strLine.replaceAll(" :","");
							strLine=strLine.replaceAll(" \\?", "? ");
							strLine=strLine.replaceAll("  \\?", "? ");
							strLine=strLine.replaceAll("/\\.\\.", "");
							strLine=strLine.replaceAll("  ", " ");
							strLine=strLine.replaceAll(" \\. ", "\\. ");
							strLine=strLine.replaceAll("\n", " ");
							strLine=strLine.replaceAll("\t", " ");


							//System.out.println(strLine);
							lines+=strLine;		
							lines=lines.replaceAll("﻿", "");
							lines=lines.replaceAll("Ôªø", "");
							lines+="\n";
						}
				}
			}
			return lines;
		}
}
