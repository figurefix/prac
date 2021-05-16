package figurefix.prac.test.report;

import org.junit.Test;

import figurefix.prac.report.Report;
import figurefix.prac.test.Commons;
import figurefix.prac.util.DataSet;

public class TestReport {

	
	public static DataSet buildDataSet() {
		DataSet data = new DataSet();
		data.set("a", "Nut Report");
		data.set("b", "hello");

		DataSet[] listx = new DataSet[3];
		for(int i=0; i<listx.length; i++) {
			listx[i] = new DataSet();
			listx[i].set("d", "d数据数据数据数据数据数据"+i);
			listx[i].set("e", "e数据数据数据数据数据数据"+i);
		}
		data.set("x", listx);
		
		DataSet[] listu = new DataSet[5];
		for(int i=0; i<listu.length; i++) {
			listu[i] = new DataSet();
			listu[i].set("d", "d数据"+i);
			if(i==3) {
				continue;
			}
			listu[i].set("e", "e数据"+i);
		}
		data.set("u", listu);
		
		DataSet[] listy = new DataSet[5];
		for(int i=0; i<listy.length; i++) {
			listy[i] = new DataSet();
			listy[i].set("d", "3-数据数据数据数据数据数据"+i);
			listy[i].set("e", "4-数据数据数据数据数据数据"+i);
		}
		data.set("y", listy);
		
		return data;
	}
	
	@Test
	public void test() throws Exception {
		Report rpt = Report.getInstance(Commons.getTestHome()+"sample.xlsx");
		rpt.setRowAccessWindowSize(100);
		rpt.export(buildDataSet(), Commons.getTestHome()+"product.xlsx");
	}
}
