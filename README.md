# Wayne

Wayne is simply a basic bot with RAG, designed to learn about Quarkus, Langchain4j, LLMs, vector databases, etc.

<p align="center">
  <img src="/src/main/resources/META-INF/resources/images/wayne.png" width="480"/>
</p>

(Wayne logo by [\@Marttuki](https://www.instagram.com/m.art_tuki/))

This project uses 

* Quarkus, the Supersonic Subatomic Java Framework.
* Langchain4J + Quarkus Langchain4J extension.
* Chroma DB as vector database.
* HTMX + Tailwind to develop a straightforward and efficient web interface.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

Before, you need to have llama3 LLM running on Ollama.

```shell script
ollama run llama3:instruct
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/wayne-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.
