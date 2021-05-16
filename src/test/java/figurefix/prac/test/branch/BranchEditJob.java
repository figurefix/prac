package figurefix.prac.test.branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.Transactional;
import figurefix.prac.jobs.alias.Alias;
import figurefix.prac.lab.branch.BranchBuilder;
import figurefix.prac.lab.branch.BranchEditor;

@Alias("BranchEdit")
public class BranchEditJob implements QuickJob, Transactional {
	
	@Override
	public void execute(QuickJobRt rt) throws Exception {
		rt.setForward("/test/branch/index.jsp");
		Connection con = rt.getDBA().getConnection();
		BranchEditor be = new BranchEditor("br_main", "br_lock");
		
		String op = rt.getRequest().getParameter("op");
		if("add".equals(op)) {
			String to = rt.getRequest().getParameter("pnode");
			if(to==null || to.trim().length()==0) {
				to = BranchBuilder.ROOT;
			}
			String name = rt.getRequest().getParameter("nodename");
			String time = String.valueOf(System.currentTimeMillis());
			String code = time.substring(time.length()-10);
			
			PreparedStatement ins = con.prepareStatement(
					"insert into br_name values (?,?)");
			ins.setString(1, code);
			ins.setString(2, name);
			ins.executeUpdate();

			be.add(con, code, to);
			
		} else if("del".equals(op)) {
			String code = rt.getRequest().getParameter("pnode");
			PreparedStatement ins = con.prepareStatement(
					"delete from br_name where code=?");
			ins.setString(1, code);
			ins.executeUpdate();
			be.remove(con, code);
			
		} else if("mov".equals(op)) {
			String code = rt.getRequest().getParameter("pnodex");
			String to = rt.getRequest().getParameter("pnode");
			if(to==null || to.trim().length()==0) {
				to = BranchBuilder.ROOT;
			}
			be.move(con, code, to);
		}
		
		//load
		Node root = new Node();
		root.code = BranchBuilder.ROOT;
		root.child = load(con, root.code);
		rt.getRequest().setAttribute("root", root);
	}
	
	private ArrayList<Node> load(Connection con, String node) throws Exception {
		PreparedStatement pst = con.prepareStatement(
				"select m.sub,n.name"
				+ " from br_main m left join br_name n on m.sub=n.code"
				+ " where m.sup=? and m.dis<=1");
		pst.setString(1, node);
		ResultSet rs = pst.executeQuery();
		ArrayList<Node> list = null;
		while(rs.next()) {
			if(list==null) {
				list = new ArrayList<Node>();
			}
			Node nod = new Node();
			nod.code = rs.getString(1);
			nod.name = rs.getString(2);
			nod.child = load(con, nod.code);
			list.add(nod);
		}
		return list;
	}

}
