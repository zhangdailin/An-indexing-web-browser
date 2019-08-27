
import java.util.HashSet;
import java.util.Set;

public class OrQuery implements Query {
	private String q;
	private Set<WebDoc> saveDoc = new HashSet<WebDoc>();

	public OrQuery(String q) {
		this.q = q;
	}

	public Set<WebDoc> matches(WebIndex wind) {
		String[] ss = q.split(",");
		for (int i = 0; i < ss.length; i++) {
			if (wind.getMatches(ss[i]) != null && !q.contains("not(")&& !q.contains("or(")) {
				saveDoc.addAll(wind.getMatches(ss[i]));
			}
		}
		return saveDoc;
	}
}
