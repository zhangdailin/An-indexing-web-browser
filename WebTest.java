
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class WebTest {
	public static void main(String[] args) {
		BufferedReader infile;
		String s;
		WebDoc setUrl = null;
		WebIndex b = null;
		try {
			infile = new BufferedReader(new FileReader(args[0]));
			b = new WebIndex();
			while ((s = infile.readLine()) != null) {
				setUrl = new WebDoc(new URL(s));
				setUrl.processUrl();
				b.add(setUrl);
				System.out.println(setUrl.toString());
			}
			infile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// input search word
		Scanner in = new Scanner(System.in);
		System.out.println("Type something to search: ");
		String wd = in.nextLine().toLowerCase();
		System.out.println(b.getMatches(wd));
		in.close();

		String saveQuery;
		try {
			QueryResult test = new QueryResult(b);
			BufferedReader Queryfile = new BufferedReader(new FileReader(args[1]));
			while ((saveQuery = Queryfile.readLine()) != null) {
				System.out.println(test.processQuery(saveQuery.toLowerCase().trim()));
			}
			Queryfile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}