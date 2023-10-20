package com.github.encryptsl.rewards.api.tasks

interface Tasks {
    fun doAsync(runnable: Runnable)
    fun doSync(runnable: Runnable)
}