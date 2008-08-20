package smartrics.rest.test.fitnesse.fixture;

import java.util.ArrayList;
import java.util.List;

public class Resources {
	private List<String> resources = new ArrayList<String>();
	private static Resources instance = new Resources();
	public static Resources getInstance(){
		return instance;
	}

	public void clear(){
		resources.clear();
	}

	public void add(String r){
		resources.add(r);
	}

	public String get(int i){
		return resources.get(i);
	}

	public int size(){
		return resources.size();
	}

	public void add(int index, String element) {
		resources.add(index, element);
	}

	public String remove(int index) {
		return resources.remove(index);
	}

	public boolean remove(Object o) {
		return resources.remove(o);
	}

	public void reset() {
		resources.clear();
		resources.add("<resource>" + System.getProperty("line.separator") +
				"   <name>a funky name</name>" + System.getProperty("line.separator") +
				"   <data>an important message</data>" + System.getProperty("line.separator") +
				"</resource>");
	}


}

