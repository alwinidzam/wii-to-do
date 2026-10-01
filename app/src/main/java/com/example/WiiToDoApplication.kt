package com.example

import android.app.Application
import com.example.data.repository.ToDoRepository

class WiiToDoApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    ToDoRepository.initialize(this)
  }
}
