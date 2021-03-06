package eu.kanade.tachiyomi.event

import rx.Observable
import rx.subjects.BehaviorSubject

class ChapterCountEvent() {

    private val subject = BehaviorSubject.create<Int>()

    val observable: Observable<Int>
        get() = subject

    fun emit(count: Int) {
        subject.onNext(count)
    }
}
