
import java.util.Set;

public interface Query{
	public Set<WebDoc> matches(WebIndex wind);
}
