CFLAGS = -Wall

all: crop board
	javac FloodIt.java

crop: crop.o ppm.o
board: board.o ppm.o

crop.o: ppm.h
board.o: ppm.h
ppm.o: ppm.h
