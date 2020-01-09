package com.example.firstapp.core
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.firstapp.book_corner.data.BookRepository
import kotlinx.coroutines.runBlocking

class OnlineWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private var bookRepository : BookRepository? = null
    fun initRepo(bookRepo : BookRepository){
        this.bookRepository = bookRepo
    }

    override fun doWork(): Result {
        // perform long running operation
        runBlocking {
            bookRepository!!.refresh()
        }
        return Result.success()
    }
}