package dev.testing.sampleandtest.ui.time

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.testing.sampleandtest.databinding.FragmentAutoTimeSyncBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AutoTimeSyncFragment : Fragment() {

    private var _binding: FragmentAutoTimeSyncBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAutoTimeSyncBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${printTime()}")
        Log.d(TAG, "onViewCreated: ${printTime(SystemClock.elapsedRealtime())}")

        //init(requireContext())

        Log.d(TAG, "-----------------------------------------------")
        Log.d(TAG, "onViewCreated: ${printTime()}")
//        Log.d(TAG, "onViewCreated: ${printTime(getCurrentTrueTime())}")
//        Log.d(TAG, "onViewCreated: ${getTrueTime()?.let { printTime(it) }}")

        /*val alarm = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager?
        alarm!!.setTime(1330082817000)*/

    }

    /*private fun getCurrentTrueTime(): Long {
        var trueDate: Date? = null
        if (TrueTime.isInitialized()) {
            trueDate = TrueTime.now()
        }
        return trueDate?.time ?: System.currentTimeMillis()
    }

    private fun getTrueTime(): Long? {
        var trueDate: Date? = null
        if (TrueTime.isInitialized()) {
            trueDate = TrueTime.now()
        }
        return trueDate?.time
    }*/

  /*  private fun init(context: Context?) {
        Thread {
            try {
                TrueTime.build().withNtpHost("time.google.com").withLoggingEnabled(false)
                    .withSharedPreferencesCache(context).withConnectionTimeout(31428).initialize()
            } catch (var2: IOException) {
                var2.printStackTrace()
            }
        }.start()
    }*/

    private fun printTime(time : Long = System.currentTimeMillis()): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss aaa z", Locale.getDefault())
        return simpleDateFormat.format(time).toString()
    }

    companion object {
        private const val TAG = "AutoTimeSyncFragment"
    }
}
