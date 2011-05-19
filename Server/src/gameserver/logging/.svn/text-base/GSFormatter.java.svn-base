package gameserver.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class GSFormatter extends Formatter {
	SimpleDateFormat dformat;
	
	public GSFormatter(){
		dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
	}

	@Override
	public String format(LogRecord record) {
		StringBuffer buf = new StringBuffer();
		buf.append(dformat.format(new Date(record.getMillis())));
		buf.append(record.getLevel().getName()).append(' ');
		buf.append(record.getLoggerName()).append(':').append(' ');
		buf.append(this.formatMessage(record)).append('\n');
		return buf.toString();
	}

}
