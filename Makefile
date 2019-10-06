ANTLR_JAR = antlr-4.7.1-complete.jar
ANTLR_FLAGS = -visitor -no-listener
GRAMMAR_FILE = tiger.g4

JFLAGS = -cp '.:$(ANTLR_JAR)' -Xlint
JC = @javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	TigerCompiler.java \

default: parser compiler

compiler: $(CLASSES:.java=.class)

clean: 
	@$(RM) *.class

parser:
	@java -jar $(ANTLR_JAR) $(ANTLR_FLAGS) $(GRAMMAR_FILE)