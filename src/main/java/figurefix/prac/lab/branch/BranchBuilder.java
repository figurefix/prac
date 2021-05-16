package figurefix.prac.lab.branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Set;

public class BranchBuilder {
	
	public static final String ROOT = "<ROOT>";

	private Connection con = null;
	private String treeTable = null;
	
	private String srcTable = null;
	private String srcThisNode = null;
	private String srcParentNode = null;
	
	/**
	 * build closure tree from parent-child relation tree
	 * @param con database connection
	 * @param treeTable table that records the relationships nodes 
	 * @param srcTable source table that records the relationships of parents and children
	 * @param srcThisNode child node column name in source table
	 * @param srcParentNode parent node column name in source table
	 */
	public BranchBuilder(Connection con, String treeTable,
			String srcTable, String srcThisNode, String srcParentNode) {
		
		this.con = con;
		this.treeTable = treeTable;
		
		this.srcTable = srcTable;
		this.srcThisNode = srcThisNode;
		this.srcParentNode = srcParentNode;
	}
	
	public void build() throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		this.con.setAutoCommit(true);
		PreparedStatement pstload = con.prepareStatement(
				"select "+this.srcThisNode+","+this.srcParentNode+" from "+this.srcTable);
		ResultSet rs = pstload.executeQuery();
		while(rs.next()) {
			map.put(rs.getString(1), rs.getString(2));
		}
		rs.close();
		
		int size = map.size();
		System.out.println("map built "+size);
		
		PreparedStatement pst = con.prepareStatement(
				"insert into "+this.treeTable+
				" (sup, sub, dis) values (?, ?, ?)");
		int count = 0;
		Set<String> set = map.keySet();
		int i = 0;
		for(String node : set) {
			i++;
			if(i%100==0) {
				System.out.println(i+"/"+size);
			}
			String parent = map.get(node);
			if(parent==null) { //TODO adjust this match rule for specific system
				pst.setString(1, ROOT);
				pst.setString(2, node);
				pst.setInt(3, 0);
				pst.addBatch();
				count++;
			} else {
				int dis = 0;
				while(parent!=null) { //TODO adjust this match rule for specific system
					dis++;
					pst.setString(1, parent);
					pst.setString(2, node);
					pst.setInt(3, dis);
					pst.addBatch();
					count++;
					parent = map.get(parent);
				}	
			}
			
			if(count%500==0) {
				pst.executeBatch();				
			}
		}
		if(count%500!=0) {
			pst.executeBatch();				
		}
		System.out.println("insert tree "+count);
		con.close();
	}
}
