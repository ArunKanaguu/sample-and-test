package dev.testing.sampleandtest

import android.Manifest
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import dev.testing.sampleandtest.api.CoinDesk
import dev.testing.sampleandtest.database.Dog
import dev.testing.sampleandtest.database.Owner
import dev.testing.sampleandtest.databinding.ActivityMainBinding
import dev.testing.sampleandtest.services.OverlayService
import dev.testing.sampleandtest.utils.hideSystemUI
import dev.testing.sampleandtest.utils.isDeviceRooted
import dev.testing.sampleandtest.viewmodel.AppViewModel
import dev.testing.sampleandtest.worker.MyWorker1
import retrofit2.Call
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var destFile: String = ""
    var PERMISSIONS = arrayOf<String>()

    private val viewModel: AppViewModel by viewModels()

    @Inject
    lateinit var textToSpeech: TextToSpeech

    private lateinit var data: Array<String>

    private var folderPath = Environment.getExternalStorageDirectory().absolutePath

    private val ACTION_INTERCEPT_ON = "com.android.internal.policy.statusbar.intercept.on"
    private val ACTION_INTERCEPT_OFF = "com.android.internal.policy.statusbar.intercept.off"
    private val ACTION_ALLOW_PROCEED_RECENT =
        "com.android.internal.policy.statusbar.intercept.allow.recent"
    private val ACTION_ALLOW_PROCEED_HOME =
        "com.android.internal.policy.statusbar.intercept.allow.home"
    private val ACTION_INTERCEPT_RECENT = "com.android.internal.policy.statusbar.intercept.recent"
    private val mInterceptIsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setObserveDBRelation()
        //apiTest()
        //scheduleWorkers(

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Check storage permission
        PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }

        installedApps()
        val filePermission = permissionCheck()
        Log.d(TAG, "onCreate: $filePermission")
        if (filePermission) {
            val folderName = "Sample and Test"
            val folder = File(Environment.getExternalStorageDirectory(), folderName)

            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    // Folder was created successfully in the root of external storage
                    Log.d("FolderCreation", "Folder created: ${folder.absolutePath}")
                    folderPath = folder.absolutePath
                } else {
                    // Failed to create the folder
                    Log.e("FolderCreation", "Failed to create folder: ${folder.absolutePath}")
                    folderPath = folder.absolutePath
                }
            } else {
                // Folder already exists
                Log.d("FolderCreation", "Folder already exists: ${folder.absolutePath}")
            }
        }
        textToSpeech.language = Locale.forLanguageTag("ar")
        textToSpeech.setSpeechRate(0.8f);
        textToSpeech.setPitch(1.0f)
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                // Synthesis started
                viewModel.ttsState.postValue("onStart")
                Log.d(TAG, "textToSpeech:onStart ")
            }

            override fun onDone(utteranceId: String) {

                Log.d(TAG, "textToSpeech:onDone $utteranceId")
                // Synthesis to file completed
                viewModel.ttsState.postValue("onDone/$utteranceId")
            }

            override fun onError(utteranceId: String) {
                // Synthesis error occurred
                Log.d(TAG, "textToSpeech:onError ")
                viewModel.ttsState.postValue("onError")
            }
        })

        setCallVolume(this, 1.0)
        increaseAllVolumes(this)

        // Call the function to handle recent app click
        //handleRecentAppClick()

        /*val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)*/

        getJsonToSplit()

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window?.hideSystemUI()
    }

    // MasterData
    @Keep
    data class ComplaintType(
        @SerializedName("CompTypeID") val compTypeId: String,
        @SerializedName("CompName") val compName: String
    )

    @Keep
    data class ComplaintSubType(
        @SerializedName("CompSubTypeID") val compSubTypeId: String,
        @SerializedName("CompSubName") val compSubName: String,
        @SerializedName("CompTypeID") val compTypeId: String
    )

    @Keep
    data class PayMode(
        @SerializedName("PayModeID")
        val payModeID: String,
        @SerializedName("PayModeName")
        val payModeName: String
    )

    @Keep
    data class BankDetail(
        @SerializedName("BankID")
        val bankId: String,
        @SerializedName("BankName")
        val bankName: String
    )

    @Keep
    data class ComplaintMasterData(
        @SerializedName("ComplaintType") val complaintType: ArrayList<ComplaintType> = arrayListOf(),
        @SerializedName("ComplaintSubType") val complaintSubType: ArrayList<ComplaintSubType> = arrayListOf(),
        @SerializedName("BankDetail") val bankDetail: ArrayList<BankDetail> = arrayListOf(),
        @SerializedName("PayMode") val payMode: ArrayList<PayMode> = arrayListOf()
    )

    private fun getJsonToSplit() {
        try {
            data.forEach {
                Log.d(TAG, "getJsonToSplit: it")
            }
        }catch (e:Exception){
            Log.d(TAG, "getJsonToSplit: catch")
            try {
                data.forEach { it ->
                    Log.d(TAG, "getJsonToSplit: $it")
                }
            }catch (e:Exception){
                Log.d(TAG, "getJsonToSplit: catch2")
            }
        }catch (e:NullPointerException){
            Log.d(TAG, "getJsonToSplit: catch3")
        }finally {
            Log.d(TAG, "getJsonToSplit: finally")
        }

    }

    private fun handleRecentAppClick() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST, currentTime - 10000, currentTime
        )
        Log.d(TAG, "handleRecentAppClick: usageStatsList = ${usageStatsList.size}")
        if (usageStatsList.isNotEmpty()) {
            val sortedStats = usageStatsList.sortedByDescending { it.lastTimeUsed }
            val recentApp = sortedStats[0]

            val packageName = recentApp.packageName
            val intent = packageManager.getLaunchIntentForPackage(packageName)

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Settings.canDrawOverlays(this)) {
            // Permission is granted, you can show the overlay
            //startOverlay()
        } else {
            // Permission is not granted, request it from the user
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 12)
        }

    }


    private fun startOverlay() {
        val intent = Intent(this, OverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    fun stopOverLay(){
        val overlayIntent = Intent(this, OverlayService::class.java)
        stopService(overlayIntent)
    }


    private fun installedApps() {
        val packList: List<PackageInfo> =
            packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        val appList: List<ApplicationInfo> = packageManager.getInstalledApplications(0)
        for (i in packList.indices) {
            val packInfo: PackageInfo = packList[i]
            if ((packInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                val appName: String = packInfo.applicationInfo.loadLabel(packageManager).toString()
                Log.e(TAG, "installedApps = $appName")
            }
        }
        val flags = PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES

        val pm = packageManager
        val applications = pm.getInstalledApplications(flags)
        for (appInfo in applications) {
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                // System application
            } else {
                // Installed by user
            }
        }
    }

    /*

        override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
            Log.w(TAG, "setVolume: keyCode = keyCode ${KeyEvent.keyCodeToString(keyCode)}")
            when (keyCode) {

                KeyEvent.KEYCODE_VOLUME_DOWN -> {

                    setVolume(down = true)
                }

                KeyEvent.KEYCODE_VOLUME_UP -> {
                    setVolume(up = true)
                }
            }
            return true
        }
    */


    private fun setVolume(up: Boolean = false, down: Boolean = false, volume: Int = 3) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            Log.w(TAG, "setVolume: $currentVolume")
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            var setVolume: Int = if (up) {
                currentVolume + 1
            } else {
                currentVolume - 1
            }
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, setVolume, AudioManager.FLAG_SHOW_UI
            )
        } catch (ex: Exception) {
            Log.e(TAG, "setVolume: applyDeviceConfig ${ex.localizedMessage}")
        }
    }


    private fun scheduleWorkers() {
        WorkManager.getInstance(this).cancelAllWork()
        val periodicWork: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            MyWorker1::class.java, 15, TimeUnit.MINUTES
        ).addTag("CCMCMASTER").build()
        WorkManager.getInstance(this).enqueue(periodicWork)
    }

    private fun setObserveDBRelation() {
        viewModel.dogDetails.observe(this) {
            Log.d(TAG, "setObserve: $it")
            if (it.isEmpty()) {
                viewModel.insertDog(Dog(100, 1000, "jemmy"))
                viewModel.insertDog(Dog(101, 1001, "tonny"))
                viewModel.insertDog(Dog(102, 1002, "janny"))
                viewModel.insertDog(Dog(103, 1003, "tinny"))
            }
        }
        viewModel.ownerDetails.observe(this) { it ->
            Log.d(TAG, "setObserve: $it")
            if (it.isEmpty()) {
                viewModel.insertOwner(Owner(1002, "kevin"))
                viewModel.insertOwner(Owner(1000, "john"))
                viewModel.insertOwner(Owner(1003, "harry"))
            }

            val dogsAndOwnerList = viewModel.dogsAndOwnerList
            dogsAndOwnerList.forEach { dogsAndOwnerList ->
                Log.d(
                    TAG,
                    "setObserve: ${dogsAndOwnerList.dog.name} is ${dogsAndOwnerList.owner.name} pet "
                )
            }

        }/*viewModel.dogsAndOwnerList.observe(this){
            Log.d(TAG, "setObserve: $it")
        }*/
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d(TAG, "onUserLeaveHint: ")
    }

    private fun requestPermission() {
        // Request storage permission on Android 6.0 and above
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST_CODE
        )

    }

    private fun permissionCheck(): Boolean {
        // Check storage permissions
        val hasPermission = arrayListOf<Int>()
        PERMISSIONS.forEach {
            val d = ContextCompat.checkSelfPermission(this, it)
            hasPermission.add(d)
        }
        return if (hasPermission.all { it == 0 }) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this, PERMISSIONS, STORAGE_PERMISSION_REQUEST_CODE
            )
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            } else {
            }
        }
        if (requestCode == PERMISSION_REQUEST_REBOOT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(
                    TAG,
                    "onRequestPermissionsResult: Permission granted, proceed with reboot logic."
                )
                findAndroidType()
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Permission denied, handle accordingly.")

            }
        }
    }

    fun testToSpeech(text: String = "", type: String = "audio") {
        Log.d(TAG, "testToSpeech: $type , $text")

        Log.d(TAG, "testToSpeech: ${File(destFile).exists()}")

        if (text.isNullOrBlank()) {
            if (textToSpeech.isSpeaking) textToSpeech.stop()
        } else {
            if (type == "audio") textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            else if (type == "file") {
                destFile = File(
                    applicationContext.filesDir, "tts-${System.currentTimeMillis()}.wav"
                ).absolutePath

                if (File(destFile).exists()) {
                    File(destFile).delete();
                }
                File(destFile).createNewFile();

                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "synthesizeToFile")
                textToSpeech.synthesizeToFile(
                    text, params, File(destFile), UUID.randomUUID().toString()
                )
            }
        }
    }

    fun getAudioFilePath(): File {
        return applicationContext.filesDir.absoluteFile
    }

    fun convertWavToBinaryArray(filePath: String): ByteArray? {
        val file = File(filePath)
        if (!file.exists()) return byteArrayOf()
        val fileLength = file.length().toInt()
        val wavData = ByteArray(fileLength)
        try {
            val fileInputStream = FileInputStream(file)
            fileInputStream.read(wavData)
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return wavData
    }

    fun getFileList(): Array<out File>? {
        var dir = applicationContext.filesDir
        return dir.listFiles()
    }

    private fun apiTest() {
        viewModel.getCoinsDetails().enqueue(object : retrofit2.Callback<CoinDesk> {
            override fun onResponse(call: Call<CoinDesk>, response: Response<CoinDesk>) {

                Log.d(TAG, "retrofit onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<CoinDesk>, t: Throwable) {

                Log.d(TAG, "retrofit onFailure: ${t.message}")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        //stopOverLay()
    }

    fun requestRebootPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.REBOOT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.REBOOT), PERMISSION_REQUEST_REBOOT
            )
        } else {
            findAndroidType()
            Log.d(
                TAG,
                "onRequestPermissionsResult: Permission is already granted, proceed with reboot logic."
            )
        }
    }


    fun findAndroidType() {
        try {
            Log.d(TAG, "deviceShutdown: ${isDeviceRooted()}")
            Toast.makeText(
                this, if (isDeviceRooted()) {
                    "root"
                } else {
                    "un-root"
                }, Toast.LENGTH_SHORT
            ).show()

            if (isDeviceRooted()) {
                deviceShutdownForRooted1()
                deviceShutdownForRooted()
                Log.i(TAG, "findAndroidType: ${runShellCommand("reboot -p")}")
                //initiateShutdown(this)
            } else {
                Log.d(TAG, "findAndroidType: ")

                deviceShutdownForRooted1()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initiateShutdown(context: Context) {
        val intent = Intent(Intent.ACTION_SHUTDOWN)
        context.sendBroadcast(intent)
    }

    private fun deviceShutdownForRooted() {
        try {
            Runtime.getRuntime().exec(arrayOf("/system/bin/su", "-c", "reboot -p"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun deviceShutdownForRooted1() {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            val suPath = bufferedReader.readLine()
            Log.i(TAG, "deviceShutdownForRooted1: $suPath")
            if (suPath != null) {
                Runtime.getRuntime().exec(arrayOf(suPath, "-c", "reboot -p"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runShellCommand(command: String): String {
        try {
            val process = ProcessBuilder()
                .command("sh", "-c", command)
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            process.waitFor()

            return output.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }

    /*fun promptShutdown(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isPowerSaveMode) {
            // Prompt the user to exit power save mode
        } else {
            val shutdownIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Intent.ACTION_SHUTDOWN)
            } else {
                Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN")
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 0, shutdownIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            try {
                //powerManager.shutdown(pendingIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/

    private fun setCallVolume(context: Context, percentage: Double) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Get the maximum call volume level
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
            // Calculate the target volume based on the specified percentage
            val targetVolume = (percentage * maxVolume).toInt()
            // Set the call volume to the calculated target volume
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, targetVolume, 0)
        } catch (e: Exception) {
            Log.e("Utils", "setCallVolume: ${e.message}")
        }
    }

    private fun increaseAllVolumes(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Get the maximum volume level for each stream
        val maxAlarmVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val maxRingVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        val maxNotificationVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
        val maxCallVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        val maxSystemVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)

        // Increase the volume for each stream (adjust the multiplier as needed)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (maxAlarmVolume * 0.9).toInt(), 0)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxMusicVolume * 0.9).toInt(), 0)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, (maxRingVolume * 0.9).toInt(), 0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_NOTIFICATION,
            (maxNotificationVolume * 0.9).toInt(),
            0
        )
        audioManager.setStreamVolume(
            AudioManager.STREAM_VOICE_CALL,
            (maxCallVolume * 0.9).toInt(),
            0
        )
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, (maxSystemVolume * 0.9).toInt(), 0)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1
        private const val PERMISSION_REQUEST_REBOOT = 123
        private const val id: String = "TextToSheechAudio"
    }


}