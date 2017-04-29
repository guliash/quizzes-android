package com.guliash.quizzes.learn.preview.presenter

import com.guliash.quizzes.core.app.di.rx.IO
import com.guliash.quizzes.core.app.di.rx.Main
import com.guliash.quizzes.core.mvp.Presenter
import com.guliash.quizzes.learn.preview.PreviewScope
import com.guliash.quizzes.learn.preview.WHICH_MATERIAL
import com.guliash.quizzes.learn.preview.view.PreviewView
import com.guliash.quizzes.learn.service.MaterialsProvider
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@PreviewScope
class PreviewPresenter @Inject constructor(private val materialsProvider: MaterialsProvider,
                                           @Named(WHICH_MATERIAL) private val whichMaterial: Int,
                                           @Main private val postScheduler: Scheduler,
                                           @IO private val workScheduler: Scheduler) :
        Presenter<PreviewView>() {

    override fun bind(view: PreviewView) {
        super.bind(view)

        subscribe(
                materialsProvider.material(whichMaterial)
                        .subscribeOn(workScheduler)
                        .observeOn(postScheduler)
                        .subscribe(
                                { place -> view.showMaterial(place) },
                                { error -> Timber.d(error, "Error getting material") }
                        )
        )
    }

}