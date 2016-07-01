<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="atg.servlet.*"%>
<%@page import="atg.repository.*"%>
<%@page import="atg.repository.rql.RqlStatement"%>

<dsp:page>


<%
class ListeStatutsMotifsIssues
{
	public void Ecrit(String a_String)
	{
		try
		{
			m_Response.getWriter().println(a_String+"<BR>");
			m_Response.getWriter().flush();
		}
		catch (Exception e)
		{
		}
	}
	
	public DynamoHttpServletResponse m_Response;
	private Repository m_Repository;

	public void setRepository(Repository a_Repository)
	{
		Ecrit("---------> castorama.ListeStatutsMotifsIssues.setRepository a_Repository="+a_Repository);
	  	m_Repository = a_Repository;
		Ecrit("<--------- castorama.ListeStatutsMotifsIssues.setRepository");
	}
	public Repository getRepository()
	{
		Ecrit("--------- castorama.ListeStatutsMotifsIssues.getRepository m_Repository="+m_Repository);
	  	return m_Repository;
	}
	
	public String getListeStatutsMotifsIssues()
	{
		Ecrit("--> castorama.ListeStatutsMotifsIssues.getListeStatutsMotifsIssues");
		String l_Resultat = "";		
		try
		{
			RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("statutMotifIssue");			
			RepositoryView l_ItemView = l_Item.getRepositoryView();
			String l_strStatement = "NOT statut.libelleStatut STARTS WITH ?0 ORDER BY statut.libelleStatut DESC,motif.libelleMotif DESC,issue.libelleIssue DESC";
			RqlStatement statement = RqlStatement.parseRqlStatement(l_strStatement);
			Object params[] = new Object[1];
			params[0] = new String("X");
			Ecrit("Statement : " + statement.toString());
			RepositoryItem [] items = statement.executeQuery(l_ItemView, params);
			Ecrit("Apres execute query");
			Vector l_Statuts = new Vector();
			String l_StatutCourant = "";
			String l_StatutPrecedent = "";
			
			Vector l_Motifs = new Vector();
			String l_MotifCourant = "";
			String l_MotifPrecedent = "";
			
			Vector l_Issues = new Vector();
			String l_IssueCourant = "";
			String l_IssuePrecedent = "";		
			
			int l_nStatuts = 0;
			int l_nMotifs = 0;
			int l_nIssues = 0;
			
			Ecrit("ICI1");
			if (items != null)
			{
				for (int i = 0;i<items.length;i++)
				{
					Ecrit("ICI2");
					RepositoryItem l_ItemStatut = (RepositoryItem)items[i].getPropertyValue("statut");
					RepositoryItem l_ItemMotif = (RepositoryItem)items[i].getPropertyValue("motif");
					RepositoryItem l_ItemIssue = (RepositoryItem)items[i].getPropertyValue("issue");
					
					l_StatutCourant = l_ItemStatut.getPropertyValue("libelleStatut").toString();
					l_MotifCourant = l_ItemMotif.getPropertyValue("libelleMotif").toString();
					l_IssueCourant = l_ItemIssue.getPropertyValue("libelleIssue").toString();
					
					Ecrit("items["+i+"]="+l_StatutCourant+","+l_MotifCourant+","+l_IssueCourant);
					
					Ecrit("ICI3");
					if (! l_StatutCourant.equals(l_StatutPrecedent))
					{
						l_nStatuts++;
						l_Statuts.addElement(l_StatutCourant);
						l_Motifs.addElement(new Vector());
						l_MotifPrecedent = "";
						l_IssuePrecedent = "";
					}
					
					Ecrit("ICI4");
					if (!l_MotifCourant.equals(l_MotifPrecedent))
					{
							l_nMotifs++;
							Vector l_v = (Vector)l_Motifs.elementAt(l_nStatuts-1);
							l_v.addElement(l_MotifCourant);
							
							l_Issues.addElement(new Vector());

							l_IssuePrecedent = "";
					}
					
					Ecrit("ICI5");
					if (!l_IssueCourant.equals(l_IssuePrecedent))
					{
							l_nIssues++;
							Vector l_v = (Vector)l_Issues.elementAt(l_nMotifs-1);
							l_v.addElement(l_IssueCourant);
					}
					
					Ecrit("ICI6");
					l_StatutPrecedent = l_StatutCourant;
					l_MotifPrecedent = l_MotifCourant;
					l_IssuePrecedent = l_IssueCourant;
				}
			}
			
			Ecrit("ICI7");
			
			int j,k,l;
			String lesStatuts = "var lesStatuts = new Array (";
			lesStatuts = lesStatuts + "'...','Vide',";
			for (j=0;j<l_Statuts.size();j++)
			{
				lesStatuts = lesStatuts + "'" + l_Statuts.elementAt(j)+"','lesMotifs_"+j+"'";
				if (j < l_Statuts.size()-1)
				{
					lesStatuts = lesStatuts + ",";
				}
			}
			Ecrit("ICI8");
			lesStatuts = lesStatuts + ");\n";
			l_Resultat = l_Resultat + lesStatuts;
			Ecrit(lesStatuts);
			
			l = 0;
			Ecrit("ICI9");
			for (j=0;j<l_Motifs.size();j++)
			{
				String lesMotifs = "var lesMotifs_"+j+" = new Array (";
				lesMotifs = lesMotifs + "'...','Vide',";
				Vector l_v = (Vector)l_Motifs.elementAt(j);
				for (k=0;k<l_v.size();k++)
				{
					lesMotifs = lesMotifs + "'" + l_v.elementAt(k)+"','lesIssues_"+l+"'";
					if (k < l_v.size()-1)
					{
						lesMotifs = lesMotifs + ",";
					}
					l++;
				}
				lesMotifs = lesMotifs + ");\n";
				l_Resultat = l_Resultat + lesMotifs;
				Ecrit(lesMotifs);
			}
			
			Ecrit("ICI10");
			for (j=0;j<l_Issues.size();j++)
			{
				String lesIssues = "var lesIssues_"+j+" = new Array (";
				lesIssues = lesIssues + "'...','Vide',";
				Vector l_v = (Vector)l_Issues.elementAt(j);
				for (k=0;k<l_v.size();k++)
				{
					lesIssues = lesIssues + "'" + l_v.elementAt(k)+"','"+l_v.elementAt(k)+"'";
					if (k < l_v.size()-1)
					{
						lesIssues = lesIssues + ",";
					}
				}
				lesIssues = lesIssues + ");\n";
				l_Resultat = l_Resultat + lesIssues;
				Ecrit(lesIssues);
			}
			Ecrit("ICI11");
			String l_Tableau = "function Tableau(elName)\n";
			l_Tableau = l_Tableau + "{\n";
			l_Tableau = l_Tableau + "if (elName == 'Vide') return Vide;\n";
			l_Tableau = l_Tableau + "if (elName == 'lesStatuts') return lesStatuts;\n";
			for (j=0;j<l_Motifs.size();j++)
			{
				l_Tableau = l_Tableau + "if (elName == 'lesMotifs_"+j+"') return lesMotifs_"+j+";\n";
			}
			Ecrit("ICI12");
			
			for (j=0;j<l_Issues.size();j++)
			{
				l_Tableau = l_Tableau + "if (elName == 'lesIssues_"+j+"') return lesIssues_"+j+";\n";
			}
			
			l_Tableau = l_Tableau + "}\n";
			
			l_Resultat = l_Resultat + l_Tableau;
			Ecrit("ICI13");
		}catch (RepositoryException e){
			l_Resultat="";
			Ecrit("(catch) castorama.ListeStatutsMotifsIssues.getListeStatutsMotifsIssues RepositoryException : "+e.toString());
		}catch(Exception e){
			l_Resultat="";
			Ecrit("(catch) castorama.ListeStatutsMotifsIssues.getListeStatutsMotifsIssues Exception : "+e.toString());
		}finally{
			Ecrit("<-- castorama.ListeStatutsMotifsIssues.getListeStatutsMotifsIssues");
		}
		return l_Resultat;
	}
}

