package com.belinda.attendanceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_student_info.*

class StudentInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)

        //Get radio group selected item using on checked change listener

        gender_radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                Toast.makeText(applicationContext,"On checked Change: ${radio.text}",Toast.LENGTH_LONG).show()
            }
        )


        //Get radio group selected item using on checked change listener

        savebutton.setOnClickListener {
            //Get the checked radio button id from radio group
            var id: Int = gender_radio_group.checkedRadioButtonId
            if (id!=-1){
                //if any radio button checked from radio group
                //Get the instance of radio button using id
                val radio:RadioButton = findViewById(id)
                Toast.makeText(applicationContext,"Selected Gender : ${radio.text}",Toast.LENGTH_LONG).show()
            }else{
                //If no radio button is checked in the group

                Toast.makeText(applicationContext,"No gender selected",Toast.LENGTH_LONG).show()

            }
        }

    }


    fun save(view: View){

    }
}
