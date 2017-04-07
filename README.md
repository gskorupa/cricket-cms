# Cricket Microsite

**Cricket Microsite** is the Java based micro site service with integrated CMS. It is built based on the following assumptions:

* cloud native
* API based
* event driven
* microservices
* open source

## Main building blocks (services):

* website
* user manager 
* content manager
* search engine

## Technologies and libraries

* Java 1.8
* Cricket Microservices Framework
* Bootstrap 3.3.7
* RiotJS

## Quick start

Build and run the service from the command line using Apache Ant build system:

    [greg]$ ant run
    Buildfile: /home/greg/workspace/cricket-cms/build.xml
    
    clean:
        [delete] Deleting directory /home/greg/workspace/cricket-cms/build/classes
        [delete] Deleting directory /home/greg/workspace/cricket-cms/dist
        
    init:
        [mkdir] Created dir: /home/greg/workspace/cricket-cms/build/classes
        
    get-dependencies:
        [get] Getting: https://github.com/gskorupa/Cricket/releases/download/1.2.9/cricket-1.2.9.jar
        [get] To: /home/greg/workspace/cricket-cms/lib/cricket-1.2.9.jar
        [get] https://github.com/gskorupa/Cricket/releases/download/1.2.9/cricket-1.2.9.jar moved to https://github-cloud.s3.amazonaws.com/releases/47293999/a36c0a48-1b8a-11e7-8b98-e56c7fab40f7.jar?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAISTNZFOVBIJMK3TQ%2F20170407%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20170407T102937Z&X-Amz-Expires=300&X-Amz-Signature=1507431f43ab1c8139fb842c13f7a3cf6c90066039408b2b0c2def427d5c0126&X-Amz-SignedHeaders=host&actor_id=0&response-content-disposition=attachment%3B%20filename%3Dcricket-1.2.9.jar&response-content-type=application%2Foctet-stream
        [get] Not modified - so not downloaded
        [get] Getting: https://github.com/gskorupa/Cricket/releases/download/1.2.9/cricket-1.2.9-javadoc.jar
        [get] To: /home/greg/workspace/cricket-cms/lib/cricket-1.2.9-javadoc.jar
        [get] https://github.com/gskorupa/Cricket/releases/download/1.2.9/cricket-1.2.9-javadoc.jar moved to https://github-cloud.s3.amazonaws.com/releases/47293999/a36b7e84-1b8a-11e7-891c-e8f457524e5b.jar?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAISTNZFOVBIJMK3TQ%2F20170407%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20170407T102938Z&X-Amz-Expires=300&X-Amz-Signature=9c36f717cfef6967e1d22f0041e7eede11058dffcf0fe9f100d2d6fb48edd3f7&X-Amz-SignedHeaders=host&actor_id=0&response-content-disposition=attachment%3B%20filename%3Dcricket-1.2.9-javadoc.jar&response-content-type=application%2Foctet-stream
        [get] Not modified - so not downloaded
        
    compile:
        [javac] Compiling 11 source files to /home/greg/workspace/cricket-cms/build/classes
        
    dist:
        [mkdir] Created dir: /home/greg/workspace/cricket-cms/dist
        [copy] Copying 1 file to /home/greg/workspace/cricket-cms/dist
        
    jar:
        [copy] Copying 2 files to /home/greg/workspace/cricket-cms/build/classes
        [jar] Building jar: /home/greg/workspace/cricket-cms/dist/cricket-cms-core-1.0.0.jar
        [jar] Building jar: /home/greg/workspace/cricket-cms/dist/cricket-cms-1.0.0.jar
        [mkdir] Created dir: /home/greg/workspace/cricket-cms/dist/config
        [mkdir] Created dir: /home/greg/workspace/cricket-cms/dist/data
        [mkdir] Created dir: /home/greg/workspace/cricket-cms/dist/www
        [mkdir] Created dir: /home/greg/workspace/cricket-cms/dist/var
        [mkdir] Created dir: /home/greg/workspace/cricket-cms/dist/log
        [copy] Copying 1 file to /home/greg/workspace/cricket-cms/dist/config
        [copy] Copying 1 file to /home/greg/workspace/cricket-cms/dist/config
        [copy] Copying 1 file to /home/greg/workspace/cricket-cms/dist/data
        [copy] Copying 10 files to /home/greg/workspace/cricket-cms/dist/www
        [copy] Copied 9 empty directories to 1 empty directory under /home/greg/workspace/cricket-cms/dist/www
        [copy] Copying 1 file to /home/greg/workspace/cricket-cms/dist
        [zip] Building zip: /home/greg/workspace/cricket-cms/service.zip
        
    run:
        [exec] CRICKET RUNNERINFO:2017-04-07 10:29:40 +0000: LOADING SERVICE PROPERTIES FOR org.cricketmsf.microsite.Service
        [exec] 
        [exec] INFO:2017-04-07 10:29:40 +0000: 	UUID=be9576a4-b88d-4697-9e42-e6670a6c8715
        [exec] INFO:2017-04-07 10:29:40 +0000: 	env name=CricketService
        [exec] INFO:2017-04-07 10:29:40 +0000: 	host=0.0.0.0
        [exec] INFO:2017-04-07 10:29:40 +0000: 	port=8080
        [exec] INFO:2017-04-07 10:29:40 +0000: 	filter=org.cricketmsf.SecurityFilter
        [exec] INFO:2017-04-07 10:29:40 +0000: 	CORS=[org.cricketmsf.config.HttpHeader@4edde6e5]
        [exec] INFO:2017-04-07 10:29:40 +0000: 	Extended properties: {filter=org.cricketmsf.SecurityFilter, cors=Access-Control-Allow-Origin:*, port=8080, host=0.0.0.0, threads=0, user-confirm=false, time-zone=GMT}
        [exec] INFO:2017-04-07 10:29:40 +0000: LOADING ADAPTERS
        [exec] INFO:2017-04-07 10:29:40 +0000: ADAPTER: scheduler
        [exec] INFO:2017-04-07 10:29:40 +0000: 	path: ./data
        [exec] INFO:2017-04-07 10:29:40 +0000: 	envVAriable name: SCHEDULER_DB_PATH
        [exec] INFO:2017-04-07 10:29:40 +0000: 	file: scheduler.xml
        [exec] INFO:2017-04-07 10:29:40 +0000: 	scheduler database file location: /home/greg/workspace/cricket-cms/dist/data/scheduler.xml
        [exec] INFO:2017-04-07 10:29:40 +0000: ADAPTER: database
        [exec] INFO:2017-04-07 10:29:40 +0000: 	path: ./data
        [exec] INFO:2017-04-07 10:29:40 +0000: 	database name: local
        [exec] INFO:2017-04-07 10:29:40 +0000: 	database file: /home/greg/workspace/cricket-cms/dist/data/local.db
        [exec] INFO:2017-04-07 10:29:40 +0000: ADAPTER: WwwService
        [exec] INFO:2017-04-07 10:29:40 +0000: 	context=/
        [exec] INFO:2017-04-07 10:29:40 +0000: 	use-cache=true
        [exec] INFO:2017-04-07 10:29:40 +0000: 	page-processor=false
        [exec] INFO:2017-04-07 10:29:40 +0000: ADAPTER: UserService
        [exec] INFO:2017-04-07 10:29:40 +0000: 	context=/api/user
        [exec] INFO:2017-04-07 10:29:40 +0000: 	extended-response=false
        [exec] INFO:2017-04-07 10:29:40 +0000: 	date-format: java.text.SimpleDateFormat@4d810dda
        [exec] INFO:2017-04-07 10:29:40 +0000: ADAPTER: logger
        [exec] INFO:2017-04-07 10:29:40 +0000: 	logger name: EchoService
        [exec] INFO:2017-04-07 10:29:40 +0000: 	log-file-name: ./log/cricket%g.log
        [exec] INFO:2017-04-07 10:29:40 +0000: 	log to console: true
        [exec] INFO:2017-04-07 10:29:40 +0000: 	max-size: 1000000
        [exec] INFO:2017-04-07 10:29:40 +0000: 	file count: 10
        [exec] INFO:2017-04-07 10:29:40 +0000: 	logging level: FINEST
        [exec] INFO:2017-04-07 10:29:40 +0000: ADAPTER: echo
        [exec] INFO:2017-04-07 10:29:40 +0000: 	context=/api/echo
        [exec] INFO:2017-04-07 10:29:40 +0000: 	silent-mode=false
        [exec] INFO:2017-04-07 10:29:40 +0000: ADAPTER: FileReader
        [exec] INFO:2017-04-07 10:29:40 +0000: 	root: ./www/
        [exec] INFO:2017-04-07 10:29:40 +0000: 	index-file: index.html
        [exec] INFO:2017-04-07 10:29:40 +0000: END LOADING ADAPTERS
        [exec] INFO:2017-04-07 10:29:40 +0000: 
        [exec] INFO:2017-04-07 10:29:40 +0000: REGISTERING EVENT HOOKS
        [exec] INFO:2017-04-07 10:29:40 +0000: hook method for event category LOG : logEvent
        [exec] INFO:2017-04-07 10:29:40 +0000: hook method for event category HTTPLOG : logHttpEvent
        [exec] INFO:2017-04-07 10:29:40 +0000: hook method for event category * : processEvent
        [exec] INFO:2017-04-07 10:29:40 +0000: END REGISTERING EVENT HOOKS
        [exec] INFO:2017-04-07 10:29:40 +0000: Running initialization tasks
        [exec] INFO:2017-04-07 10:29:40 +0000: Starting listeners ...
        [exec] INFO:2017-04-07 10:29:40 +0000: scheduler (Scheduler)
        [exec] INFO:2017-04-07 10:29:40 +0000: Starting http listener ...
        [exec] INFO:2017-04-07 10:29:40 +0000: context: /
        [exec] INFO:2017-04-07 10:29:40 +0000: context: /api/user
        [exec] INFO:2017-04-07 10:29:40 +0000: context: /api/echo
        [exec] INFO:2017-04-07 10:29:40 +0000: 
        [exec] INFO:2017-04-07 10:29:40 +0000:   __|  \  | __|  Cricket
        [exec] INFO:2017-04-07 10:29:40 +0000:  (    |\/ | _|   Microservices Framework
        [exec] INFO:2017-04-07 10:29:40 +0000: \___|_|  _|_|    version 1.2.9
        [exec] INFO:2017-04-07 10:29:40 +0000: 
        [exec] INFO:2017-04-07 10:29:40 +0000: # Service: MSiteService
        [exec] INFO:2017-04-07 10:29:40 +0000: # UUID: be9576a4-b88d-4697-9e42-e6670a6c8715
        [exec] INFO:2017-04-07 10:29:40 +0000: # NAME: CricketService
        [exec] INFO:2017-04-07 10:29:40 +0000: #
        [exec] INFO:2017-04-07 10:29:40 +0000: # HTTP listening on port 8080
        [exec] INFO:2017-04-07 10:29:40 +0000: #
        [exec] INFO:2017-04-07 10:29:40 +0000: # Started in 103ms. Press Ctrl-C to stop
        [exec] INFO:2017-04-07 10:29:40 +0000:
        
 The service will be running until you press Ctrl-C. You can see the default web page at http://localhost:8080

## Where to go next

1. Modify html pages and resources within 'www' folder to test current (static page based) implementation
2. Watch cricket-cms for updates - dynamic, cms based version will be provided within next weeks 