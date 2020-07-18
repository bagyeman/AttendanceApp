package com.belinda.attendanceapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.textclassifier.SelectionEvent
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity() {

    lateinit var ref: DatabaseReference
    lateinit var attendanceref: DatabaseReference
    lateinit var absenteesref: DatabaseReference
    lateinit var mAuth: FirebaseAuth

    lateinit var dashspinner: Spinner

    var totalattendance = 0
    var counter = 0
    var absent_names_Array = ArrayList<String>()
    var selectedWeek : String? = null
    //var termlyaverage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //ref = FirebaseDatabase.getInstance().getReference().child("Student Profile")
        mAuth = FirebaseAuth.getInstance()
        attendanceref = FirebaseDatabase.getInstance().reference.child("Attendance")
        absenteesref = FirebaseDatabase.getInstance().reference.child("Absentees")

        dashspinner = findViewById(R.id.spinnerDashWeeks)
        dashspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedWeek = parent!!.selectedItem.toString()
                firebaseWeeklyData(selectedWeek)
                display_absenteees(selectedWeek!!)
            }

        }

        display_absenteees("Week 1")


        firebaseData()

    }

    /*
        function for calculating the termly average
    */
    private fun firebaseData() {

        val postListener = object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                // getting the total of weeks recorded
                val total_number_of_weeks = p0.children.count()

                for (data : DataSnapshot in p0.children){
                    // the total number of days that attendance was taken for each week
                    counter +=  data.children.count()

                    // looping through the weeks to be able to access the total attendance taken
                    for (sub_data: DataSnapshot in data.children){
                        val value = sub_data.child("Total Present").value.toString()

                        totalattendance += value.toInt()
                    }

                    // the old or original code you sent me
                    // val value = data.child("Total Present").value.toString()

                    // if (value.toInt() != 0)
                    //     totalattendance += value.toInt()

                }

                var termlyaverage = totalattendance.toDouble()/counter

                // change this textview id to the termly one
                termly_average.text = "%.4f".format(termlyaverage)
            }

            override fun onCancelled(p0: DatabaseError) {
                //To change body of created functions use File | Settings | File Templates.
            }

        }
        attendanceref.addValueEventListener(postListener)


    }

    /*
        function for calculating the weekly average
        This function must be able to take the Week # that you want to see its average
        So we will make use of another spinner in the dashboard where the lecturer will select
            the particular week he wants to know the average attendance.
    */
    private fun firebaseWeeklyData(week_number: String?) {

        var weeklyCounter = 0
        var weeklyTotalAverage = 0

        val postListener = object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                // getting the total of weeks recorded
                val total_number_of_weeks = p0.children.count()

                for (data : DataSnapshot in p0.children){
                    // the total number of days that attendance was taken for each week
                   // weeklyCounter =  data.children.count()

                    // we will check if the data coming has the week number that the lecturer
                    // is requesting for
                    if (data.key == week_number){
                        weeklyCounter =  data.children.count()

                        // looping through the weeks to be able to access the total attendance taken
                        for (sub_data: DataSnapshot in data.children){
                            val value = sub_data.child("Total Present").value.toString()
                            weeklyTotalAverage += value.toInt()
                        }
                    }
                }

                var weeklyaverage = weeklyTotalAverage.toDouble()/weeklyCounter
                weekly_average.text = "%.4f".format(weeklyaverage)
            }

            override fun onCancelled(p0: DatabaseError) {
                //To change body of created functions use File | Settings | File Templates.
            }

        }
        attendanceref.addValueEventListener(postListener)


    }

    private fun display_absenteees(week : String){

        var absentNumberList = ArrayList<Int>()

        absent_names_Array = arrayListOf()

        absenteesref.child(week).addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                for (data: DataSnapshot in p0.children){

                    absentNumberList.add(data.value.toString().toInt())

                    /*for (i in 0 until 2){
                        absenteesref.child(week).equalTo(absentNumberList[i].toString()).addListenerForSingleValueEvent(object :ValueEventListener{

                            override fun onDataChange(p0: DataSnapshot) {

                                absent_names_Array.add(data.key.toString())
                                bottom_attendees.text = absent_names_Array.toString()
                            }


                            override fun onCancelled(p0: DatabaseError) {

                            }



                        })
                    }*/

                }

                //sort the array list of Int
                absentNumberList.sortedDescending()

                for (i in 0 until 2){
                    absenteesref.child(week).addListenerForSingleValueEvent(object :ValueEventListener{

                        override fun onDataChange(p0: DataSnapshot) {
                           /* for (data: DataSnapshot in p0.children){

                            }*/

                            p0.children.forEach{
                                if (it.value.toString().toInt() == absentNumberList.sortedDescending()[i]){
                                    absent_names_Array.add(it.key.toString())
                                }
                              //  absent_names_Array.add(it.toString())
                            }

                            //absent_names_Array.add(p0.key.toString())
                            bottom_attendees.text = absent_names_Array.distinct().joinToString(separator = "\n")
                        }


                        override fun onCancelled(p0: DatabaseError) {

                        }



                    })
                }
            }


            override fun onCancelled(p0: DatabaseError) {
            }



        })


    }


    fun takeAttendance(view: View){

            val intent = Intent(applicationContext,AttendanceActivity::class.java)
            startActivity(intent)



    }
}