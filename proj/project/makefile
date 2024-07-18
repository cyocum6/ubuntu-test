# top is arm, bottom is x86-64

BUILDROOT_HOME=/home/iottemp/Documents/VirtualMachines/buildroot-2024.02.3

CC=$(BUILDROOT_HOME)/output/host/usr/bin/arm-linux-gcc
CFLAGS=--sysroot=$(BUILDROOT_HOME)/output/staging
INCLUDES=
LFLAGS=

LIBS=-lcurl -lc

SRC=tc_main.c tc_state.c tc_error.c
OBJ=$(SRC:.c=.o)
MAIN=tcsimd

RM=rm -rf

.c.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

$(MAIN): $(OBJ)
	$(CC) $(CFLAGS) $(INCLUDES) -o $(MAIN) $(OBJ) $(LFLAGS) $(LIBS)

all: $(MAIN)

clean: 
	$(RM) $(MAIN) *.o *~


# start x86-64

#CC=gcc
#CCFLAGS=
#INCLUDES=
#LFLAGS=-L/usr/lib/x86_64-linux-gnu
#LIBS=-lcurl -lpthread

#SRC=tc_main.c tc_state.c tc_error.c
#OBJ=$(SRC:.c=.o)
#MAIN=tcsimd

#RM=rm -rf

#.c.o:
#	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

#$(MAIN): $(OBJ)
#	$(CC) $(CCFLAGS) $(INCLUDES) -o $(MAIN) $(OBJ) $(LFLAGS) $(LIBS)

#all: $(MAIN)

#clean: 
#	$(RM) $(MAIN) *.o *~