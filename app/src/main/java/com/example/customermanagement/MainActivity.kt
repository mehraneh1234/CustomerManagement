package com.example.customermanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import android.util.Log
import java.util.jar.Attributes.Name
/* Mehraneh - 30062786 - AT2- Practical */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // By click on Insert button The entry data in Name, Email and Mobile fields send to
        // addCustomer method then the controllers Name, Email and Mobile will be cleared.
        // At the beginning assign all xml controller to the code behind
        val btnInsertCustomer = findViewById<Button>(R.id.btnInsert)
        btnInsertCustomer.setOnClickListener {
            val db = DBHelper(this, null)
            val etName = findViewById<EditText>(R.id.etName)
            val etEmail = findViewById<EditText>(R.id.etEmail)
            val etMobile = findViewById<EditText>(R.id.etMobile)
            val etId = findViewById<EditText>(R.id.etId)
            // Get user input
            val id = etId.text.toString().toIntOrNull() ?:0 // Convert to Int
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val mobile = etMobile.text.toString()
            // Validate input
            if (name.isNotEmpty() && email.isNotEmpty() && mobile.isNotEmpty()) {
                // To debug and display the result of the method
                val variable = db.addCustomer(name, email, mobile)
                Log.d("TAG", "Value of some variable: $variable")
                // Toast to message on the screen
                Toast.makeText(this, name + " added to database", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etEmail.text.clear()
                etMobile.text.clear()
                etId.text.clear()
            } else {
            // Display a message indicating that all fields are required
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }


        }
        // By clicking on the print button, the getAllCustomers method in the DBHelper class display
        // all records if exist in the text view
        val btnPrintCustomers = findViewById<Button>(R.id.btnPrint)
        btnPrintCustomers.setOnClickListener {
            val db = DBHelper(this, null)
            val cursor = db.getAllCustomers()
            //To store a list of Customer instance
            val customerList = mutableListOf<Customer>()
            // To find the exception by debugging if the database query doesn't work
            if (cursor == null) {
                Log.e("PrintButton", "Cursor is null")
                Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Process the cursor and create Customer instances
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.EMAIL))
                val mobile = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MOBILE))
                // Create a Customer instance for each row in the cursor
                val customer = Customer(id, name, email, mobile)
                // Add the Customer instance to the list
                customerList.add(customer)
            }
            for (customer in customerList) {
                // Display or process each customer instance
                Log.d("CustomerList", "Customer: $customer")
            }
            // !!The cursor is not null and if it is null throw a "NullPointException". MoveToFirst()
            // method is used to move the cursor to the first row of the result set, if available
            cursor!!.moveToFirst()
            val tvCustomer = findViewById<TextView>(R.id.tvCustomerRecord)
            tvCustomer.text = "### Customers ###\n"
            if (cursor!!.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ID))
                Log.d("PrintButton", "ID from cursor: $id")
                // To display the id in 6 digits
                val formattedId = String.format("%06d", id)
                // To define how to display the result in text view
                tvCustomer.append(formattedId + ": "+
                    cursor.getString(1) +
                    "(" + cursor.getString(3) + ")" +
                    "(" + cursor.getString(2) +")\n")
            }
            // While there is another row "moveToNext" returns true then display the result in the
            // text view with the defined formatted.
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ID))
                Log.d("PrintButton", "ID from cursor: $id")
                val formattedId = String.format("%06d", id)
                tvCustomer.append(
                    "$formattedId" +
                    ": " + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.NAME)) +
                    "(" + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MOBILE)) + ")" +
                    "(" + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.EMAIL)) + ")\n")
            }
            Toast.makeText(this, " Print all customers", Toast.LENGTH_SHORT).show()
            cursor.close()
        }
        // By clicking on the delete button, if the customer is written in the id
        // controller exists, that record is deleted.
        val btnDeleteCustomer = findViewById<Button>(R.id.btnDelete)
        btnDeleteCustomer.setOnClickListener {
            val db = DBHelper(this, null)
            val idCustomer = findViewById<EditText>(R.id.etId).text.toString()
            val rows = db.deleteCustomer(idCustomer)
            // To display the appropriate message in regards to delete a record or not
            Toast.makeText(this,
                when (rows) {
                    0 -> "Nothing deleted"
                    1 -> "1 record deleted"
                    else -> "" // shouldn't happen
                },
                Toast.LENGTH_LONG).show()
            // To display the value of the variable when debug the code
            Log.d("TAG", "Value of some variable: $rows")
        }
        // By clicking on the update button, regarding to the id that is written in the
        //id controller the name, email and mobile are changed to the new one that are written
        // in related controller
        val btnUpdateCustomer = findViewById<Button>(R.id.btnUpdate)
        btnUpdateCustomer.setOnClickListener {
            val db = DBHelper(this, null)
            val idCustomer = findViewById<EditText>(R.id.etId).text.toString()
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val mobile = findViewById<EditText>(R.id.etMobile).text.toString()
            val rows = db.updateCustomer(idCustomer, name, email, mobile)
            Toast.makeText(this, "$rows subjects updated", Toast.LENGTH_LONG).show()
            Log.d("TAG", "Value of some variable: $rows")
        }
        // By clicking on the search button, regarding to the name that user entered the method of
        // searchCustomer is called and if it is found display it in the text view. In each status
        // display the appropriate message!
        val btnSearchCustomer = findViewById<Button>(R.id.btnSearch)
        btnSearchCustomer.setOnClickListener {
            val db = DBHelper(this, null)
            val nameCustomer = findViewById<EditText>(R.id.etName).text.toString()
            val cursor = db.searchCustomer(nameCustomer)
            if (cursor == null) {
                Log.e("PrintButton", "Cursor is null")
                Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            /*else if (cursor != null && !cursor.moveToFirst()) {
                Toast.makeText(this, "Please enter the name!", Toast.LENGTH_SHORT).show()
            }*/
            cursor!!.moveToFirst()
            val tvCustomer = findViewById<TextView>(R.id.tvCustomerRecord)
            tvCustomer.text = "### Customers ###\n"
            if (cursor!!.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ID))
                Log.d("PrintButton", "ID from cursor: $id")
                val formattedId = String.format("%06d", id)
                tvCustomer.append(
                    "$formattedId" +
                //tvCustomer.append(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ID)) +
                    ": " + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.NAME)) +
                    "(" + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MOBILE)) + ")" +
                    "(" + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.EMAIL)) + ")\n")
                Toast.makeText(this, nameCustomer + "'s Detail!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "The customer is not found!", Toast.LENGTH_SHORT).show()
            }
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ID))
                Log.d("PrintButton", "ID from cursor: $id")
                val formattedId = String.format("%06d", id)
                tvCustomer.append(
                    "$formattedId" +
                    //tvCustomer.append(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ID)) +
                    ": " + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.NAME)) +
                    "(" + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MOBILE)) + ")" +
                    "(" + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.EMAIL)) + ")\n"
                )
                Toast.makeText(this, nameCustomer + "'s Detail!", Toast.LENGTH_SHORT).show()
            }
           cursor.close()
        }
    }
}