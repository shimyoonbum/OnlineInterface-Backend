# RestApplication

Spring Boot RestApplication + JDBC template + oracle

JPA DB connect, mybatis,interceptor,mvc,yaml,lombok,Mapper/jdbcTemplate,user rest api,
logging yaml/logback (-Dspring.profiles.active=local,log-console,log-file,log-jdbc -Dserver.id=local; sqltiming X), https, camel,
authkey, mandatory, response, error case, interfaces table, logging interfaces table, gradle.properties

oracle jdbc driver:
~\.m2\repository\com\oracle\jdbc\ojdbc8\12.2.0.1\ojdbc8-12.2.0.1.jar

maven build:
Base directory: ${project_loc:OnlineIFServer}
Goals: package
Profiles: pom.xml
gradle build:
name: OnlineIFServer-build.gradle
Base directory: ${project_loc:OnlineIFServer}
Goals: package
Profiles: build.gradle
deploy2dev.bat
deploy2prd1.bat
deploy2prd2.bat
gradle task: 1. Run -> External Tools -> External Tools Configurations 2. Program -> new Configurations 3. Set config
Location
${workspace_loc:/OnlineIFServer/deploy2dev.bat}
Working Directory
${workspace_loc:/OnlineIFServer} 4. Apply -> Run

run(win) :
bin/OnlineIFServerLocal.bat

run(linux) :
:set fileformat=unix
service OnlineIFServer start
service OnlineIFServer stop
service OnlineIFServer restart
