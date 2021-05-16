package figurefix.prac.test.taglib;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.Alias;
import figurefix.prac.taglib.DataGrid;
import figurefix.prac.taglib.FormGrid;
import figurefix.prac.util.DataMap;
import figurefix.prac.util.DataSet;

@Alias("testTagLibJob")
public class TestTagLibJob implements QuickJob {
	
	static class DataItera implements Iterator<DataMap> {
		
		int i = 10;
		int ms = 10;
		
		DataItera(int n, int gap) {
			this.i = n;
			this.ms = gap;
		}

		@Override
		public boolean hasNext() {
			return i>0;
		}

		@Override
		public DataMap next() {
			if(this.ms>0) {
				try {
					Thread.sleep(this.ms); // emulate the database delay
				} catch (Exception e) {

				}				
			}
			i--;
			DataSet data = new DataSet();
			data.set("sno", "序号aaaaaaaaaa "+i);
			data.set("nam", "名称bbbbbbbbbbbbbbbbbbbbbbb\\'' "+i);
			data.set("rmk", "备注cccccccccccccccccccccccccccccccc "+i);
			data.set("num", i);
			return data;
		}

		@Override
		public void remove() {

		}
		
	}

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		
		HttpServletRequest req = rt.getRequest();
		req.setAttribute("matchif", "aa");
		
		String tipmsg = rt.getRequest().getLocale().getLanguage()+","+rt.getRequest().getLocale().getDisplayLanguage();
		rt.getRequest().setAttribute("normaltxt", tipmsg);
		
		req.setAttribute("showselect", "");

		ArrayList<DataMap> dm = FormGrid.bind(rt.getRequest(), "myformgrid");
		if(dm.size()>0) {
			dm.get(0).set("mytxt", "后台赋值");
			dm.get(0).set("myipt", "后台修改");
		}

		rt.getRequest().setAttribute("mymsg", "我的消息");
		rt.getRequest().setAttribute("msg1", new TestPageMsg());
		rt.getRequest().setAttribute("msg3", new Exception("xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！xx全错啦！！！"));
		
		DataGrid dg = new DataGrid();
		dg.setFixTitle(false);
		dg.addColumn("序号", "sno").setJsFunction("dglink").setJsArgus("sno", "nam");
		dg.addColumn("名称", "nam");
		dg.addColumn("备注", "rmk");
		dg.addColumn("数字", "num");
		dg.setData(new DataItera(10, 0));
		
		rt.getRequest().setAttribute("mydatagrid", dg);
		
		rt.getRequest().setAttribute("crash", new Exception("crashed exception"));
		
		String action = rt.getRequest().getParameter("action");
		
		if("download".equals(action)) {
			dg.export(rt.getResponse(), 
					"this is title", 
					"this is sub title");			
		} else {
			rt.setForward("/test/taglib/index.jsp");	
		}
	}
}
