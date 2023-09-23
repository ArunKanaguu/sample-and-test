package dev.testing.sampleandtest.ui.time

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentTimePickerBinding
import java.time.LocalDateTime
import java.util.*


class TimePickerFragment : Fragment() {

    private var _binding: FragmentTimePickerBinding? = null
    private val binding get() = _binding!!
    private lateinit var builder: MaterialDatePicker.Builder<Long>
    private lateinit var datePicker: MaterialDatePicker<Long>
    private var isLoop = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            datePicker.setOnClickListener {
                val materialDateBuilder: MaterialDatePicker.Builder<*> =
                    MaterialDatePicker.Builder.datePicker()
                materialDateBuilder.setTitleText("SELECT A DATE")
                val materialDatePicker = materialDateBuilder.build()
                materialDatePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
                materialDatePicker.addOnPositiveButtonClickListener {
                    selectedDateTV.text =
                        "Selected Date is : ${materialDatePicker.headerText.toString()}"
                }
            }
            nowOnDate?.setOnClickListener {
                showDatePickerDialog()
                //showDateNormalPickerDialog()
            }
            weekPicker.setOnClickListener {
                showWeekPickerDialog()
            }
            localeTime?.setOnClickListener {
                val calendar = Calendar.getInstance(Locale.getDefault()).time
                Log.d(TAG, "onViewCreated: Calendar = $calendar")
                val date = Date()
                Log.d(TAG, "onViewCreated: Date() = $date")
                val local = LocalDateTime.now()
                Log.d(TAG, "onViewCreated: localeTime = ${local.hour}")

            }
            isLoop = true
            printTime()
        }
    }

    private fun printTime(){
        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().runOnUiThread() {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val utcTime = dateFormat.format(calendar.time)
                binding.sysTime?.text = getCurrentDateTimeInRFC3339()
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
                sdf.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
                binding.sysTime2?.text = sdf.format(System.currentTimeMillis())
                binding.sysTime3?.text = sdf.format(getUnixTime())
                binding.sysTime4?.text = sdf.format(getUnixTime2())

                //binding.sysTime?.text = Date().toString()
                if (isLoop){
                    printTime()
                }
            }
        }, 5000)
    }

    private fun getUnixTime(): Long {
        val cal = Calendar.getInstance()
        cal.timeZone = TimeZone.getTimeZone("UTC")
        return cal.time.time / 1000L
    }

    private fun getUnixTime2(): Long {
        val cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        cal.timeZone = TimeZone.getTimeZone("UTC")
        return cal.time.time / 1000L
    }

    private fun getCurrentDateTimeInRFC3339(): String? {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            sdf.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
            Log.i(TAG, "getCurrentDateTimeInRFC3339: ${sdf.format(Date())}")
            return sdf.format(Date())
        } catch (ex: Exception) {
            Log.e(TAG, "getCurrentDateTimeInRFC3339: ", ex)
        }
        return null
    }



    private fun showWeekPickerDialog() {
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setValidator(WeekValidator())
        builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Select Week Start date")
        builder.setCalendarConstraints(constraintsBuilder.build())

        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedWeek = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            selectedWeek.timeInMillis = selection!!
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startOfWeek = selectedWeek.clone() as Calendar
            startOfWeek[Calendar.DAY_OF_WEEK] = startOfWeek.firstDayOfWeek
            val endOfWeek = startOfWeek.clone() as Calendar
            endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
            val formattedStartDate: String = sdf.format(startOfWeek.time)
            val formattedEndDate: String = sdf.format(endOfWeek.time)
            val selectedWeekText =
                "Start Date: $formattedStartDate\nEnd Date: $formattedEndDate \n Week Number: ${
                    getWeekNumber(
                        Date(selection)
                    )
                }"
            binding.selectedWeekTV.text = selectedWeekText
        }


        datePicker.show(parentFragmentManager, "WeekPickerDialog")
    }

    private fun getCalendarConstraints(): CalendarConstraints {
        val now = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setEnd(now)

        return constraintsBuilder.build()
    }

    private fun getWeekNumber(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.firstDayOfWeek = Calendar.MONDAY // Adjust the first day of the week if needed

        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    private class WeekValidator() : CalendarConstraints.DateValidator {
        constructor(parcel: Parcel) : this() {
        }

        override fun isValid(date: Long): Boolean {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = date

            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return dayOfWeek == Calendar.SUNDAY // Adjust this condition as needed for your desired start day of the week
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {

        }

        companion object CREATOR : Parcelable.Creator<WeekValidator> {
            override fun createFromParcel(parcel: Parcel): WeekValidator {
                return WeekValidator(parcel)
            }

            override fun newArray(size: Int): Array<WeekValidator?> {
                return arrayOfNulls(size)
            }
        }
    }

    private fun showDatePickerDialog() {
        val constraintsBuilder = CalendarConstraints.Builder()

        // Set the minimum selectable date to a specific date (e.g., January 1, 2000).
        val calendar = Calendar.getInstance()
        val min = calendar.timeInMillis
        constraintsBuilder.setStart(min)
        calendar.add(Calendar.DAY_OF_MONTH, 10)
        val max = calendar.timeInMillis
        constraintsBuilder.setEnd(max)
        constraintsBuilder.setEnd(calendar.timeInMillis)
        val dateValidatorMin: CalendarConstraints.DateValidator =
            DateValidatorPointForward.from(min)
        val dateValidatorMax: CalendarConstraints.DateValidator =
            DateValidatorPointBackward.before(max)

        val listValidators = ArrayList<CalendarConstraints.DateValidator>()
        listValidators.apply {
            add(dateValidatorMin)
            add(dateValidatorMax)
        }
        val validators = CompositeDateValidator.allOf(listValidators)
        constraintsBuilder.setValidator(validators)

        builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(getString(R.string.app_name))
        builder.setCalendarConstraints(constraintsBuilder.build())
        // Set the initial date to the current date
        val currentTimeMillis = Calendar.getInstance().timeInMillis
        builder.setSelection(currentTimeMillis)
        builder.setTheme(R.style.MyDatePickerStyle)
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.selectedNowOnTV?.text = simpleDateFormat.format(selection)
            Log.d(TAG, "showDatePickerDialog: ${binding.selectedNowOnTV?.text}.timeInMillis")
        }
        datePicker.show(parentFragmentManager, "cheque_date")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        isLoop = false
    }


    companion object {
        const val TAG = "TimePickerFragment"
    }
}