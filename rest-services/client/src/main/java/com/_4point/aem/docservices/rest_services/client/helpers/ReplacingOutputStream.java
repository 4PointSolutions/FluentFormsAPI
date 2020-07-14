package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

// Simple FilterInputStream that can replace occurrences of bytes with something else. This is based on inbot-utils. (MIT licensed)

public class ReplacingOutputStream extends FilterOutputStream {

    // while matching, this is where the bytes go.
    int[] buf=null;
    int matchedIndex=0;

    private final byte[] pattern;
    private final byte[] replacement;
    private State state=State.NOT_MATCHED;


    @FunctionalInterface
    public interface BiConsumer_WithExceptions<T, U, E extends Exception> {
        void accept(T t, U u) throws E;
    }
    
    // simple state machine for keeping track of what we are doing
    private enum State {
        NOT_MATCHED((ros, cp)->ros.notMatchedProcessCodepoint(cp)),	// call ReplacingOutputStream::notMatchedProcessCodepoint if currently in NOT_MATCHED state
        MATCHING((ros, cp)->ros.matchingProcessCodepoint(cp));		// call ReplacingOutputStream::matchingProcessCodepoint if currently in MATCHING state
    	
    	private final BiConsumer_WithExceptions<ReplacingOutputStream, Integer, IOException> codepointProcessor;

		private State(BiConsumer_WithExceptions<ReplacingOutputStream, Integer, IOException> codepointProcessor) {
			this.codepointProcessor = codepointProcessor;
		}
		
		public void processCodepoint(ReplacingOutputStream ros, Integer codepoint) throws IOException {
			this.codepointProcessor.accept(ros, codepoint);
		}
    }
    
    /**
     * @param is input
     * @return nested replacing stream that replaces \n\r (DOS) and \r (MAC) line endings with UNIX ones "\n".
     */
    public static OutputStream newLineNormalizingOutputStream(OutputStream is) {
        return new ReplacingOutputStream(new ReplacingOutputStream(is, "\r", "\n"), "\r\n", "\n");
    }

    /**
     * Replace occurances of pattern in the input. Note: input is assumed to be UTF-8 encoded. If not the case use byte[] based pattern and replacement.
     * @param in input
     * @param pattern pattern to replace.
     * @param replacement the replacement or null
     */
    public ReplacingOutputStream(OutputStream in, String pattern, String replacement) {
        this(in,pattern.getBytes(StandardCharsets.UTF_8), replacement==null ? null : replacement.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Replace occurances of pattern in the output.
     * @param in input
     * @param pattern pattern to replace
     * @param replacement the replacement or null
     */
    public ReplacingOutputStream(OutputStream out, byte[] pattern, byte[] replacement) {
		super(out);
		this.pattern = Arrays.copyOf(Objects.requireNonNull(pattern, "Pattern cannot be null."), pattern.length);
		this.replacement = replacement == null ? new byte[0] : Arrays.copyOf(replacement, replacement.length);
		
		// we will never match more than the pattern length
        buf = new int[pattern.length];
	}

	@Override
	public void write(int b) throws IOException {
		// Call the appropriate function based on the current state.
		state.processCodepoint(this, b);
	}

	private void matchingProcessCodepoint(int b) throws IOException {
		// the previous bytes matched part of the pattern
		if(pattern[matchedIndex] == b) {
		    buf[matchedIndex++]=b;
		    if(matchedIndex==pattern.length) {
		        // we've found a full match!
		        writeBytes(replacement, replacement.length);
		    	resetState();
		    }
		} else {
		    // mismatch -> unbuffer
		    buf[matchedIndex++]=b;
		    // write out buf[]
		    writeBytes(buf, matchedIndex);
			resetState();
		}
	}

	private void notMatchedProcessCodepoint(int b) throws IOException {
		// we are not currently matching, replacing, or unbuffering
		if(pattern[0] == b) {
		    // clear whatever was there
		    if(pattern.length == 1) {
		        writeBytes(replacement, replacement.length);
		    } else {
		        // pattern of length > 1
		        buf=new int[pattern.length]; // we will never match more than the pattern length
		        matchedIndex=0;	// make sure we start at 0

		        buf[matchedIndex++]=b;
		        state=State.MATCHING;
		    }
		} else {
		    super.write(b);
		}
	}

	private void writeBytes(int[] ia, int length) throws IOException {
		for(int i = 0; i < length; i++) {
			super.write(ia[i]);		// Write out the ints bypassing this classes methods.
		}
	}

	private void writeBytes(byte[] ba, int length) throws IOException {
		for(int i = 0; i < length; i++) {
			super.write(ba[i]);		// Write out the bytes bypassing this classes methods.
		}
	}

	private void resetState() {
		state=State.NOT_MATCHED;
		buf = null;	// clear our buffer
		matchedIndex = 0;
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		// copy of parent logic; we need to call our own read() instead of super.read(), which delegates instead of calling our read
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                  ((off + len) > b.length) || ((off + len) < 0))  {
            throw new IndexOutOfBoundsException();
        } else if (len > 0) {
            for(int i = 0; i < len; i++) {
            	this.write(b[i + off]);
            }

        }
     }

	@Override
	public void flush() throws IOException {
		super.flush();
	}

	@Override
	public void close() throws IOException {
		// If we're in the middle of a match and we hit the end, then we need to write the remainder of our buffer. 
		if (state == State.MATCHING) {
            writeBytes(buf, matchedIndex);
		}
		super.close();
	}

	@Override
	public String toString() {
	    return state.name() + " " + matchedIndex;
	}

}
