Maybe keep this interface and implementation format, but rename. 

I think I missed the idea. Make the interface the main name- for example remove the I on the interface name, and then implement them with a class name like HibrernateBlockchainDao for instance, so 

BlockchainDao is an interface
HibernateBlockchainDao is an implementing class
JdbcBlockchainDao is another implementing class

This is clean and semantic code, and not confused code. It's amazing my jalopy ran. 

Of course it ran, but it ran like duct tape fixed the issue with your door. But it was all we were taught. 

And we had to learn IoC and Spring at this time, annotations. So much was new. 

MUST DIFFERENTIATE BETWEEN JDBC/HIBERNATE AND JPA AND ORM and other things. 

My choice is spring data jpa versus spring data jdbc

USE (TRY) THIS (I think) https://spring.io/guides/gs/accessing-data-mysql/