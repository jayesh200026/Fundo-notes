package service

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import util.Notes
import util.SharedPref
import util.SqlNotes

var DATABASE_NAME="MyDb"
var TABLE_NOTE="Notes"
var COLUMN_UID="uid"
var COLUMN_TITLE="title"
var COLUMN_NOTE="note"


class DBHandler(var context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
//        val createTable="CREATE TABLE "+ TABLE_NOTE+" ("+
//                COLUMN_UID+" VARCHAR(256),"+
//                COLUMN_TITLE+" VARCHAR(256),"+
//                COLUMN_NOTE+" VARCHAR(256)," +
//                "PRIMARY KEY( "+ COLUMN_UID+" , "+ COLUMN_TITLE+" ))";

        val createTable="create table $TABLE_NOTE ($COLUMN_UID text,$COLUMN_TITLE text,$COLUMN_NOTE text)"
        db?.execSQL(createTable)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertNote(note:SqlNotes){
        val db=this.writableDatabase
        var cv=ContentValues()
        cv.put(COLUMN_UID,note.uid)
        cv.put(COLUMN_TITLE,note.title)
        cv.put(COLUMN_NOTE,note.note)
        var result=db.insert(TABLE_NOTE,null,cv)
        if(result.toInt() == -1){
        Toast.makeText(context,"Failed to save in sqlite",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(context,"saved in sqlite",Toast.LENGTH_SHORT).show()
        }


    }

    @SuppressLint("Range")
    fun getNotes(uid:String):MutableList<Notes>{
        var notes= mutableListOf<Notes>()
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NOTE"
        val cursor = db.rawQuery(selectQuery, null)
        //if (cursor != null){
            if(cursor.moveToFirst()){
           do{
                val id=cursor.getString(cursor.getColumnIndex(COLUMN_UID)).toString()
                if(id==uid){
                    val title=cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)).toString()
                    val note=cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)).toString()
                    val userNote=Notes(title,note)
                    notes.add(userNote)
                }

            } while (cursor.moveToNext())
        }

    return notes

    }

    fun updateNote(note:SqlNotes){
        val db=this.writableDatabase
        var cv=ContentValues()
        cv.put(COLUMN_UID,note.uid)
        cv.put(COLUMN_TITLE,note.title)
        cv.put(COLUMN_NOTE,note.note)
        val result=db.update(TABLE_NOTE,cv, COLUMN_UID+"= ? AND "+ COLUMN_TITLE+"= ?", arrayOf(note.uid,note.title))
        if(result==-1){
            Toast.makeText(context,"Failed to update in sqlite",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(context,"updated in sqlite",Toast.LENGTH_SHORT).show()
            SharedPref.setUpdateStatus("updateStatus",false)

        }
    }

    fun deleteNote(note:SqlNotes){
        val db=this.writableDatabase
        val result=db.delete(TABLE_NOTE,COLUMN_UID+"= ? AND "+ COLUMN_TITLE+"= ?", arrayOf(note.uid,note.title))
        if(result==-1){
            Toast.makeText(context,"Failed to delete in sqlite",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(context,"deleted in sqlite",Toast.LENGTH_SHORT).show()
            SharedPref.setUpdateStatus("updateStatus",false)

        }
    }
}