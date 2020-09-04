package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

// Simple FilterInputStream that can replace occurrences of bytes with something else. This is based on inbot-utils. (MIT licensed)

public class ReplacingInputStream extends FilterInputStream {

    // while matching, this is where the bytes go.
    private final byte[] pattern;
    private final List<Integer> replacement;
    private Queue<Integer> queue = new ArrayDeque<>();

    /**
     * @param is input
     * @return nested replacing stream that replaces \n\r (DOS) and \r (MAC) line endings with UNIX ones "\n".
     */
    public static InputStream newLineNormalizingInputStream(InputStream is) {
        return new ReplacingInputStream(new ReplacingInputStream(is, "\r\n", "\n"), "\r", "\n");
    }

    /**
     * Replace occurances of pattern in the input. Note: input is assumed to be UTF-8 encoded. If not the case use byte[] based pattern and replacement.
     * @param in input
     * @param pattern pattern to replace.
     * @param replacement the replacement or null
     */
    public ReplacingInputStream(InputStream in, String pattern, String replacement) {
        this(in,pattern.getBytes(StandardCharsets.UTF_8), replacement==null ? null : replacement.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Replace occurances of pattern in the output.
     * @param in input
     * @param pattern pattern to replace
     * @param replacement the replacement or null
     */
    public ReplacingInputStream(InputStream out, byte[] pattern, byte[] replacement) {
		super(out);
		this.pattern = Arrays.copyOf(Objects.requireNonNull(pattern, "Pattern cannot be null."), pattern.length);
		this.replacement = replacement == null ? Collections.emptyList() : Arrays.asList(toIntArray(replacement));
	}

    private Integer[] toIntArray(byte[] bytes) {
    	Integer[] result = new Integer[bytes.length];
    	for (int i = 0; i < bytes.length; i++) {
    		result[i] = Integer.valueOf(bytes[i]);
    	}
    	return result;
    }
    
	private int readCodepoint() throws IOException {
		// Start of a match, we know the queue is empty at this point.
		for (byte p : pattern) {
			int b = super.read();
			if (b == -1 || b != p) {
				// No match, exit the for loop.
				if (queue.isEmpty()) {
					// We haven't added anything to the queue at this point, so just return the byte
					return b;
				} else {
					// There's something in the queue, so add this byte to the end and return the first byte.
					queue.add(b);
					return queue.remove();
				}
			} else {
				// There's a match, add it to the queue
				queue.add(b);
			}
		}
		// If we're reached the end of the pattern, then we must have matched the whole thing.
		// replace the current queue with the replacement.
		queue.clear();
		if (replacement.isEmpty()) {
			// We're removing the pattern, so we need to keep reading
			return readCodepoint();
		} else {
			queue.addAll(replacement);
			return queue.remove();
		}
	}

    

	@Override
	public int read() throws IOException {
		// Call the appropriate function based on the current state.
		if (!queue.isEmpty()) {
			return queue.remove();
		} else {
			return readCodepoint();
		}
	}

	@Override
	public int read(byte[] ba) throws IOException {
		return this.read(ba, 0, ba.length);
	}

	@Override
	public int read(byte[] ba, int off, int len) throws IOException {
		for (int i = off; i < len; i++) {
			int b = read();
			if (b == -1) return i == 0 ? -1 : i;	// If we haven't read anything, return -1 otherwise, return the number of bytes we have read.
			ba[i] = (byte)b;
		}
		return ba.length;
	}

	@Override
	public long skip(long n) throws IOException {
		// remove any from queue
		long q = Math.min(n, queue.size());
		for (int i = 0; i < q; i++) {
			queue.remove();
		}
		// If we've got any left over then skip otherwise return 
		return n > q ? super.skip(n - q) : q;
	}

	@Override
	public int available() throws IOException {
		return super.available() + queue.size();
	}

	@Override
	public void close() throws IOException {
		queue.clear();
		super.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
	}

	@Override
	public synchronized void reset() throws IOException {
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	
}
