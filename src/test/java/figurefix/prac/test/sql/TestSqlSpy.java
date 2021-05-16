package figurefix.prac.test.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.junit.Test;

import figurefix.prac.sql.SqlSpy;
import figurefix.prac.test.jobs.DsUtil;

public class TestSqlSpy {
	
	@Test
	public void spyStatement() throws Exception {

//		SqlSpy.setRule(new TestSpyRule());
		
		Connection con = SqlSpy.tap(
				DsUtil.getJobDataSource().getConnection(), 
				TestSqlSpy.class.getName());
		Statement st = con.createStatement();
		
		for(int i=0; i<5; i++) {
			st.addBatch("insert into tt values ("+i+", 'v"+i+"', "+i+")");			
		}
		st.executeBatch();
		con.close();		
	}
	
	@Test
	public void spyPreparedStatement() throws Exception {

		SqlSpy.setRule(new TestSpyRule());
		
		Connection con = SqlSpy.tap(
				DsUtil.getJobDataSource().getConnection(), 
				TestSqlSpy.class.getName());
		PreparedStatement st = con.prepareStatement("insert into tt values (?,?,?)");
		for(int i=0; i<3; i++) {
			st.setInt(1, i*10);
			st.setString(2, i+"aa");
			st.setDouble(3, i*100);
			st.addBatch();
		}
		st.executeBatch();
		con.close();		
	}
}
