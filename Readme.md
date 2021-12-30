# Indico Object Detection Demo
## Usage
- Put files in path ``src/main/resources/input``. Note the file type should be PNG or JPEG, or CSV with image URLs. 
- Build file with``mvn clean install``
- Run with 
```shell
java -jar -Dworkflow_id="13" -Dtoken="{fill_with_your_api_token}" target/java-indico-object-detection-demo-1.0-SNAPSHOT-jar-with-dependencies.jar
```
- Enjoy your json format output in path ``src/main/resources/input``.
