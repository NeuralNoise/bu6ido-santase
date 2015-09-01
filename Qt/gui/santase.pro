//CONFIG += debug_and_release
TEMPLATE = app
TARGET = qsantase
QT += core gui
//QMAKE_CXXFLAGS += -std=c++11

SOURCES = sleeper.cpp card.cpp iboard.cpp inotification.cpp abstract_player.cpp \
	  settings.cpp game.cpp gui_player.cpp computer_player.cpp \
	  gui_notification.cpp gui_board.cpp settings_dialog.cpp santase_main_window.cpp santase.cpp
HEADERS = common.h sleeper.h card.h iboard.h inotification.h abstract_player.h \ 
	  settings.h game.h gui_player.h computer_player.h \ 
          gui_notification.h gui_board.h settings_dialog.h santase_main_window.h
RESOURCES = santase.qrc
RC_FILE = santase.rc

