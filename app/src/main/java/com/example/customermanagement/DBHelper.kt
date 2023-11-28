package com.example.customermanagement
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import androidx.core.app.NavUtils
import androidx.core.content.contentValuesOf

/* Mehraneh - 30062786 - AT2- Practical */
/* Declare DBHelper class that extends SQLiteOpenHelper. It takes a Context and an optional
CursorFactory as parameters and passes them to the constructor of the superclass (SQLiteOpenHelper).
The superclass constructor initializes the database, version, and other parameters. */
class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
/* A companion object is a singleton object associated with the class, and its members can be
 accessed directly on the class itself without creating an instance of the class.*/
    companion object {
        private val DB_NAME = "smtbiz"
        private val DB_VERSION = 1
        val TABLE_NAME = "customer"
        val ID = "Id"
        val NAME = "Name"
        val EMAIL = "Email"
        val MOBILE= "Mobile"
    }
    // onCreate method creates a table with columns in the declared database
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = (
                "CREATE TABLE $TABLE_NAME (" +
                    "$ID INTEGER NOT NULL PRIMARY KEY," +
                    "$NAME TEXT," +
                    "$EMAIL TEXT," +
                    "$MOBILE TEXT" + ")"
                )
        db?.execSQL(createTable) //NULLABLE
    }
    // Called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") // !!non-null assertion.
        //Error if null at compile time
        onCreate(db)
    }

    // This method is to add a record in DB
    fun addCustomer(name: String, email: String, mobile: String) {
        // create a writable DB variable of our database to insert record
        val db = this.writableDatabase
        try {
            // Check the current number of records
            val countCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
            countCursor.moveToFirst()
            val currentRecordCount = countCursor.getInt(0)
            countCursor.close()
            // Reset the table if the count is 5 or more
            if (currentRecordCount >= 5) {
                db.execSQL("DELETE FROM $TABLE_NAME")
            }
            // Insert new records
            val values = ContentValues()
            values.put(NAME, name)
            values.put(EMAIL, email)
            values.put(MOBILE, mobile)
            // insert all values into DB
            db.insert(TABLE_NAME, null, values)
        } finally {
            // Close the database in a finally block to ensure it's closed
            db.close()
        }
    } // This method is get all customers records from DB
    fun getAllCustomers(): Cursor? {
        // create a readable DB variable of our database to read record
        val db = this.readableDatabase
        // Return all records from DB
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    } // This method is delete the customer record from DB
    fun deleteCustomer(customerId: String): Int {
        // create a writable DB variable of our database to delete record
        val db = this.writableDatabase
        // delete a customer by ID
        val rows = db.delete(TABLE_NAME, "Id=?", arrayOf(customerId))
        db.close()
        return rows // 0 or 1
    } // This method is update the customer record from DB
    fun updateCustomer(customerId: String, customerName: String, customerEmail: String,
                       customerMobile: String): Int {
        // create a writable DB variable of our database to update record
        val db = this.writableDatabase
        // This ContentValues class is used to store a set of values
        val values = ContentValues()
        values.put(NAME, customerName)
        values.put(EMAIL, customerEmail)
        values.put(MOBILE, customerMobile)
        val rows = db.update(TABLE_NAME, values, "Id=?", arrayOf(customerId))
        db.close()
        return rows // rows updated
    } // This method is search the customer by Name from DB
    fun searchCustomer(customerName: String): Cursor? {
        // create a readable DB variable of our database to read record
        val db = this.readableDatabase
        // Find the records from DB which their names are equal to the entered name by the user
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE Name=?",
            arrayOf(customerName))
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("Id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
                    val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
                    val mobile = cursor.getString(cursor.getColumnIndexOrThrow("Mobile"))
                } while (cursor.moveToNext())
            }
        }
        else { // // To debug and display the message
            Log.e("DBHelper", "Cursor is null")
            return null
        }
        return cursor
    }


}