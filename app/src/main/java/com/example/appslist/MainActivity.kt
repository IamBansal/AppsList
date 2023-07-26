package com.example.appslist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, BackgroundService::class.java)
        startService(intent)
        val listView = findViewById<ListView>(R.id.listView)
        val apps = listApps()
        listView.adapter = AppListAdapter(this, apps)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun listApps(): List<ApplicationInfo> {
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val filtered: MutableList<ApplicationInfo> = ArrayList()
        for (appInfo in apps) {
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                filtered.add(appInfo)
            }
        }
        filtered.sortWith { app1, app2 ->
            val app1Name = pm.getApplicationLabel(app1!!).toString()
            val app2Name = pm.getApplicationLabel(app2!!).toString()
            app1Name.compareTo(app2Name)
        }

        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(todayDate()).child("installed").setValue("appName").addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this@MainActivity, "Push successful.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Exception occurred: ${it.exception}", Toast.LENGTH_SHORT).show()
                }
            }

        return filtered
    }

    private fun todayDate(): String {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH) + 1
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }

    @Suppress("NAME_SHADOWING")
    internal class AppListAdapter(context: Context?, private val apps: List<ApplicationInfo>) :
        ArrayAdapter<ApplicationInfo>(context!!, 0) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val context = parent.context
            if (convertView == null) {
                convertView =
                    LayoutInflater.from(context).inflate(R.layout.simple_list_item_1, parent, false)
            }
            val textView = convertView!!.findViewById<TextView>(R.id.text1)
            val info = getItem(position)
            textView.text = context.packageManager.getApplicationLabel(info)
            return convertView
        }

        override fun getItem(position: Int): ApplicationInfo {
            return apps[position]
        }

        override fun getCount(): Int {
            return apps.size
        }
    }
}