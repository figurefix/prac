package figurefix.prac.test.taglib;

import javax.servlet.http.HttpServletRequest;

import figurefix.prac.taglib.SelectElement;
import figurefix.prac.taglib.html.HtmlElement;
import figurefix.prac.taglib.html.TextNode;

public class MySelection implements SelectElement {

	@Override
	public HtmlElement build(HttpServletRequest request, String value) throws Exception {
		HtmlElement select = new HtmlElement("select");
		select.addSubNode(this.create("va", "vvv aaa", value));
		select.addSubNode(this.create("vb", "vvv bbb", value));
		select.addSubNode(this.create("vc", "vvv ccc", value));
		return select;
	}
	
	private HtmlElement create(String value, String text, String dftval) {
		HtmlElement o = new HtmlElement("option");
		TextNode t = new TextNode(text);
		o.getAttributeList().set("value", value);
		if(value.equals(dftval)) {
			o.getAttributeList().set("selected");
		}
		o.addSubNode(t);
		return o;
	}

}
