.SUFFIXES:.java .class

SRCDIR=Taariq_java/src
BINDIR=Taariq_java

CLASSES=Reader.class Appender.class Board.class AlphaBeta.class PureMonte.class LockMonte.class ThreadedMonte.class MonteThread.class LockCrawler.class AILockMonte.class AIMaker.class AIMonte.class TaariqLearning.class KeyBoardInterrupt.class Gen.class TaariqNoLearning.class

$(BINDIR)/%.class: $(SRCDIR)/%.java
	@javac -d $(BINDIR)/ -cp "$(BINDIR)/encog-core-3.4.jar;$(BINDIR)/" $<
	@echo "Compiled " $(<:$(SRCDIR)/%=%)

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

defualt: $(CLASS_FILES)

clean:
	@rm $(BINDIR)/*.class
	@echo "Removed all classes"










run:
	python Master.py
