# Description
I coded this project in early 2021.

I challenged my friends to see who could make the strongest Othello AI. 
This is my entry. I spent about a week on the project. I tried copying the techniques of AlphaGo.
I used a Monte-Carlo tree search that used a heuristic evaluation instead of a simulation step. The heuristic was
a neural network that was trained to predict the winner of the game from a given position. The AI worked fairly well.
In the end I came second in the competition.

I don't particularly like java, but I coded this project in java as practice for second year computer science.

# How to run
Run
`make`
to compile all the java.

If you are using linux, you may want to change the following line in the makefile

`@javac -d $(BINDIR)/ -cp "$(BINDIR)/encog-core-3.4.jar;$(BINDIR)/" $<`

to 

`@javac -d $(BINDIR)/ -cp "$(BINDIR)/encog-core-3.4.jar:$(BINDIR)/" $<`.


Then run the `Master.py` file. This can be done with `make run`.
