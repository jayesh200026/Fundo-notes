package com.example.fundoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.fundoapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.service.NotificationWorker
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.util.Constants
import com.example.fundoapp.util.SharedPref
import com.example.fundoapp.viewModel.AddNoteViewModel
import com.example.fundoapp.viewModel.AddNoteViewModelFactory
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val DATE_FORMAT = "dd/MM/yy hh:mm"

class AddNotesFragment : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    lateinit var toolbar: Toolbar
    lateinit var userIcon: ImageView
    lateinit var gridorLinear: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView
    lateinit var remainder: ImageView
    lateinit var archive: ImageView
    lateinit var title: EditText
    lateinit var note: EditText
    lateinit var savetext: TextView
    lateinit var saveBtn: FloatingActionButton
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var addNoteViewModel: AddNoteViewModel


    var day = 0
    var year = 0
    var month = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedYear = 0
    var savedMonth = 0
    var savedHour = 0
    var savedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_notes, container, false)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        addNoteViewModel = ViewModelProvider(
            this,
            AddNoteViewModelFactory()
        )[AddNoteViewModel::class.java]

        observe()
        initializeVar(view)
        handleToolbar()
        onClicks()
        checkIfUpdate()
        return view
    }

    private fun checkIfUpdate() {
        val updateStatus = SharedPref.getUpdateStatus("updateStatus")
        if (updateStatus) {
            remainder.isVisible = true
            deleteBtn.isVisible = true
            updateNote()
        }
    }

    private fun onClicks() {
        saveBtn.setOnClickListener(this)
        savetext.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)

        archive.setOnClickListener(this)
        remainder.setOnClickListener(this)
    }

    private fun handleToolbar() {
        userIcon.isVisible = false
        gridorLinear.isVisible = false
        searchBar.isVisible = false
        searchview.isVisible = false
        deleteBtn.isVisible = false
        archive.isVisible = false

        if (SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)) {
            archive.setImageResource(R.drawable.ic_baseline_unarchive_24)
            archive.isVisible = true
        } else if (!SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)) {
            archive.setImageResource(R.drawable.ic_baseline_archive_24)
            archive.isVisible = true
        }



        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            SharedPref.setUpdateStatus("updateStatus", false)
            //activity?.onBackPressed()
            sharedViewModel.setGotoHomePageStatus(true)
        }
    }

    private fun initializeVar(view: View) {
        userIcon = requireActivity().findViewById(R.id.userProfile)
        gridorLinear = requireActivity().findViewById(R.id.notesLayout)
        searchBar = requireActivity().findViewById(R.id.searchNotes)
        searchview = requireActivity().findViewById(R.id.searchView)
        deleteBtn = requireActivity().findViewById(R.id.deleteButton)
        archive = requireActivity().findViewById(R.id.archiveImage)
        remainder = requireActivity().findViewById(R.id.remainder)
        title = view.findViewById(R.id.noteTitle)
        note = view.findViewById(R.id.userNote)
        saveBtn = view.findViewById(R.id.saveFAB)
        savetext = view.findViewById(R.id.saveText)
        toolbar = requireActivity().findViewById(R.id.myToolbar)
    }

    private fun updateNote() {
        val noteTitle = SharedPref.get("title")
        val noteContent = SharedPref.get("note")
        title.setText(noteTitle)
        note.setText(noteContent)
    }

    private fun observe() {
        addNoteViewModel.databaseNoteAddedStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noteStoringFailed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        addNoteViewModel.databaseNoteUpdateStatus.observe(viewLifecycleOwner) {
            SharedPref.setUpdateStatus("updateStatus", false)
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noteUpdationFailed),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }
        addNoteViewModel.databaseNoteDeletionStatus.observe(viewLifecycleOwner) {
            SharedPref.setUpdateStatus("updateStatus", false)
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noteDeletionFailed),
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
        addNoteViewModel.archivedStatus.observe(viewLifecycleOwner) {
            SharedPref.setUpdateStatus("updateStatus", false)
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            }
        }
        addNoteViewModel.unArchivedStatus.observe(viewLifecycleOwner) {
            SharedPref.setUpdateStatus("updateStatus", false)
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            }
        }
        addNoteViewModel.remainderStatus.observe(viewLifecycleOwner) {
            SharedPref.setUpdateStatus("updateStatus", false)
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            }
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.saveText, R.id.saveFAB -> {
                if (SharedPref.getUpdateStatus("updateStatus")) {
                    updateNoteToDatabase()
                } else {
                    storeToDatabase()
                }
            }
            R.id.deleteButton -> {
                deleteNote()
            }
            R.id.archiveImage -> {
                if (!SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)) {
                    archiveNote()
                } else if (SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)) {
                    unarchive()
                }
            }
            R.id.remainder -> {
                pickDate()
            }
        }
    }


    private fun unarchive() {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val key = SharedPref.get("key")
        val deleted = SharedPref.getBoolean(Constants.COLUMN_DELETED)
        val archived = SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)
        val remainder = SharedPref.getRemainder(Constants.COLUMN_REMAINDER)
        val mtime = SharedPref.get(Constants.COLUMN_MODIFIEDTIME)
        val userNote = NotesKey(titleText, noteText, key!!, deleted, archived, mtime!!, remainder)
        if (titleText.isNotEmpty() && noteText.isNotEmpty()) {
            addNoteViewModel.unArchiveNote(userNote, context)
        }
    }

    private fun archiveNote() {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val key = SharedPref.get("key")
        val deleted = SharedPref.getBoolean(Constants.COLUMN_DELETED)
        val archived = SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)
        val remainder = SharedPref.getRemainder(Constants.COLUMN_REMAINDER)
        val mtime = SharedPref.get(Constants.COLUMN_MODIFIEDTIME)
        val userNote = NotesKey(titleText, noteText, key!!, deleted, archived, mtime!!, remainder)
        if (titleText.isNotEmpty() && noteText.isNotEmpty()) {
            addNoteViewModel.archiveNote(userNote, context)
        }
    }

    private fun deleteNote() {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val key = SharedPref.get("key")
        val deleted = SharedPref.getBoolean(Constants.COLUMN_DELETED)
        val archived = SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)
        val remainder = SharedPref.getRemainder(Constants.COLUMN_REMAINDER)
        val mtime = SharedPref.get(Constants.COLUMN_MODIFIEDTIME)
        val userNote = NotesKey(titleText, noteText, key!!, deleted, archived, mtime!!, remainder)
        if (titleText.isNotEmpty() && noteText.isNotEmpty()) {
            addNoteViewModel.deleteNote(userNote, context)
        }
    }

    private fun updateNoteToDatabase() {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val key = SharedPref.get("key")
        val deleted = SharedPref.getBoolean(Constants.COLUMN_DELETED)
        val archived = SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)
        val mtime = SharedPref.get(Constants.COLUMN_MODIFIEDTIME)
        val remainder = SharedPref.getRemainder(Constants.COLUMN_REMAINDER)
        val userNote = NotesKey(titleText, noteText, key!!, deleted, archived, mtime!!, remainder)
        addNoteViewModel.updateNote(userNote, context)
    }

    private fun storeToDatabase() {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val uid = Authentication.getCurrentUid()
        if (titleText != "" || noteText != "") {
            if (uid != null) {
                addNoteViewModel.addNote(uid, titleText, noteText, context)
            }
        }
    }

    private fun pickDate() {
        getDateTimeCalender()
        DatePickerDialog(requireContext(), this, year, month, day).show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        savedDay = day
        savedMonth = month
        savedYear = year
        getDateTimeCalender()
        TimePickerDialog(requireContext(), this, hour, minute, false).show()
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute

        val cal = Calendar.getInstance()
        cal.set(savedYear, savedMonth, savedDay, savedHour, savedMinute, 0)
        val timeInMilli = cal.timeInMillis
        val date = millisToDate(timeInMilli)

        if (timeInMilli > System.currentTimeMillis()) {
            addRemainder(timeInMilli)
        } else {
            Toast.makeText(requireContext(), R.string.invalidRemainder, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addRemainder(timeInMilli: Long) {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val key = SharedPref.get("key")
        val deleted = SharedPref.getBoolean(Constants.COLUMN_DELETED)
        val archived = SharedPref.getBoolean(Constants.COLUMN_ARCHIVED)
        val note = NotesKey(titleText, noteText, key!!, deleted, archived, "")
        if (titleText.isNotEmpty() && noteText.isNotEmpty()) {
            scheduleNotification(note,timeInMilli)
            addNoteViewModel.addRemainder(note, context, timeInMilli)
        }
    }

    private fun scheduleNotification(i: NotesKey,timeInMilli: Long) {
        Log.d("Notification","scheduling notification")
        val currentTime = System.currentTimeMillis()

        val reminder = timeInMilli
        val delay = reminder - currentTime
        if (delay > 0) {
            val data = Data.Builder()
            data.putString("noteTitle", i.title)
            data.putString("noteContent", i.note)
            data.putString("noteKey", i.key)
            data.putBoolean("isDeleted", i.deleted)
            data.putBoolean("isArchived", i.archived)
            data.putString("modifiedTime", i.mTime)
            data.putLong("reminder", reminder)
            Toast.makeText(
                requireContext(),
                "set remainder in " + TimeUnit.MILLISECONDS.toMinutes(delay) + " minutes",
                Toast.LENGTH_SHORT
            ).show()
            val request = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInputData(data.build())
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            //WorkManager.getInstance(this).enqueue(request)
            WorkManager.getInstance(requireActivity()).enqueueUniqueWork(
                i.key,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }


    }

    private fun getDateTimeCalender() {
        val calender = Calendar.getInstance()
        year = calender.get(Calendar.YEAR)
        month = calender.get(Calendar.MONTH)
        day = calender.get(Calendar.DAY_OF_MONTH)
        hour = calender.get(Calendar.HOUR)
        minute = calender.get(Calendar.MINUTE)
    }

    private fun millisToDate(millis: Long): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date(millis))
    }
}