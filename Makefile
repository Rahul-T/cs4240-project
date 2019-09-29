JFLAGS = -cp '.:antlr-4.7.2-complete.jar' -Xlint
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
	TerminalPrinter.java

default: classes

classes: $(CLASSES:.java=.class)

clean: 
	@$(RM) *.class