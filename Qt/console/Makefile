CFLAGS=-c -Wall
LDFLAGS=
SOURCES= card.cpp iboard.cpp inotification.cpp abstract_player.cpp settings.cpp \
	 game.cpp console_board.cpp console_notification.cpp console_player.cpp \
	 computer_player.cpp santase.cpp
OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=csantase

all: $(SOURCES) $(EXECUTABLE)
	
$(EXECUTABLE): $(OBJECTS) 
	$(CXX) $(LDFLAGS) $(OBJECTS) -o $@

.cpp.o:
	$(CXX) $(CFLAGS) $< -o $@

clean:
	rm -rf *.o $(EXECUTABLE)
