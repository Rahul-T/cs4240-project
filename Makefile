ANTLR_JAR = antlr-4.7.1-complete.jar
ANTLR_FLAGS = -visitor
ANTLR_FILES = tigerBaseListener.java tigerBaseVisitor.java tigerLexer.* tigerListener.java tigerParser.java tiger.interp tiger.tokens
GRAMMAR_FILE = tiger.g4

JFLAGS = -cp '.:$(ANTLR_JAR)' -Xlint
JC = @javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	*.java \

default: parser compiler

compiler: $(CLASSES:.java=.class)

clean: 
	@$(RM) *.class $(ANTLR_FILES)

parser:
	@java -jar $(ANTLR_JAR) $(ANTLR_FLAGS) $(GRAMMAR_FILE)