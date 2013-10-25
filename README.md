reports
=======

<h3>Reports! application</h3>

<h5>Requirements:</h5>
<ul>
  <li>MySQL 5+ database available at <a>127.0.0.1:3306</a></li>
  <li>JDK 1.5+</li>
  <li>Maven 2+ (tested on Maven 3.0.5)</li>
<h5>Launch:</h5> execute reports.sql placed in the root directory against MySQL instance (with admin priviledge)

<ol>
  <li><strong>/reportrs$ mysql -u user - p &lt; reports.sql</strong></li>
  <li><strong>/reportrs/reports-solr$ mvn clean install tomcat6:run-war</strong></li>
  <li><strong>/reportrs/reports-app$ mvn clean install tomcat6:run-war</strong></li>
</ol>
  
  Application will be available under <a href="http://localhost:8090/sp">http://localhost:8090/sp</a>
  <br/>
  Solr admin application: <a href="http://localhost:8089/solr">http://localhost:8089/solr</a>
  
  <h5>Credentials:</h5>
  <ul>
  	<li><strong>admin : admin</strong></li>
  	<li><strong>user : user</strong></li>
  </ul>
  
  Email address for application subscription: aareports@mail.ru (password: reports2010)
  
  <h5>General Depandencies:</h5>
  <ul>
  	<li>Spring Framework Core 3.1.0.RELEASE</li>
  	<li>Spring Framework Web MVC 3.1.0.RELEASE</li>
  	<li>String Security 3.0.5.RELEASE</li>
  	<li>spring Data Solr 1.0.0</li>
  	<li>Hibernate Core 3.6.3.Final</li>
  	<li>Hibernate Entity Manager 3.6.3.Final</li>
  	<li>Hibernate JPA2 1.0.1</li>
  	<li>Hibernate Validator 4.3.0 + JSR-303 1.1</li>
  	<li>Bitronix TM 2.1.3 + JTA 1.1</li>
  	<li>Velocity 1.7 + Velocity Tools 2.0</li>
  	<li>JavaMail 1.4.5</li>
  	<li>slf4j 1.7.5</li>
  	<li>logback 1.0.13</li>
  	<li>Mockito 1.9.5</li>
  </ul>
  
  <h5>Web part</h5>
  <ul>
  	<li>jQuery 1.10.2</li>
  	<li>jQuery UI 1.10.3</li>
  	<li>jQuery Validation Plugin 1.11.1</li>
  	<li>Modernizr 2.6.2</li>
  	<li>normalize.css 1.1.2</li>
  	<li>Entypo Font Bundle</li>
  </ul>
  
  <h5>Search:</h5>
  <ul>
  	<li>JPA2 Criteria API</li>
  	<li>Custom Reverse Index implementation</li>
  	<li>Apache Solr 4.3</li>
  </ul>
  
  <h5>Browsers</h5>
  <ul>
  	<li>IE 8+</li>
  	<li>Firefox</li>
  	<li>Chrome</li>
  </ul>
