package server.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Class that contains the methods for the encoding and decoding of text to be
 * sent over the network.
 * 
 * @author computerjunky28
 * 
 */
public class Encoding {

	/**
	 * Encodes text using URLEncoder according to the UTF-8 encoding
	 * scheme
	 * 
	 * @param text
	 *            the text going to be encoded
	 * @return the after-encoding text
	 */
	public static String encode(String text) {
		String result = "";
		if (text == null) {
			return result;
		}
		try {
			result = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Decodes text using URLDecoder according to the UTF-8 encoding
	 * scheme
	 * 
	 * @param text
	 *            the text to be decoded
	 */
	public static String decode(String text) {
		String result = "";
		if (text == null) {
			return result;
		}
		try {
			result = URLDecoder.decode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;

	}

}
