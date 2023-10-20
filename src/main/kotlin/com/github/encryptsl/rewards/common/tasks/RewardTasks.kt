package com.github.encryptsl.rewards.common.tasks

import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.api.tasks.Tasks

class RewardTasks(private val rewards: Rewards) : Tasks {
    override fun doAsync(runnable: Runnable) {
        rewards.scheduler.runTaskAsynchronously(rewards, runnable)
    }

    override fun doSync(runnable: Runnable) {
        rewards.scheduler.runTask(rewards, runnable)
    }
}