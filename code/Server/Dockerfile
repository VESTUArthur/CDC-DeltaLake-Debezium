FROM debian 
COPY public-html /var/www/html
RUN apt-get update && apt-get install -y libapache2-mod-php7.4 && apt-get install -y apache2 && apt-get install -y php-mysql
CMD /usr/sbin/apache2ctl -D FOREGROUND