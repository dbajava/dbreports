package dbasuite;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = { "title", "query","rawColname", "dayOfWeek" } )
@XmlRootElement( name = "report" )
public class Report {
	private String title;
	private String query;
	private String rawColname;
	private String dayOfWeek;
	public String getTitle() {
		return title;
	}
@XmlElement( name = "title" )
	public void setTitle(String title) {
		this.title = title;
	}
	public String getQuery() {
		return query;
	}
@XmlElement( name = "query" )
	public void setQuery(String query) {
		this.query = query;
	}

@XmlElement( name = "colnames")
	public void setRawColname(String rawColname){
	this.rawColname=rawColname;
}
public String getRawColname(){
	return rawColname;
	
}

public String getDayOfWeek() {
	return dayOfWeek;
}
@XmlElement( name = "dayofweek")
public void setDayOfWeek(String dayOfWeek) {
	this.dayOfWeek = dayOfWeek;
}

public ArrayList<String> getDaysOfWeek(){
	 StringTokenizer strTkn = new StringTokenizer(this.dayOfWeek, ",");
	 ArrayList<String> arrLis = new ArrayList<String>(this.dayOfWeek.length());
	 while(strTkn.hasMoreTokens())
	 arrLis.add(strTkn.nextToken());
	 return arrLis;
}

public ArrayList<String> getColname(){
	 StringTokenizer strTkn = new StringTokenizer(this.rawColname, ",");
	 ArrayList<String> arrLis = new ArrayList<String>(this.rawColname.length());
	 while(strTkn.hasMoreTokens())
	 arrLis.add(strTkn.nextToken());
	 return arrLis;
}
}
