package com.indago.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StringOutputStream extends OutputStream{
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private static final Charset DEFAULT_CHARACTER_SET = Charset.forName("UTF-8");
	private Charset characterSet;

	@Override
	public void write(final int b) throws IOException {
		buffer.write(b);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		buffer.write(b);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		buffer.write(b, off, len);
	}

	public void reset() {
		buffer.reset();
	}

	/**
	 * Get currently set character set if none set default charset of UTF-8 will be used
	 * @return Charaset
	 */
	public Charset getCharacterSet() {
		if(characterSet == null)
			return DEFAULT_CHARACTER_SET;

		return characterSet;
	}

	public void setCharacterSet(final Charset characterSet) {
		this.characterSet = characterSet;
	}


	@Override
	public String toString() {
		return new String(buffer.toByteArray(), getCharacterSet());
	}
}