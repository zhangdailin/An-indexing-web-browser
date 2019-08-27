
import java.util.HashSet;
import java.util.Set;

public class AtomicQuery implements Query {
	
	private String q;
	private Set<WebDoc> saveDoc = new HashSet<WebDoc>();
	
	public AtomicQuery(String q) {
		this.q = q;
	}

	public Set<WebDoc> matches(WebIndex wind) {
		saveDoc =wind.getMatches(q);
		return saveDoc;
	}
}
