package figurefix.prac.test.report;

import java.util.Iterator;

import figurefix.prac.util.DataMap;
import figurefix.prac.util.DataSet;

public class SampleList implements Iterator<DataMap> {

	private String title = null;
	private int size = 0;
	private String[] dsname = null;
	
	public SampleList(String title, int size, String ... dsname) {
		this.title = title;
		this.size = size;
		this.dsname = dsname;
	}
	
	@Override
	public boolean hasNext() {
		return size>0;
	}

	@Override
	public DataMap next() {
		if(size>0) {
			size--;
			DataSet m = new DataSet();
			for(int i=0; i<this.dsname.length; i++) {
				m.set(this.dsname[i], "itr-"+this.title+"-"+this.dsname[i]+"-"+size);				
			}
			return m;
		} else {
			return null;			
		}
	}

	@Override
	public void remove() {

	}

}
