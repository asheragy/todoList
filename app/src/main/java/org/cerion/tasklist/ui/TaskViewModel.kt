package org.cerion.tasklist.ui

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.cerion.tasklist.R
import org.cerion.tasklist.common.ResourceProvider
import org.cerion.tasklist.data.Task
import org.cerion.tasklist.data.TaskDao
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel(private val resources: ResourceProvider, private val db: TaskDao) : ViewModel() {

    // TODO remove double due fields and use binding converter for dateFormat
    // https://mlsdev.com/blog/57-android-data-binding

    private lateinit var task: Task

    // TODO base this field on the fact if task has an ID or not
    private var isNew = false

    var dueDate = Date(0)
        private set

    var windowTitle = ObservableField("")
    var title = ObservableField("")
    var notes = ObservableField("")
    var completed = ObservableBoolean(false)
    var hasDueDate = ObservableBoolean(false)
    var due = ObservableField("")
    var isDirty = ObservableField(false)
    var modified = ObservableField("")

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun addTask(taskListId: String) {
        if (!isNew) {
            isNew = true
            val task = Task(taskListId)
            windowTitle.set("Add new task")
            loadTaskFields(task)
        }
    }

    fun setTask(listId: String, id: String) {
        windowTitle.set("Edit task")

        val task = db.get(listId, id)
        loadTaskFields(task)
    }

    private fun loadTaskFields(task: Task) {
        this.task = task

        title.set(task.title)
        notes.set(task.notes)
        completed.set(task.completed)
        setDue(task.due)

        // TODO this seems to be detecting the set() from above when it should not
        // If that can be fixed the notifyChange() inside can be removed
        val onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                isDirty.set(true)
                isDirty.notifyChange()
            }
        }

        title.addOnPropertyChangedCallback(onPropertyChangedCallback)
        notes.addOnPropertyChangedCallback(onPropertyChangedCallback)
        completed.addOnPropertyChangedCallback(onPropertyChangedCallback)
        due.addOnPropertyChangedCallback(onPropertyChangedCallback)

        // TODO is it ever this value?
        if (task.updated.time > 0)
            modified.set(task.updated.toString())
        else
            modified.set("")

        isDirty.set(false)
    }

    fun save() {
        task.setModified()
        task.title = title.get()!!
        task.notes = notes.get()!!
        task.completed = completed.get()
        task.due = dueDate

        if (isNew)
            db.add(task)
        else
            db.update(task)

        isNew = false
    }

    fun removeDueDate() {
        setDue(Date(0))
    }

    fun setDue(date: Date) {
        dueDate = date

        if (date.time != 0L) {
            due.set(dateFormat.format(date))
            hasDueDate.set(true)
        } else {
            due.set(resources.getString(R.string.no_due_date))
            hasDueDate.set(false)
        }
    }

    companion object {
        private val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)
    }
}