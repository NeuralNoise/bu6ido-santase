#include "santase_main_window.h"

int main(int argc, char *argv[])
{
  QApplication app(argc, argv);
  app.setWindowIcon(QIcon(":/images/qsantase.jpg"));
  QTextCodec::setCodecForCStrings(QTextCodec::codecForName("UTF-8"));

  SantaseMainWindow window;
  window.show();

  return app.exec();
}

