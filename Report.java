package dbasuite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = { "title", "query","rawColname", "dayOfWeek", "dayOfMonth", "runHour" } )
@XmlRootElement( name = "report" )
public class Report {
	private String title;
	private List<String> query;
	private List<String> rawColname;
	private String dayOfWeek;
	private String dayOfMonth;
	private String runHour;
	public String getTitle() {
		return title;
	}
	@XmlElement( name = "title" )
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getQuery() {
		return query;
	}
	@XmlElement( name = "query" )
	public void setQuery(List<String> query) {
		this.query = query;
	}
	@XmlElement( name = "colnames")
	public void setRawColname(List<String> rawColname){
		this.rawColname=rawColname;
	}
	public List<String> getRawColname(){
		return rawColname;
	}
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	@XmlElement( name = "dayofweek")
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getDayOfMonth() {
		return dayOfMonth;
	}
	@XmlElement( name = "dayofmonth" )
	public void setDayOfMonth(String dom) {
		this.dayOfMonth = dom;
	}
	public String getrunHour() {
		return runHour;
	}
	@XmlElement( name = "runhour" )
	public void setrunHour(String runH) {
		this.runHour = runH;
	}

	public ArrayList<String> getDaysOfWeek(){
		StringTokenizer strTkn = new StringTokenizer(this.dayOfWeek, ",");
		ArrayList<String> arrLis = new ArrayList<String>(this.dayOfWeek.length());
		while(strTkn.hasMoreTokens())
			arrLis.add(strTkn.nextToken());
		return arrLis;
	}

	public ArrayList<String> getColname(int i){
		StringTokenizer strTkn = new StringTokenizer(this.rawColname.get(i), ",");
		ArrayList<String> arrLis = new ArrayList<String>(this.rawColname.get(i).length());
		while(strTkn.hasMoreTokens())
			arrLis.add(strTkn.nextToken());
		return arrLis;
	}

	public ArrayList<String> getDaysofMonth(){
		StringTokenizer strTkn = new StringTokenizer(this.dayOfMonth, ",");
		ArrayList<String> arrLis = new ArrayList<String>(this.dayOfMonth.length());
		while(strTkn.hasMoreTokens())
			arrLis.add(strTkn.nextToken());
		return arrLis;
	}

	public ArrayList<String> getRunHours(){
		StringTokenizer strTkn = new StringTokenizer(this.runHour, ",");
		ArrayList<String> arrLis = new ArrayList<String>(this.runHour.length());
		while(strTkn.hasMoreTokens())
			arrLis.add(strTkn.nextToken());
		return arrLis;
	}

	public boolean toRun(){
		ArrayList<String> dayOfWeek =this.getDaysOfWeek();
		String daysArray[] = {"sunday","monday","tuesday", "wednesday","thursday","friday", "saturday"};
		Calendar calendar = Calendar.getInstance();
		boolean weekRun=false;
		int day = calendar.get(Calendar.DAY_OF_WEEK)-1;
		for (int y=0;y<dayOfWeek.size();y++){
			if(dayOfWeek.get(y).toLowerCase().equals("everyday")){
				weekRun=true;	
			}else if(dayOfWeek.get(y).toLowerCase().equals(daysArray[day])){
				weekRun=true;
			}
		}
		ArrayList<String> runHour =this.getRunHours();
		calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		boolean hourRun=false;
		for (int i=0;i<runHour.size();i++){
			if(runHour.get(i).equals(String.valueOf(hour))){
				hourRun=true;
			}
		}
		if(hourRun&&weekRun){
			return true;
		}else if(hourRun){
			ArrayList<String> month =this.getDaysofMonth();
			calendar = Calendar.getInstance();
			int dayofMo = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i=0;i<month.size();i++){
				if(month.get(i).equals(String.valueOf(dayofMo))){
					return true;
				}
			}
			return false;
		}else{
			return false;
		}
	}

}