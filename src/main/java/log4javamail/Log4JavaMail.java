package log4javamail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;

/**
 * 将javamail的debug日志输出到slf4j，由slf4j传递给具体的日志实现。<br/>
 * 用法：<br/>
 * <code>
 * Log4JavaMail log4JavaMail = new Log4JavaMail(logger, "utf-8");<br/>
 * Session session = Session.getInstance(props, null); // 获得邮件会话对象<br/>
 * session.setDebugOut(log4JavaMail); <br/>
 * session.setDebug(true);<br/>
 * </code><br/>
 * 默认不输出邮件内容部分，如果需要输出则可以设置trace属性为true <br/>
 * <code>log4JavaMail.setTrace(true);</code>
 * 
 * @author jinshan
 *
 */
public class Log4JavaMail extends PrintStream {
	private static byte[] CRLF = new byte[] { 13, 10 };
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private Logger actualLog;
	private boolean trace;
	private String charset;
	private boolean hitData;

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Log4JavaMail(Logger log) {
		super(System.out);
		this.actualLog = log;
	}

	public Log4JavaMail(Logger log, String charset) {
		super(System.out);
		this.actualLog = log;
		this.charset = charset;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public void write(byte buf[], int off, int len) {
		bos.write(buf, off, len);
		//到过一行末尾，输出日志
		if (len > 1) {
			if (buf[off + len - 2] == CRLF[0]
					&& buf[off + len - 1] == CRLF[1]) {
				flush();
			}
		}
	}

	@Override
	public void flush() {
		if (charset == null) {
			charset = "utf-8";
		}
		try {
			String msg = new String(bos.toByteArray(), charset);
			//debug输出会加上换行符，所以去掉javamail加上的换行符。
			if (msg.endsWith("\r\n")) {
				msg = msg.substring(0, msg.length() - 2);
			}

			if (msg.equals("DATA")) {
				hitData = true;
			} else if (msg.equals(".\r\n")) {
				hitData = false;
			}

			if (hitData) {
				//data阶段输出邮件内容,设置为trace级别.
				actualLog.trace(msg);
			} else {
				actualLog.debug(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		bos.reset();
	}

	@Override
	public void println(String x) {
		actualLog.debug(x);
	}
}
