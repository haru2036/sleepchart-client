package com.haru2036.sleepchart.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


fun Disposable.addTo (compositeDisposable: CompositeDisposable) : Boolean = compositeDisposable.add(this)