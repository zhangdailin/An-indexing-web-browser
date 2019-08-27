
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class WebIndex {

	Map<String, Set<WebDoc>> map1 = new TreeMap<>();
	Set<WebDoc> saveDoc = null;

	public WebIndex() {
	}

	public void add(WebDoc doc) {
		Iterator<String> a = doc.collectWords.iterator();
		while (a.hasNext()) {
			String name = a.next();
			saveDoc = new HashSet<>();
			if (map1.containsKey(name)) {
				map1.get(name).add(doc);
			} else {
				saveDoc.add(doc);
				map1.put(name, saveDoc);
			}
		}
	}

	public Collection<Set<WebDoc>> getURLs() {
		return map1.values();
	}

    public Set<WebDoc> getAllDocuments() {
        Set<WebDoc> allDocuments = new HashSet<>();
        for (String key: map1.keySet()){
            Iterator<WebDoc> keyIterator = map1.get(key).iterator();
            while (keyIterator.hasNext()) {
                allDocuments.add(keyIterator.next());
            }
        }
        return allDocuments;
    }
    
	// compare word with whole of content in information
	public Set<WebDoc> getMatches(String wd) {
		Set<WebDoc> saveUrl = null;
		if (map1.containsKey(wd)) {
			saveUrl = new HashSet<>();
			Iterator<WebDoc> b = map1.get(wd).iterator();
			while (b.hasNext()) {
				saveUrl.add(b.next());
			}
			return saveUrl;
		} else {
			return null;
		}
	}
}