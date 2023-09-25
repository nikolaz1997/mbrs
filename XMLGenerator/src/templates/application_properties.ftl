spring.application.name=${appName}

spring.datasource.url=jdbc:mysql://localhost:8081/${appName}?useSSL=false&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true
spring.datasource.username=admin
spring.datasource.password=admin

spring.jpa.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.show_sql=true
spring.jpa.hibernate.format_sql=true
spring.jackson.serialization.fail-on-empty-beans=false
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.jdbc.batch_size=50
spring.jpa.hibernate.connection.CharSet=utf8
spring.jpa.hibernate.connection.characterEncoding=utf8
spring.jpa.hibernate.connection.useUnicode=true
