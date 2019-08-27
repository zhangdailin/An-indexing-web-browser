
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class QueryResult {
	private WebIndex wind;

	public QueryResult(WebIndex b) {
		this.wind = b;
	}

	public Set<String> processQuery(String q) {
		System.out.println("*****input query: " + q);
		int i = 0;
		int j = 0;
		if (countleft(q, i) != countright(q, j) || q.isEmpty()) {
			System.out.println("Unknow Type! ---" + q);
		} else if (!q.contains("(")) {
			System.out.println(QueryBuilder.parseInfixForm(q));
			Query a = QueryBuilder.parse(QueryBuilder.parseInfixForm(q), wind);
			if (a.matches(wind) != null) {
				Set<String> saveUrl = new TreeSet<>();
				Iterator<WebDoc> b = a.matches(wind).iterator();
				while (b.hasNext()) {
					saveUrl.add(b.next().url.toString());
				}
				return saveUrl;
			}
		} else {
			q = q.replace(" ( ", "(").replace(" )", ")").replace(" , ", ",");
			if (q.substring(0, 4).trim().equals("and(") || q.substring(0, 3).trim().equals("or(")
					|| q.substring(0, 4).trim().equals("not(")) {
				Query a = QueryBuilder.parse(q, wind);
				if (a.matches(wind) != null) {
					Set<String> saveUrl = new TreeSet<>();
					Iterator<WebDoc> b = a.matches(wind).iterator();
					while (b.hasNext()) {
						saveUrl.add(b.next().url.toString());
					}
					return saveUrl;
				}
			}
		}
		return null;
	}
	
	public static int countleft(String q, int i) {
		String right = "(";
		int a = q.indexOf(right) + right.length();
		int b = q.length();
		if (a > 0) {
			i++;
			return countleft(q.substring(a, b), i);
		}
		return i;
	}

	public static int countright(String q, int j) {
		String right = ")";
		int a = q.indexOf(right) + right.length();
		int b = q.length();
		if (a > 0) {
			j++;
			return countright(q.substring(a, b), j);
		}
		return j;
	}
}