FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=ping
COPY my.cnf /etc/mysql/conf.d/my.cnf
ADD subscribe.sql /docker-entrypoint-initdb.d/
CMD ["mysqld"]
