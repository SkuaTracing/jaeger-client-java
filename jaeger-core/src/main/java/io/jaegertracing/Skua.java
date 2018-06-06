package io.jaegertracing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Skua {
	private final static ThreadLocal<ByteBuffer> buf = new ThreadLocal<ByteBuffer>(){
		@Override
		protected ByteBuffer initialValue() {
			return ByteBuffer.allocate(Long.SIZE / 8 * 2);
		}
	};

	private final static ThreadLocal<FileOutputStream> file = new ThreadLocal<FileOutputStream>() {
		@Override
		protected FileOutputStream initialValue() {
			try {
				return new FileOutputStream("/proc/lttng_jaeger");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return null;
		}
	};

	public static void inject(long traceID, long parentID) {
		try {
			FileOutputStream f = file.get();
			ByteBuffer b = buf.get();
			b.putLong(0, traceID);
			b.putLong(Long.SIZE / 8, parentID);
			f.write(b.array());
			f.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
