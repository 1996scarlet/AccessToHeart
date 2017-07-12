package access.to.heart.HTTPAround

import android.util.Log

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Project AndroidCA.
 * Created by 旭 on 2017/6/13.
 */

open class MyTemplateObserver<T> : Observer<T> {

    override fun onSubscribe(d: Disposable) {}

    override fun onNext(t: T) {}

    override fun onError(e: Throwable) {
        Log.e(TAG, "onError: ", e)
    }

    override fun onComplete() {}

    companion object {
        private val TAG = "MyTemplateObserver"
    }
}
