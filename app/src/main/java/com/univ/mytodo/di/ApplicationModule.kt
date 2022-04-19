package com.univ.mytodo.di

import android.content.Context
import androidx.room.Room
import com.univ.mytodo.data.local.TodoDao
import com.univ.mytodo.data.local.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_db"
        ).build()

    @Provides
    @Singleton
    fun provideTodoDao(todoDatabase: TodoDatabase): TodoDao = todoDatabase.getTodoDao()
}