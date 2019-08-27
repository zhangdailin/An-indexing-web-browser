
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class AndQuery implements Query {
	private String q;
	private Map<String, Set<WebDoc>> saveurl = new TreeMap<>();
	private Set<WebDoc> saveDoc;
	
	public AndQuery(String q) {
		this.q = q;
	}

	public Set<WebDoc> matches(WebIndex wind) {
		String[] ss = q.split(",");
		for (int i = 0; i < ss.length; i++) {
				saveurl.put(ss[i], wind.getMatches(ss[i]));
		}

		for (int i = 0; i < ss.length; i++) {
			saveDoc = new HashSet<WebDoc>();
			if(wind.map1.keySet().contains(ss[i])){
				for (String key : wind.map1.keySet()) {
					Iterator<WebDoc> a = wind.map1.get(key).iterator();
					while (a.hasNext()) {
						saveDoc.add(a.next());
					}
				}
			}
		}
		
		for (String key : saveurl.keySet()) {
			if(saveurl.get(key)!=null) {
				saveDoc.retainAll(saveurl.get(key));
			}else {
				saveDoc=null;
				break;
			}
		}
		return saveDoc;
	}
}
