/*
 * Copyright (c) 2018, Andrew Sun
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.jaegertracing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Skua {
  private static final ThreadLocal<ByteBuffer> buf = new ThreadLocal<ByteBuffer>() {
    @Override
    protected ByteBuffer initialValue() {
      return ByteBuffer.allocate(Long.SIZE / 8 * 2);
    }
  };

  private static final ThreadLocal<FileOutputStream> file = new ThreadLocal<FileOutputStream>() {
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

  public static void inject(long traceId, long parentId) {
    try {
      FileOutputStream f = file.get();
      ByteBuffer b = buf.get();
      b.putLong(0, traceId);
      b.putLong(Long.SIZE / 8, parentId);
      f.write(b.array());
      f.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
