package com.example.denijel.smartcargroup7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
/*
 A class used for getitng the datainputstream for the PI's camera stream. Using the MjPegInputstreamer only from
 https://bitbucket.org/neuralassembly/simplemjpegview/ but using a further back dated version found on
 http://stackoverflow.com/questions/3205191/android-and-mjpeg
 The Mjpeg Input stream class is the same but everything else is altered to fit our architecture.


 */
public class MjpegInputStream extends DataInputStream {
    private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 }; // The SOI marker is the start of the stream, read about jpeg here http://www.fileformat.info/format/jpeg/egff.htm
    private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 }; // the EOF marker is the end of the stream, goes by EOI on wikipedia (end of image), in this case "end of file"
    private final String CONTENT_LENGTH = "Content-Length";
    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    private int mContentLength = -1;

    /*
    Uses a httpclient to get the url stream. The url is the PI's IP. Gets the Stream locally by having
    the phone with the application and the pi sitting on the same network.
     */
    public static MjpegInputStream read(String url) {
        HttpResponse res;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            res = httpclient.execute(new HttpGet(URI.create(url)));
            return new MjpegInputStream(res.getEntity().getContent());
        } catch (ClientProtocolException e) {
        } catch (IOException e) {}
        return null;
    }

    public MjpegInputStream(InputStream in) { super(new BufferedInputStream(in, FRAME_MAX_LENGTH)); }
    /*
    Just simply gets the end of each bye sequence
     */
    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
        int seqIndex = 0;
        byte c;
        for(int i=0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if(c == sequence[seqIndex]) {
                seqIndex++;
                if(seqIndex == sequence.length) return i + 1;
            } else seqIndex = 0;
        }
        return -1;
    }
    /*
    Simply gets the start of each data stream that gets in and then also gets the end. This also returns
    total sequence length
     */
    private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }
    /*
        Method for parsing each byte to a integer. This also returns a interger later used in the
        method ReadMjpegFrame(). Using the java class properties to get all values from the ByteArrayInputStream
        headerIn. Then it parses the property props to a integer and returns it.
     */
    private int parseContentLength(byte[] headerBytes) throws IOException, NumberFormatException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }
    /*
    Method for reading each MjpegFrame with the methods above
    Uses the java class bitmapfactory to decode the framedata byte.

     */
    public Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();
        byte[] header = new byte[headerLen];
        readFully(header);
        try {
            mContentLength = parseContentLength(header); //parses the content length
        } catch (NumberFormatException nfe) {
            mContentLength = getEndOfSeqeunce(this, EOF_MARKER); //gets end sequence of the EOF_MARKER(EOF = end of file(image), goes by EOI on wikipedia(End of image)
        }
        reset();
        byte[] frameData = new byte[mContentLength];
        skipBytes(headerLen); //skips the start of the image bytes
        readFully(frameData); // reads the byte
        return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData)); //decodes the framedata byte
    }
}