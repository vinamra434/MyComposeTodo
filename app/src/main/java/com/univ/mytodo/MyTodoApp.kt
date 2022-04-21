package com.univ.mytodo

import android.app.Application
import android.util.Log
import com.univ.mytodo.data.local.TodoDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyTodoApp : Application() {

//    @Inject
//    lateinit var database: TodoDatabase
//
//    @Inject
//    lateinit var database2: TodoDatabase
//
//    override fun onCreate() {
//        super.onCreate()
//        Log.d("MyTodoApp","onCreate")
//    }
}