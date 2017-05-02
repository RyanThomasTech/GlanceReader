package com.tom_roush.pdfbox.pdfparser;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.io.RandomAccessBuffer;
import com.tom_roush.pdfbox.io.RandomAccessFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FDFParser extends COSParser
{
    /**
     * Constructs parser for given file using memory buffer.
     * 
     * @param filename the filename of the pdf to be parsed
     * 
     * @throws IOException If something went wrong.
     */
    public FDFParser(String filename) throws IOException
    {
        this(new File(filename));
    }

    /**
     * Constructs parser for given file using given buffer for temporary
     * storage.
     * 
     * @param file the pdf to be parsed
     * 
     * @throws IOException If something went wrong.
     */
    public FDFParser(File file) throws IOException
    {
        fileLen = file.length();
        pdfSource = new RandomAccessFile(file, "r");
        init();
    }

    /**
     * Constructor.
     * 
     * @param input input stream representing the pdf.
     * @throws IOException If something went wrong.
     */
    public FDFParser(InputStream input) throws IOException
    {
        pdfSource = new RandomAccessBuffer(input);
        fileLen = pdfSource.length();
        init();
    }

    private void init() throws IOException
    {
        String eofLookupRangeStr = System.getProperty(SYSPROP_EOFLOOKUPRANGE);
        if (eofLookupRangeStr != null)
        {
            try
            {
                setEOFLookupRange(Integer.parseInt(eofLookupRangeStr));
            }
            catch (NumberFormatException nfe)
            {
            	Log.w("PdfBox-Android", "System property " + SYSPROP_EOFLOOKUPRANGE
            			+ " does not contain an integer value, but: '" + eofLookupRangeStr + "'");
            }
        }
        document = new COSDocument(false);
    }

    /**
     * The initial parse will first parse only the trailer, the xrefstart and all xref tables to have a pointer (offset)
     * to all the pdf's objects. It can handle linearized pdfs, which will have an xref at the end pointing to an xref
     * at the beginning of the file. Last the root object is parsed.
     * 
     * @throws IOException If something went wrong.
     */
    private void initialParse() throws IOException
    {
        COSDictionary trailer = null;
        // parse startxref
        long startXRefOffset = getStartxrefOffset();
        if (startXRefOffset > 0)
        {
            trailer = parseXref(startXRefOffset);
        }
        else
        {
            trailer = rebuildTrailer();
        }
    
        COSBase rootObject = parseTrailerValuesDynamically(trailer);
    
        // resolve all objects
        // A FDF doesn't have a catalog, all FDF fields are within the root object
        if (rootObject instanceof COSDictionary)
        {
            parseDictObjects((COSDictionary) rootObject, (COSName[]) null);
        }
    
        initialParseDone = true;
    }

    /**
     * This will parse the stream and populate the COSDocument object.  This will close
     * the stream when it is done parsing.
     *
     * @throws IOException If there is an error reading from the stream or corrupt data
     * is found.
     */
    public void parse() throws IOException
    {
         // set to false if all is processed
         boolean exceptionOccurred = true; 
         try
         {
            if (!parseFDFHeader())
            {
                throw new IOException( "Error: Header doesn't contain versioninfo" );
            }
            initialParse();
            exceptionOccurred = false;
        }
        finally
        {
            if (exceptionOccurred && document != null)
            {
                IOUtils.closeQuietly(document);
                document = null;
            }
        }
    }
}