# JWAF

JWAF (Java Web Agent Framework) is a [multi agent platform] based on the [Java EE] stack. It was built as a scalable and extensible web application. The platform provides an infrastructure for agent execution, communication and various other utilities for easier agent development. Agents can be implemented as stateless enterprise java beans, while their information and state are kept in a database. The application server used for development is [WildFly] 9 and the database is [MongoDB]. The platform can be extended with additional modules due to its modular design. The functionality of the platform is exposed to the rest of the world through a REST interface which enables communication with clients and other platform instances.

## Features

### Agent execution and communication
Agents communicate with each other, and with the rest of the world, primarily by exchanging messages. Agents can exchange messages within the platform, between different platform instances or with a client (human or other software). The platform keeps an agent directory and will route the messages to the desired receiver agent. One message can be sent to multiple agents.

Upon receiving a message, the platform invokes one of the agent's methods in order to notify him about the message. The way that the agent's methods are invoked depends on agent's execution type:

- **SingleThreadedAgent**: The platform ensures that there is at most one thread tied to a single instance of an agent at any given time. An agent is notified about a message by the platform by invoking the `_execute` method. The messages are stored in agent's private inbox and the agent can retrieve them at any time during his execution. The platform will notify the agent about each new message, unless the agent discovers the messages himself, either by retrieval or active ignoring.

- **MultiThreadedAgent**: There can be multiple threads tied to a single agent instance. Each message is passed to agent's `_handle` method on a separate thread. This model allows parallel execution, but agents have to take care of concurrent access to their data themselves (by avoiding access to the same data, by locking, etc..).

### Data
Agents can store their private data in a document database ([MongoDB]). Each agent has his own mongo collection and can store, query and update documents using [Morphia] (Object Document Mapper) interface. Agents can store and retrieve POJOs using morphia ODM. Additionally agents can annotate their `Serializable` fields with `@Persistent` annotation and they will be serialized/deserialized to/from the database automatically as if the bean was stateful.

### Event
Agents can register, subscribe to or fire events. When fired events send a notification message to all subscribed agents. Events can be fired with a `String` or `Serializable` content, and the content can be additionally processed by a registered `EventProcessor` bean. Agents can also register timers that fire events periodically or after a certain delay. Events can be used as a publish-subscribe form of passing messages.

### Service
Agents can discover and call agent services that are implemented as stateless beans. Services can contain key-value parameters specified using annotations, by which agents can search for them. Agents can invoke the services synchronously or asynchronously.

### Task
Clients can interact with agents by requesting a task to be executed. A `TaskDeployer` bean can create/find agents and pass them a message with the task request. Upon the completion of the task, the agent can send a response back to the deployer, which can then post-process the results and store them for later retrieval by the client.

### Behaviours
Agents can specify certain callback methods using annotations, that will be invoked in order to handle messages. The annotations can specify the behaviour name and the performative of the messages that it can handle. Agents can change their behaviour at any time during execution.

### Modular design
The platform is composed of a number of modules. Some of them are essential to the platform, while others can be ignored or excluded. New modules can be added easily on top of the existing ones. The modules expose their functionality through the so called management beans, which provide an interface towards agents and other modules. Agents communicate with the management beans through another layer of abstraction called tool objects. These objects create a facade that exposes platform functionalities in a way that is more intuitive from the point of view of an agent developer and allow for the functionalities to be combined as desired.
 
---

For more insight in platform features, you can look at the example test agents implemented in `org.jwaf.test.agents` or the javadocs documentation.

## Quick start

#### Requirements:

- Java JDK 8
- [WildFly] 8 or 9
- [MongoDB]

#### Installation:

- Download and install Java JDK 8
- Download and unpack Eclipse IDE (the project was built and tested with the eclipse compiler, javac can produce some errors due to certain incompatibilities)
- Download and unpack WildFly 8 or 9
- In Eclipse Preferences > Server > Runtime Environment: add new WildFly server runtime environment
- Download and install [MongoDB]
- Clone JWAF project and import it into eclipse as a maven project
- Maven-update the project to pull all the dependencies and build it using eclipse (Menu > Project > Clean.. unless eclipse builds it automatically)

#### Test run:
- Start the MongoDB server using default parameters
- Deploy the project to the WildFly server using eclipse (Run As > Run on Server or add it to the server in the Servers tab view)
- When the application is deployed the server logs should contain a line saying something like:

        INFO [AgentSetup] Registered agent type: <IntegrationTestAgent>.
	and a couple of similar lines for other agent types.
- To invoke the test agent you can request a test task by sending an HTTP POST request to http://localhost:8080/jwaf/task/request with the Content-Type: application/xml and the following content:

```xml
<?xml version="1.0"?>
<taskRequest>
	<employer>client1</employer>
	<taskType>IntegrationTestTask</taskType>
	<content></content>
</taskRequest>
```

After the tests finish, in the server logs you should see an output similar to this:

    All tests PASSED **************************
If all went well, you can now start implementing your own agents. For help and examples look at the test agents implementations and the javadoc documentation.


## Roadmap

Things and ideas that should be implemented in the future:

- Security:
  - message encryption
  - agent, platform and client authentication
- Interoperability with other [FIPA] compliant platforms like [JADE]
- Interpreter agent for JavaScript, Python, XTend or other more dynamic languages for easier agent development and execution isolation
- More unit/integration tests

## Contribute

If you would like to use or contribute to the project, you can use github's issue system to add an issue or send me an email if you have any questions.

## License

JWAF is a free and opensource project licensed under the MIT license (see LICENCE.txt).

[Java EE]:https://en.wikipedia.org/wiki/Java_Platform,_Enterprise_Edition
[WildFly]:http://wildfly.org/
[MongoDB]:https://www.mongodb.org/
[Morphia]:https://github.com/mongodb/morphia
[multi agent platform]:https://en.wikipedia.org/wiki/Multi-agent_system
[FIPA]:http://www.fipa.org/
[JADE]:http://jade.tilab.com/
