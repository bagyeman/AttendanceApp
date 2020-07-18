package com.belinda.attendanceapp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_attendance.*
import kotlinx.android.synthetic.main.student_list.*
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import kotlin.math.abs

class AttendanceActivity : AppCompatActivity() {

    var total: Int = 0
    var students: Int = 0
    var selected: Uri? = null
    var counter: Int = 0
    var studentsArray = ArrayList<String>()
    var present_Students_Array = ArrayList<String>()




    lateinit var mRecyclerView: RecyclerView
    lateinit var ref: DatabaseReference
    lateinit var attendanceref: DatabaseReference
    lateinit var absenteesref: DatabaseReference


    lateinit var weeklySpinner: Spinner





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_student,menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_student){
            val intent = Intent(applicationContext,StudentInfoActivity::class.java)
            startActivity(intent)

        }

        return super.onOptionsItemSelected(item)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        setSupportActionBar(findViewById(R.id.my_toolbar))


        ref = FirebaseDatabase.getInstance().reference.child("Student Profile")
        attendanceref = FirebaseDatabase.getInstance().reference.child("Attendance")
        absenteesref = FirebaseDatabase.getInstance().reference.child("Absentees")
        mRecyclerView = findViewById<RecyclerView>(R.id.studentrecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        weeklySpinner = findViewById(R.id.spinnerWeeks)
       // mStorageRef = FirebaseStorage.getInstance().reference

        //show_progress = findViewById(R.id.pro)
        //spinnerComment.isEnabled = false

        firebaseData()

    }


    private fun firebaseData() {

        val option = FirebaseRecyclerOptions.Builder<Model>()
            .setQuery(ref,Model::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Model, MyViewHolder>(option){

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ):MyViewHolder {

                val itemView = LayoutInflater.from(this@AttendanceActivity).inflate(R.layout.student_list,parent,false)
                val reasons = resources.getStringArray(R.array.absence_reasons)




                return MyViewHolder(itemView)
            }



            override fun onBindViewHolder(p0:MyViewHolder, p1: Int, p2: Model) {

                val placeid = getRef(p1).key.toString()
                val picasso = Picasso.get()

                val current_position = p0


                ref.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        students = p0.children.count()
                    }
                })

                ref.child(placeid).addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        //students = dataSnapshot.children.count()

                        studentsArray.add(p2.Name.toString())

                        p0.txt_name.text = p2.Name
                        p0.gender_type.text = p2.Gender
                        // this is how you will apply it when you add it to your firebase database
                        picasso.load(p2.Profile_Image).into(p0.profile_pic)

                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(this@AttendanceActivity,"Error Occurred" + p0.toException(),Toast.LENGTH_LONG).show()
                    }


                })


                p0.check_box.setOnClickListener{
                    if (p0.check_box.isChecked){

                        totalAttendance()
                        present_Students_Array.add(p2.Name.toString())
                        commentText.isEnabled = false
                        spinnerComment.isEnabled = false
                        //Toast.makeText(this@AttendanceActivity, p2.Name.toString(),Toast.LENGTH_LONG).show()

                    }else{
                        lesstotalAttendance()
                        present_Students_Array.remove(p2.Name.toString())
                        spinnerComment.isEnabled = true

                        //val item_position = myDataset.get()
                    }
                }

                if (p0.spinner != null){
                    val adapter = ArrayAdapter.createFromResource(this@AttendanceActivity, R.array.absence_reasons,android.R.layout.simple_spinner_item)
                    p0.spinner.adapter = adapter
                }

                //picasso.load("https://firebasestorage.googleapis.com/v0/b/attendance-ba2fa.appspot.com/o/profile_image%2Fbrainy.jpeg?alt=media&token=0317af9b-4b4e-4bc5-8667-147432813249").into(p0.profile_pic)



            }

        }

        mRecyclerView.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }


    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

        internal  var txt_name: TextView = itemView!!.findViewById(R.id.nameTextView)
        internal  var gender_type: TextView = itemView!!.findViewById(R.id.genTextView)
        internal  var profile_pic: ImageView = itemView!!.findViewById(R.id.profile_image)
        internal  var check_box: CheckBox = itemView!!.findViewById(R.id.attendancecheckbox)
        internal  var spinner: Spinner = itemView!!.findViewById(R.id.spinnerComment)


    }

    fun saveAttendance(view: View){

        /*attendanceref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            @TargetApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {

                val total_present = total

                val rnds = (0..100).random()

               // val current_date_time = LocalDateTime.now().toString()
                attendanceref.child(rnds.toString()).child("Total Present").setValue(total_present)



            }

        })*/
       // counter ++
        val total_present = total

        val rnds = (0..100).random()

        val week = weeklySpinner.selectedItem.toString()

        studentsArray.removeAll(present_Students_Array)
        Log.d("Absent students", studentsArray.toString())


        attendanceref.child(week).child(rnds.toString()).child("Total Present").setValue(total_present)

        for(i in 0 until studentsArray.count()){

            absenteesref.addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(p0: DataSnapshot) {

                    for(data: DataSnapshot in p0.children){
                        if (data.key == week){

                            if (data.hasChild(studentsArray[i])){
                                var absent_count = data.child(studentsArray[i]).value.toString().toInt()
                                absent_count++
                                absenteesref.child(week).child(studentsArray[i]).setValue(absent_count.toString())
                            }else{
                                absenteesref.child(week).child(studentsArray[i]).setValue("1")
                            }

                        }else{
                            absenteesref.child(week).child(studentsArray[i]).setValue("1")
                        }

                    }


                }


                override fun onCancelled(p0: DatabaseError) {
                }



            })

           // absenteesref.child(week).child(studentsArray[i]).setValue("1")





        }

            Toast.makeText(this@AttendanceActivity,"Attendance Submitted",Toast.LENGTH_LONG).show()
            val intent = Intent(applicationContext,Dashboard::class.java)
            startActivity(intent)





    }


    fun totalAttendance(){
        total += 1

        totalTextView.text = "Total: $total / $students"

    }

    fun lesstotalAttendance(){

        total -= 1

        totalTextView.text = "Total: $total / $students"

    }

    fun upload(){


    }





}
