agent: agent.class

agent.class: agent.java
	javac agent.java
	
run: agent.class
	java -classpath . agent
