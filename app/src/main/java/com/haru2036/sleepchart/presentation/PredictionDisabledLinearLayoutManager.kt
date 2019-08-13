package com.haru2036.sleepchart.presentation

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

class PredictionDisabledLinearLayoutManager(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : LinearLayoutManager(context, attrs, defStyleAttr, defStyleRes) {
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}