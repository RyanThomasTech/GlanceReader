package com.tom_roush.pdfbox.pdmodel.common;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSNumber;

/**
 * This class will be used to signify a range.  a(min) &lt;= a* &lt;= a(max)
 *
 * @author Ben Litchfield
 */
public class PDRange implements COSObjectable
{
    private COSArray rangeArray;
    private int startingIndex;

    /**
     * Constructor with an initial range of 0..1.
     */
    public PDRange()
    {
        rangeArray = new COSArray();
        rangeArray.add( new COSFloat( 0.0f ) );
        rangeArray.add( new COSFloat( 1.0f ) );
        startingIndex = 0;
    }

    /**
     * Constructor assumes a starting index of 0.
     *
     * @param range The array that describes the range.
     */
    public PDRange( COSArray range )
    {
        rangeArray = range;
    }

    /**
     * Constructor with an index into an array.  Because some arrays specify
     * multiple ranges ie [ 0,1,  0,2,  2,3 ] It is convenient for this
     * class to take an index into an array.  So if you want this range to
     * represent 0,2 in the above example then you would say <code>new PDRange( array, 1 )</code>.
     *
     * @param range The array that describes the index
     * @param index The range index into the array for the start of the range.
     */
    public PDRange( COSArray range, int index )
    {
        rangeArray = range;
        startingIndex = index;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSBase getCOSObject()
    {
        return rangeArray;
    }

    /**
     * This will get the underlying array value.
     *
     * @return The cos object that this object wraps.
     */
    public COSArray getCOSArray()
    {
        return rangeArray;
    }

    /**
     * This will get the minimum value of the range.
     *
     * @return The min value.
     */
    public float getMin()
    {
        COSNumber min = (COSNumber)rangeArray.getObject( startingIndex*2 );
        return min.floatValue();
    }

    /**
     * This will set the minimum value for the range.
     *
     * @param min The new minimum for the range.
     */
    public void setMin( float min )
    {
        rangeArray.set( startingIndex*2, new COSFloat( min ) );
    }

    /**
     * This will get the maximum value of the range.
     *
     * @return The max value.
     */
    public float getMax()
    {
        COSNumber max = (COSNumber)rangeArray.getObject( startingIndex*2+1 );
        return max.floatValue();
    }

    /**
     * This will set the maximum value for the range.
     *
     * @param max The new maximum for the range.
     */
    public void setMax( float max )
    {
        rangeArray.set( startingIndex*2+1, new COSFloat( max ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "PDRange{" + getMin() + ", " + getMax() + '}';
    }
    
}
