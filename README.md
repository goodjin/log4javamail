# log4javamail
a log redirect for javamail to slf4j
javamail将debug日志默认输出到System.out，但很多应用程序希望日志能统一由像logback,log4j这样的类库来管理，此工具提供了一种方法将日志统一由slf4j来管理，具体使用方法如下：
<code>
  Log4JavaMail log4JavaMail = new Log4JavaMail(logger, "utf-8");//logger为slf4j的日志对象
  Session session = Session.getInstance(props, null); // 获得邮件会话对象
  session.setDebugOut(log4JavaMail); //设置将日志输出到工具类对象
  session.setDebug(true);
 </code>
//默认不输出邮件内容部分，如果需要输出则可以设置trace属性为true 
  log4JavaMail.setTrace(true);
