# ReFeed

ReFeed is a tool for automatically extracting relevant information from end user feedback and computing priorities for the associated set of requirements. ReFeed is able to:

  - create a meaningful association between requirements and user feedback
  - extract relevant properties from user feedback (sentiment, intention, severity)
  - synthesize prioritization for the given set of requirements based on the properties of the associated user feedback

## Building
The project is developed using Java 8 and the build is managed via [Maven] (https://maven.apache.org/), so you need to have a working Maven installation and Java 8 runtime to build and execute ReFeed.

To build the project:
 - clone the repo
 ```sh
 $ git clone git@github.com:se-fbk/ReFeed.git
 ```
- set some local paths in ```config.properties ```: path to GATE files and path to WordNet DB files; download the files from the respective websites and place them locally, then update the paths in the config file
-  build the project
```sh 
$ mvn package 
```
make sure that there are not tests failing.

## Running
To run the overall tool, it suffices to run the class ``` userfeedbacknlp.util.App.java ```
To reproduce the results from the experiments reported in the paper, it suffices to run the class ``` userfeedbacknlp.util.Stat.java ``` 
The necessary data required are included in the project directory.

License
----
Apache V2.0 (https://www.apache.org/licenses/LICENSE-2.0)

