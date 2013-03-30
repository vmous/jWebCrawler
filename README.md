# Web Crawler

A multi-threaded, database-assisted, sitemap-aware, web crawler.

## Configuration

Here is some basic setup you need to prepare to run the crawler.

### Database

Assuming you have a ready to use MySQL server running somewhere, accessible through network, log-in to the CMD line client and follow the next steps.

Create the database to be used
~~~
mysql> CREATE DATABASE `jwebcrawler` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
~~~

Now select the new database you just created
~~~
mysql> use jwebcrawler
~~~

and start creating the needed tables
~~~
mysql> CREATE TABLE `domain` (
    -> `pk_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    -> `name` VARCHAR( 250 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
    -> ) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
~~~

~~~
mysql> CREATE TABLE `content` (
    -> `pk_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    -> `remote_uri` VARCHAR( 250 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
    -> `local_uri` VARCHAR( 250 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
    -> `title` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
    -> `fk_domain_id` INT NOT NULL,
    -> `fk_mime_id` INT NOT NULL
    -> ) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
~~~

~~~
mysql> CREATE TABLE `mime` (
    -> `pk_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    -> `content_type` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
    -> ) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
~~~

Now it is time to create the users to be able to handle the database
~~~
mysql> CREATE USER 'spiderman'@'localhost' IDENTIFIED BY '1234';
~~~

~~~
mysql> GRANT ALL PRIVILEGES ON *.* TO 'spiderman'@'localhost' IDENTIFIED BY '1234' WITH
    -> GRANT OPTION MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR                                                                                                                                                                                     
    -> 0 MAX_USER_CONNECTIONS 0;  
~~~

~~~
mysql> CREATE USER 'spiderman'@'%' IDENTIFIED BY '1234';
~~~

~~~
mysql> GRANT ALL PRIVILEGES ON *.* TO 'spiderman'@'%' IDENTIFIED BY '1243' WITH GRANT
    -> OPTION MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0
    -> MAX_USER_CONNECTIONS 0;
~~~

I also needed this in order to connect
~~~
GRANT ALL PRIVILEGES ON jwebcrawler.* TO 'spiderman'@'%' IDENTIFIED BY '1234' WITH GRANT OPTION;
~~~
