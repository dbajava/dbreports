/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbasuite;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author c954080
 */
@XmlRootElement( name = "instance" )
@XmlType( propOrder = { "dbName", "fantasyName","hostName", "port", "userName","passw","reports","mailto","hostmail" } )
public class Instance {
	private String dbName;
	private String fantasyName;
	private String hostName;
	private String port;
	private String userName;
	private String passw;
	private String mailto;
	private String hostmail;
	List<Report> reports;


	public String getDbName() {
		return dbName;
	}
	@XmlElement( name = "iname" )
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getHostName() {
		return hostName;
	}
	@XmlElement( name = "hostname" )
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getPort() {
		return port;
	}
	@XmlElement( name = "port" )
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	@XmlElement( name = "username" )
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassw() {
		return passw;
	}
	@XmlElement( name = "password" )
	public void setPassw(String passw) {
		this.passw = passw;
	}
	public List<Report> getReports() {
		return reports;
	}
	public void add(Report report){
		if (this.reports==null){
			this.reports=new ArrayList<Report>();
		}
		this.reports.add(report);
	}
	@XmlElement(name="report")
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	public String getMailto() {
		return mailto;
	}
	@XmlElement(name="mailto")
	public void setMailto(String mailto) {
		this.mailto = mailto;
	}
	public String getHostmail() {
		return hostmail;
	}
	@XmlElement(name="mailhost")
	public void setHostmail(String hostmail) {
		this.hostmail = hostmail;
	}
	public String getFantasyName() {
		return fantasyName;
	}
	@XmlElement(name="fname")
	public void setFantasyName(String fantasyName) {
		this.fantasyName = fantasyName;
	}

	public ArrayList<String> getToMail(){
		StringTokenizer strTkn = new StringTokenizer(this.mailto, ",");
		ArrayList<String> arrLis = new ArrayList<String>(this.mailto.length());
		while(strTkn.hasMoreTokens())
			arrLis.add(strTkn.nextToken());
		return arrLis;
	}

}
