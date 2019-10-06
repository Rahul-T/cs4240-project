ANTLR_JAR = antlr-4.7.1-complete.jar

JFLAGS = -cp '.:$(ANTLR_JAR)' -Xlint
JC = @javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	TigerCompiler.java \
	tigerLexer.java \
	tigerListener.java \
	tigerParser.java \
	TigerParserWrapper.java \
	TerminalPrinter.java \

default: parser compiler

compiler: $(CLASSES:.java=.class)

clean: 
	@$(RM) *.class

parser:
	@java -jar $(ANTLR_JAR) -visitor -no-listener tiger.g4