%>
<HTML>
<H1>LISTE DES STATUTS MOTIFS ISSUES (serveur gulten)</H1><BR>
<%
	Repository l_Repository = (Repository)((DynamoHttpServletRequest)request).resolveName("/atg/registry/Repository/ContactCallCenterGSARepository");
	ListeStatutsMotifsIssues l_ListeStatutsMotifsIssues = new ListeStatutsMotifsIssues();
	l_ListeStatutsMotifsIssues.m_Response = (DynamoHttpServletResponse)response;
	l_ListeStatutsMotifsIssues.Ecrit("-----------------------------------------------------------DEBUT---------------------------------------------------------------");	
	l_ListeStatutsMotifsIssues.setRepository(l_Repository);
	String l_MotifsIssues = l_ListeStatutsMotifsIssues.getListeStatutsMotifsIssues();
	gnu.regexp.RE l_RE = new gnu.regexp.RE("\n");
	String l_Result = l_RE.substituteAll(l_MotifsIssues,"<BR>");
	l_ListeStatutsMotifsIssues.Ecrit("-----------------------------------------------------------RESUTAT---------------------------------------------------------------");
	l_ListeStatutsMotifsIssues.Ecrit(l_Result);
	l_ListeStatutsMotifsIssues.Ecrit("-----------------------------------------------------------FIN---------------------------------------------------------------");
%>
</HTML>

</dsp:page>
