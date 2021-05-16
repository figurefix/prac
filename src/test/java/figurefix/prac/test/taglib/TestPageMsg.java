package figurefix.prac.test.taglib;

import figurefix.prac.taglib.PageMessage;

public class TestPageMsg implements PageMessage {

	@Override
	public String getFgColor() {
		return "#DD0";
	}

	@Override
	public String getBgColor() {
		return "#00D";
	}

	@Override
	public String getHTML() {
		return "定制消息 customized MESSAGE";
	}

}
