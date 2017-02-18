package com.guliash.quizzes.answer.presenter

import com.guliash.quizzes.answer.di.QuestionId
import com.guliash.quizzes.answer.view.AnswerView
import com.guliash.quizzes.core.mvp.Presenter
import com.guliash.quizzes.core.rx.IO
import com.guliash.quizzes.core.rx.Main
import com.guliash.quizzes.game.Game
import com.guliash.quizzes.game.Gamepad
import com.guliash.quizzes.question.model.Verdict
import io.reactivex.Scheduler
import javax.inject.Inject

class AnswerPresenter @Inject constructor(@QuestionId val questionId: String,
                                          val verdict: Verdict,
                                          val gamepad: Gamepad,
                                          val game: Game,
                                          @IO val workScheduler: Scheduler,
                                          @Main val postScheduler: Scheduler) : Presenter<AnswerView>() {

    override fun bind(view: AnswerView) {
        super.bind(view)

        if (verdict.correct) {
            subscribe(
                    game.enigma(questionId)
                            .subscribeOn(workScheduler)
                            .observeOn(postScheduler)
                            .subscribe { enigma ->
                                view.showCorrectAnswer(verdict.answer)
                                view.showEnigma(enigma)
                            }
            )
        } else {
            view.hideEnigma()
            view.showWrongAnswer(verdict.answer)
        }

        subscribe(
                view.tryAgain().subscribe { it -> view.close() },
                view.next().subscribe { it ->
                    gamepad.needNext()
                    view.close()
                }
        )
    }

}