# JetBrains test assignment

A simple file browser that displays preview for text files and images. 
Supported storage types: local disk, zip archives, FTP server.

![alt text](https://gist.githubusercontent.com/yarps95/57c64da171e37148cc3936df5349b587/raw/1447d8f13f0f37a437ad38f2b0804f1e7110866b/screenshot.png "File browser with preview")

## Build

Requirements:

- Java 1.7+
- Maven 

```
mvn clean package
cd target
java -jar file-browser-1.0.0-jar-with-dependencies.jar
```

## Testing
In **test-files** folder there are some text files, images and an archive. 
Also for testing FTP-related things there's a public FTP server [test.rebex.net](http://test.rebex.net/).