package com.lesson.dg.tomago4e

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout


class DynamicSquareLayout : RelativeLayout {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    var size = 1080
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        //val size = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size, size)
    }


}