package figurefix.prac.lab.branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BranchEditor {
	
	private String treeTable = null;
	private String lockTable = null;
	
	/**
	 * closure tree editor
	 * @param treeTable table that records relation（refrence BR_MAIN in pdm file）
	 * @param lockTable table that records locking nodes（refrence BR_LOCK in pdm file）
	 */
	public BranchEditor(String treeTable, String lockTable) {
		this.treeTable = treeTable;
		this.lockTable = lockTable;
	}
	
	private void checkNode(Connection con, String ... nodes) throws Exception {
		PreparedStatement pst = con.prepareStatement(
			"select count(*) from "+this.treeTable+" where sub=? and dis<=1");
		for(String node : nodes) {
			if(BranchBuilder.ROOT.equals(node)) {
				continue;
			}
			pst.setString(1, node);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				int cnt = rs.getInt(1);
				if(cnt==0) {
					throw new NodeNotFound();
				}
			} else {
				throw new NodeNotFound();
			}
		}
		pst.close();
	}
	
	public void add(Connection con, String node, String parent) throws Exception {
		
		if(con==null || node==null || parent==null) {
			throw new NullPointerException("parameter null");
		}
		
		if(con.getAutoCommit()) {
			throw new SQLException("Database connection auto commit");
		}
		
		if(BranchBuilder.ROOT.equals(parent)) {
			PreparedStatement insroot = con.prepareStatement(
					"insert into "+this.treeTable+
					" (sup, sub, dis) values (?, ?, ?)");
			insroot.setString(1, BranchBuilder.ROOT);
			insroot.setString(2, node);
			insroot.setInt(3, 0);
			insroot.executeUpdate();
			return;
		}

		// lock
		try {
			PreparedStatement lock = con.prepareStatement(
					"insert into "+this.lockTable+
					" (nod, keynod) values (?, ?) ");
			lock.setString(1, parent);
			lock.setString(2, node);
			lock.addBatch();
			lock.setString(1, node);
			lock.setString(2, node);
			lock.addBatch();
			
			lock.executeBatch();
			
		} catch (Exception e) {
			throw new BranchEditConflict(e);
		}
		
		this.checkNode(con, parent);

		PreparedStatement ins1 = con.prepareStatement(
				"insert into "+this.treeTable+
				" (sup, sub, dis) (select sup, ?, dis+1" +
				" from "+this.treeTable+" where sub=? and dis!=0)");
		ins1.setString(1, node);
		ins1.setString(2, parent);
		ins1.executeUpdate();
		PreparedStatement ins2 = con.prepareStatement(
				"insert into "+this.treeTable+
				" (sup, sub, dis) values (?,?,1)");
		ins2.setString(1, parent);
		ins2.setString(2, node);
		ins2.executeUpdate();
		
		this.unlock(con, node);
		
	}
	
	private void unlock(Connection con, String node) throws SQLException {
		PreparedStatement unlock = con.prepareStatement(
				"delete from "+this.lockTable+" where keynod=?");
		unlock.setString(1, node);
		unlock.executeUpdate();		
	}
	
	private void lock(Connection con, String node) throws BranchEditConflict {
		try {
			PreparedStatement lock1 = con.prepareStatement(
					"insert into "+this.lockTable+"(nod, keynod)"+
					" (select sub,? from "+this.treeTable+" where sup=?)");
			lock1.setString(1, node);
			lock1.setString(2, node);
			lock1.executeUpdate();
			PreparedStatement lock2 = con.prepareStatement(
					"insert into "+this.lockTable+"(nod, keynod) values (?,?)");
			lock2.setString(1, node);
			lock2.setString(2, node);
			lock2.executeUpdate();
		} catch (Exception e) {
			throw new BranchEditConflict(e);
		}
	}
	
	public void remove(Connection con, String node) throws Exception {
		
		if(con==null || node==null) {
			throw new NullPointerException("parameter null");
		}
		
		if(con.getAutoCommit()) {
			throw new SQLException("Database connection auto commit");
		}
		
		this.lock(con, node);
		
		PreparedStatement del1 = con.prepareStatement(
				"delete from "+this.treeTable+
				" where sub in (select nod from "+this.lockTable+" where keynod=?)");
		del1.setString(1, node);
		del1.executeUpdate();

		this.unlock(con, node);
	}
	
	private static class NodeDis {
		String nod = null;
		int dis = -1;
		NodeDis(String node, int dist) {
			this.nod = node;
			this.dis = dist;
		}
	}
	
	public void move(Connection con, String node, String to) throws Exception {
		
		if(con==null || node==null || to==null) {
			throw new NullPointerException("parameter null");
		}
		if(node.equals(to)) {
			throw new BranchEditConflict("target node error");
		}
		
		if(con.getAutoCommit()) {
			throw new SQLException("Database connection auto commit");
		}
		
		PreparedStatement chkpst = con.prepareStatement(
				"select count(*) from "+this.treeTable+" where sup=? and sub=? and dis<=1");
		chkpst.setString(1, to);
		chkpst.setString(2, node);
		ResultSet chkrs = chkpst.executeQuery();
		if(chkrs.next()) {
			int chkcnt = chkrs.getInt(1);
			if(chkcnt==1) { // move to it's parent
				return;
			}
		}
		
		this.lock(con, node);
		
		try {	
			PreparedStatement lock = con.prepareStatement(
				"insert into "+this.lockTable+" (nod, keynod) values (?,?)");
			lock.setString(1, to);
			lock.setString(2, node);
			lock.executeUpdate();
		} catch (Exception e) {
			throw new BranchEditConflict(e);
		}
		
		this.checkNode(con, node, to);
		
		PreparedStatement pstp = con.prepareStatement("select sup from "+this.treeTable+" where sub=?");
		pstp.setString(1, node);
		ResultSet rsp = pstp.executeQuery();
		ArrayList<String> parents = new ArrayList<String>();
		while(rsp.next()) {
			parents.add(rsp.getString(1));
		}
		rsp.close();

		PreparedStatement del = con.prepareStatement(
				"delete from "+this.treeTable+" where sup=? "+
				" and sub in (select nod from "+this.lockTable+" where keynod=? and nod!=?)");
		for(String parent : parents) {
			del.setString(1, parent);
			del.setString(2, node);
			del.setString(3, to);
			del.addBatch();
		}
		del.executeBatch();

		PreparedStatement ins2 = con.prepareStatement(
				"insert into "+this.treeTable+" (sup,sub,dis) values (?,?,?)");
		
		if(BranchBuilder.ROOT.equals(to)) {
			ins2.setString(1, to);
			ins2.setString(2, node);
			ins2.setInt(3, 0);
			ins2.executeUpdate();
		} else {
			PreparedStatement ins1 = con.prepareStatement(
					"insert into "+this.treeTable+" (sup,sub,dis)"+
					" (select ?,sub,dis+? from "+this.treeTable+" where sup=?)");
			
			ArrayList<NodeDis> ndlist = new ArrayList<NodeDis>();
			ndlist.add(new NodeDis(to, 1));
			
			PreparedStatement sel = con.prepareStatement(
					"select sup,dis+1 from "+this.treeTable+" where sub=? and dis!=0");
				sel.setString(1, to);
			ResultSet rs = sel.executeQuery();
			while(rs.next()) {
				NodeDis nd = new NodeDis(rs.getString(1), rs.getInt(2)); // sup, supdis
				ndlist.add(nd);
			}
			for(NodeDis nd : ndlist) {
				ins1.setString(1, nd.nod);
				ins1.setInt(2, nd.dis);
				ins1.setString(3, node);
				ins1.addBatch();
				
				ins2.setString(1, nd.nod);
				ins2.setString(2, node);
				ins2.setInt(3, nd.dis);
				ins2.addBatch();
			}
			
			ins1.executeBatch();
			ins2.executeBatch();			
		}

		this.unlock(con, node);
	}
	
}
