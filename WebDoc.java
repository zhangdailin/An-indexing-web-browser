
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class WebDoc {
	URL url;
	Set<String> collectWords =new TreeSet<>();
	Set<String> collectKeywords =new TreeSet<>();
	
	String formedtest;
	
	public WebDoc(URL url) {
		this.url=url;
	}
	
	public void processUrl() {
		String htmls= getPageSource(url,"UTF-8").toString().toLowerCase();  
		String[] ss = htmls.split("\n");	
		for (int i = 0; i < ss.length; i++) {		
			if(ss[i].contains("name=\"keywords\"")) {	
				getKeyword(ss[i].trim()+" "+ss[i+1].trim());
				}
        }
		getContents(htmlRemoveTag(htmls));
		formedtest=formtest(htmls);
	}

	private String htmlRemoveTag(String inputString) {
		if (inputString == null)
			return null;
		String htmlStr = inputString;
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; 
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; 
			String regEx_html = "<[^>]+>";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textStr;
	}
	
    private StringBuffer getPageSource(URL url,String encoding) {    
        StringBuffer sb = new StringBuffer();    
        try {       
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));    
            String line;    
            while ((line = in.readLine()) != null) {    
            	sb.append(line).append(System.getProperty("line.separator"));  
            }    
            in.close();    
        } catch (Exception ex) {    
            System.err.println(ex);    
        }    
        return sb;    
    } 

	public Set<String> getContents(String removeHtm) {
		
		if(!removeHtm.isEmpty()) { 
			String a = removeHtm.trim().replace("."," ").replace("!", " ").replace(":", " ").replace("?", " ").replace("(", " ").replace(")", " ")
					.replace(",", " ").replace("-", " ").replace("/", " ").replace("\"", " ").replace("|", " ").replace(";", " ").replace(">", " ")
					.replace("&", " ").replace("?~", " ").replace("=", " ").replace("#x", " #x");
			String[] wordList = a.split("\\s+"); 
			for(int i=0;i<wordList.length;i++){
				if(!wordList[i].contains("#x")&&!wordList[i].contains("0")&&!wordList[i].contains("1")&&!wordList[i].contains("2")
						&&!wordList[i].contains("3")&&!wordList[i].contains("4")&&!wordList[i].contains("5")&&!wordList[i].contains("6")
						&&!wordList[i].contains("7")&&!wordList[i].contains("8")&&!wordList[i].contains("9")&&!wordList[i].contains("~")) {
					collectWords.add(wordList[i]);
				}
				
			}
		}
		return collectWords;
	}
	
	public Set<String> getKeyword(String Content) {	
		String prepross=Content.replaceAll("contents", "content");
		String start = "content=\"";
		String end = "\"";
		int a = prepross.indexOf(start) + start.length();
		int b = prepross.indexOf(end,a);	
		if(prepross.indexOf(start)>=0){
			String keywords = prepross.substring(a,b);
			if(!keywords.equals("")) {		
			String[] wordList = keywords.trim().split(","); 
			for(String key:wordList){
				String saveKeywords=key.trim();
				collectKeywords.add(saveKeywords);
			}
			return collectKeywords;
		}else {
			return null;
		}
		}else{
			return null;
		}	
	}
	
	private String formtest(String content) {
		String start = "<html>";
		String end = "</html>";
		String test= content;
		int a = test.indexOf(start) + start.length();
		int b = test.indexOf(end,a);	
		if(test.indexOf(start)>=0&&test.indexOf(end)>=0){
			String keywords = test.substring(a,b);
			String a1=test.replace(keywords, "").replace("<html>", "").replace("</html>", "");
			if(a1.trim().isEmpty()) {
				return "well-formed";
			}else {
				return "ill-formed";
			}
		}else {
			return "ill-formed";
		}	
	}
	
	public String toString() {
		if(!collectWords.isEmpty()) {
			return url + " "+ collectWords.size()+ " (" + ((TreeSet<String>) collectWords).first()+" - "+((TreeSet<String>) collectWords).last()+ ") " + collectKeywords.size() +" " +formedtest;
		}else {
			return url + " "+ collectWords.size()+ " (null - null) " + collectKeywords.size() +" " +formedtest;
		}
		
	}
}