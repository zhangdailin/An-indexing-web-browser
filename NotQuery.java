
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class NotQuery implements Query{
	private String q;
	Map<String, Set<WebDoc>> saveurl = new TreeMap<>();
	private Set<WebDoc> saveDoc = new HashSet<WebDoc>();

	public NotQuery(String q) {
		this.q = q;
	}

	public Set<WebDoc> matches(WebIndex wind) {
		String[] ss = q.split(",");
		for (int i = 0; i < ss.length; i++) {
			if (wind.getMatches(ss[i]) != null) {
				saveurl.put(ss[i], wind.getMatches(ss[i]));
			}
		}
		
		saveDoc=wind.getAllDocuments();

		for (String key : saveurl.keySet()) {
			saveDoc.removeAll(saveurl.get(key));
		}
		return saveDoc;
	}
}