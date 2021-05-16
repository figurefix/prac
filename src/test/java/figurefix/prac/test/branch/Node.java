package figurefix.prac.test.branch;

import java.util.ArrayList;

public class Node {
	public String code = null;
	public String name = null;
	public ArrayList<Node> child = null;
	public void tojs(StringBuffer js) {
		js.append(".spreadto('"+code+"-"+name+"','"+code+"')");
		for(int i=0; child!=null && i<child.size(); i++) {
			Node sub = child.get(i);
			sub.tojs(js);
		}
		js.append(".parent()");
	}
}
