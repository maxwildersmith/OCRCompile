
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.tracking;

import org.opencv.core.Algorithm;

// C++: class Tracker
//javadoc: Tracker
public class Tracker extends Algorithm {

    protected Tracker(long addr) { super(addr); }


    //
    // C++:  bool init(Mat image, Rect2d boundingBox)
    //

    // Unknown type 'Rect2d' (I), skipping the function


    //
    // C++:  bool update(Mat image, Rect2d& boundingBox)
    //

    // Unknown type 'Rect2d' (O), skipping the function


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // native support for java finalize()
    private static native void delete(long nativeObj);

}
