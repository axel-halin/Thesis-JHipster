JHipster : BackEnd Application [Database] Authentication [ClusteredSession] [InternationalizationSupport] [SpringWebSockets] [TestingFrameworks] :: _JHipster ;

BackEnd : Gradle
	| Maven ;

Application : MicroserviceGateway
	| [SocialLogin] [LibSass] :: Monolithic
	| MicroserviceApplication
	| UaaServer ;

Database : Development Production [Hibernate2ndLvlCache] [ElasticSearch] :: SQL
	| Cassandra
	| MongoDB ;

Development : Oracle12c
	| H2
	| PostgreSQLDev
	| MariaDBDev
	| MySql ;

H2 : DiskBased
	| InMemory ;

Production : MySQL
	| Oracle
	| MariaDB
	| PostgreSQL ;

Hibernate2ndLvlCache : HazelCast
	| EhCache ;

Authentication : HTTPSession
	| Uaa
	| OAuth2
	| JWT ;

TestingFrameworks : [Gatling] [Cucumber] [Protractor] :: _TestingFrameworks ;

%%

OAuth2 and not SocialLogin and not MicroserviceApplication implies SQL or MongoDB ;
SocialLogin implies Monolithic and (HTTPSession or JWT) ;
UaaServer implies Uaa ;
Oracle implies H2 or Oracle12c ;
not OAuth2 and not SocialLogin and not MicroserviceApplication implies SQL or MongoDB or Cassandra ;
MySQL implies H2 or MySql ;
Monolithic implies JWT or HTTPSession or OAuth2 ;
SocialLogin implies SQL or MongoDB ;
MariaDB implies H2 or MariaDBDev ;
MicroserviceApplication or UaaServer implies not Protractor ;
PostgreSQL implies H2 or PostgreSQLDev ;
MicroserviceGateway or MicroserviceApplication implies JWT or Uaa ;
ClusteredSession implies Monolithic or MicroserviceGateway ;
SpringWebSockets implies Monolithic or MicroserviceGateway ;
not Database implies MicroserviceApplication ;