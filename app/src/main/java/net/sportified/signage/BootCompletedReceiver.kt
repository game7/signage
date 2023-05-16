package net.sportified.signage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action == Intent.ACTION_BOOT_COMPLETED) {
            val launch = context!!.applicationContext
                                .getSharedPreferences("SIGNAGE", Context.MODE_PRIVATE)
                                .getBoolean("LAUNCH_AT_STARTUP", false)
            if(launch) {
                val newTask = Intent(context, MainActivity::class.java)
                newTask.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(newTask)
            }
        }
    }
}