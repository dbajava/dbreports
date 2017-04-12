DBREPORTS - README
----------------------
Version Control
----------------------
Version Control
- 1.0 Created 07/04/2017 
----------------------

----------------------
Command Line Arguments (TODO)
----------------------
s silent mode (use this argument for schedules)
c create html file
eg.
java -jar dbreport.jar s
java -jar dbreport.jar c s

----------------------
Installation
----------------------
To install dbreports you will need:
- JRE 1.6+
- Compatible with Oracle 11g and 12c(maybe 10g)
- Copy dbreports.jar to any folder
- Create a folder called xml on the same level as dbreports.jar
- Add your xml files to the xml folder

----------------------
How to generate an encrypted password
----------------------
-On dbreport.jar
java -Dpasswd=<newPassword> -jar dbreport.jar

----------------------
XML configuration
----------------------
Dbreports works in one xml file per database instance. Every file holds only one database instace connection parameters and as many reports as it's needed.
These are the necessary parameters for the database xml file:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<instance>
		<iname>ptrb.eeee.com</iname> --Database service name, it must be the same name used to connect to the database instance
		<fname>PTRB - Application Report</fname> --Database Report name, the database name that you want to show on the report
		<port>1921</port> --Listener port 
		<hostname>svora03.imagi.nation</hostname> --database hostname
		<username>patmurphy</username> --Database username
		<password>nOpAsSfOrYa</password> --Database password
		<mailto>pat.murphy@eeee.com</mailto> --Email(s) that will recive the report. for more than one e-mail use ","
		<mailhost>54.33.162.22</mailhost> -- SMTP server needed to send e=mail
		<report> -- add one tag report for each report for this particular instance
			<title>N0N3</title> - Report Title
			<query>select instance_name,host_name,version,startup_time,SYSDATE from gv$instance</query> -- Report Query - Please check XML special chars session
			<colnames>instance_name,host_name,version,startup_time,date</colnames> -- Column name that you want to show on report
			<dayofweek>Everyday</dayofweek> --What day of week the report will run. You can write everyday for all days of week(Mon-Sun) or you can write the days that you'd like that the report run separed by ",": Monday,Friday,Sunday
		</report>
</instance>

----------------------
XML Specials
----------------------
-- If your report name is N0N3(N zero N three) the report will have no title and no division(<hr>) with the next report, Usefull for reports that uses more than one query.
-- Special chars
Symbol 								Escape Sequence
< (less-than)						&#60; or &lt;
> (greater-than)					&#62; or &gt;
& (ampersand)						&#38;
' (apostrophe or single quote)		&#39;
" (double-quote)					&#34;
-- Check the last session XML Example

----------------------
How to run
----------------------
-On dbreport.jar path, using command line, run the following command:
java -jar dbreport.jar -<argument>
eg. 
java -jar dbreport.jar c s
java -jar dbreport.jar s

----------------------
TODO
----------------------
- Remove unecessary files from jdbc and mail libraries

----------------------
XML Example
----------------------
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<instance>
	<iname>ptrb.eeee.com</iname> 
		<fname>PTRB - Application Report</fname> 
		<port>1921</port> 
		<hostname>svora03.imagi.nation</hostname>
		<username>patmurphy</username>
		<password>nOpAsSfOrYa</password> 
		<mailto>pat.murphy@eeee.com</mailto> 
		<mailhost>54.33.162.22</mailhost> 
		<report>
			<title>N0N3</title>
			<query>select instance_name,host_name,version,startup_time,SYSDATE from gv$instance</query>
			<colnames>instance_name,host_name,version,startup_time,date</colnames>
			<dayofweek>Everyday</dayofweek>
		</report>
		<report>
			<title>EOD job</title>
			<query>select count(*),buyer_name from tableowner1.tablebuyers</query>
			<colnames>Number of buyers, buyer name</colnames>
			<dayofweek>friday</dayofweek>
		</report>	
</instance>