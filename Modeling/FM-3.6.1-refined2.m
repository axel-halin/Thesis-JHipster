//Generated by FAMILI
JHipster : Generator Authentication [SocialLogin] [Docker] [Database] [SpringWebSockets] [Libsass] [ClusteredSession] BackEnd [InternationalizationSupport] TestingFrameworks :: _JHipster ;

Generator : Server
	| Application ;

Server : MicroserviceApplication
	| UaaServer ;

Application : MicroserviceGateway
	| Monolithic ;

Authentication : HTTPSession
	| OAuth2
	| Uaa
	| JWT ;

Database : [Hibernate2ndLvlCache] Development Production [ElasticSearch] :: SQL
	| Cassandra
	| MongoDB ;

Hibernate2ndLvlCache : HazelCast
	| EhCache ;

Development : H2
	| PostgreSQLDev
	| MariaDBDev
	| MySql ;

H2 : DiskBased
	| InMemory ;

Production : MySQL
	| MariaDB
	| PostgreSQL ;

BackEnd : Gradle
	| Maven ;

TestingFrameworks : [Protractor] Gatling Cucumber :: _TestingFrameworks ;

%%

OAuth2 and not  SocialLogin and not  MicroserviceApplication implies SQL or MongoDB ;
SocialLogin implies (HTTPSession or JWT) and Monolithic and (SQL or MongoDB) ;
UaaServer implies Uaa ;
not  OAuth2 and not  SocialLogin and not  MicroserviceApplication implies SQL or MongoDB or Cassandra ;
Server implies not  Protractor ;
not  Server implies Protractor ;
MySQL implies H2 or MySql ;
MicroserviceApplication or MicroserviceGateway implies JWT or Uaa ;
Monolithic implies JWT or HTTPSession or OAuth2 ;
MariaDB implies H2 or MariaDBDev ;
PostgreSQL implies H2 or PostgreSQLDev ;
SpringWebSockets or ClusteredSession implies Application ;
Libsass implies Application ;

