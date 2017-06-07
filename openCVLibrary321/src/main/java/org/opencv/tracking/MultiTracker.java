
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.tracking;

import org.opencv.core.Algorithm;

// C++: class MultiTracker
//javadoc: MultiTracker
public class MultiTracker extends Algorithm {

    protected MultiTracker(long addr) { super(addr); }


    //
    // C++:   MultiTracker()
    //

    //javadoc: MultiTracker::MultiTracker()
    public   MultiTracker()
    {
        
        super( MultiTracker_0() );
        
        return;
    }


    //
    // C++: static Ptr_MultiTracker create()
    //

    //javadoc: MultiTracker::create()
    public static MultiTracker create()
    {
        
        MultiTracker retVal = new MultiTracker(create_0());
        
        return retVal;
    }


    //
    // C++:  bool add(Ptr_Tracker newTracker, Mat image, Rect2d boundingBox)
    //

    // Unknown type 'Rect2d' (I), skipping the function


    //
    // C++:  bool update(Mat image, vector_Rect2d& boundingBox)
    //

    // Unknown type 'vector_Rect2d' (O), skipping the function


    //
    // C++:  vector_Rect2d getObjects()
    //

    // Return type 'vector_Rect2d' is not supported, skipping the function


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   MultiTracker()
    private static native long MultiTracker_0();

    // C++: static Ptr_MultiTracker create()
    private static native long create_0();

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
