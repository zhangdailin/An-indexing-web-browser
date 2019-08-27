
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryBuilder {
	static Query query;
	static Set<WebDoc> saveDoc = new HashSet<WebDoc>();

	public static Query parse(String q, WebIndex wind) {
		String regex = "\\(.*\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(q);
		String replaced = q.replaceAll(regex, "").trim();
		if (matcher.find()) {
			String a = matcher.group().substring(1, matcher.group().length() - 1);
			if (q.contains("not(") || q.contains("and(") || q.contains("or(")) {
				Set<String> save = new TreeSet<>();
				if (a.contains("and(")) {
					String end = "and(";
					int k = a.indexOf(end);
					int j = a.length();
					save.add(a.substring(0, k));
					save.add(a.substring(k, j));
				} else if (a.contains("or(")) {
					String end = "or(";
					int k = a.indexOf(end);
					int j = a.length();
					save.add(a.substring(0, k));
					save.add(a.substring(k, j));
				} else if (a.contains("not(")) {
					String end = "not(";
					int k = a.indexOf(end);
					int j = a.length();
					save.add(a.substring(0, k));
					save.add(a.substring(k, j));
				}
				Iterator<String> save2 = save.iterator();
				while (save2.hasNext()) {
					parse(save2.next(), wind);
				}
			}
			System.out.println("************" + replaced);
			System.out.println("************" + a);
			if (replaced.contains("and")) {
				query = new AndQuery(a);
				saveDoc = query.matches(wind);
			} else if (replaced.contains("or")) {
				query = new OrQuery(a);
				saveDoc = query.matches(wind);
			} else if (replaced.contains("not")) {
				query = new NotQuery(a);
				saveDoc = query.matches(wind);
			} else {
				query = new AtomicQuery(q);
				saveDoc = query.matches(wind);
			}
		} else {
			query = new AtomicQuery(q);
			saveDoc = query.matches(wind);
		}
		return query;
	}

	public static String parseInfixForm(String q) {
		q = q.replace("not ", "!");
		String changeToprefix = "";
		String[] infixQuery = q.split(" ");
		Stack<String> querytype = new Stack<>();
		Stack<String> querycontent = new Stack<>();
		querytype.setSize((infixQuery.length + 1) / 2);
		querycontent.setSize((infixQuery.length - 1) / 2);
		int countContent = 0;
		int countType = 0;
		if (infixQuery.length == 1) {
			q = q.replace("!", "not(");
			if (QueryResult.countleft(q, 0) != QueryResult.countright(q, 0)) {
				q = addbrackets(q, QueryResult.countleft(q, 0) - QueryResult.countright(q, 0));
			}
			return q;
		} else if (infixQuery.length >= 3) {
			for (int i = 0; i < infixQuery.length; i++) {
				if (countType <= countContent) {
					if (countType != 0) {
						querytype.push(infixQuery[i] + ")" + ",");
						countType++;

					} else {
						querytype.push(infixQuery[i] + ",");
						countType++;
					}
				} else {
					querycontent.push(infixQuery[i] + "(");
					countContent++;
				}
			}

			ArrayList<String> querycontentList = new ArrayList<String>();
			for (int i = 0; i < ((infixQuery.length - 1) / 2); i++) {
				querycontentList.add(querycontent.pop());
			}
			ArrayList<String> querytypeList = new ArrayList<String>();
			for (int i = 0; i < ((infixQuery.length + 1) / 2); i++) {
				querytypeList.add(querytype.pop());
			}
			for (int i = 0; i < querycontentList.size(); i++) {
				changeToprefix = changeToprefix + querycontentList.get(i);
			}
			for (int i = querytypeList.size() - 1; i >= 0; i--) {
				changeToprefix = changeToprefix + querytypeList.get(i);
			}
			changeToprefix = changeToprefix.substring(0, changeToprefix.length() - 1);
		}
		if (changeToprefix.contains("!")) {
			int a = changeToprefix.indexOf("!");
			int b = changeToprefix.indexOf(",", a);
			changeToprefix = changeToprefix.substring(0, b) + ")"
					+ changeToprefix.substring(b, changeToprefix.length());
			changeToprefix = changeToprefix.replace("!", "not(");
		}
		return changeToprefix;
	}

	public static String addbrackets(String q, int num) {
		for (int i = 0; i < num; i++) {
			q = q + ")";
		}
		return q;
	}
}