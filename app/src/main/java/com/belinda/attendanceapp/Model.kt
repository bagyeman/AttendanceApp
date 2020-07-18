package com.belinda.attendanceapp

/*class Model {
// student profile model class


    var Name: String? = null
    var Gender: String? = null
    var Profile_Image: String? = null

    constructor():this("",""){

    }

    constructor(Name: String?, Gender: String?) {
        this.Name = Name
        this.Gender = Gender
    }
}*/

class Model {

    var Name: String? = null
    var Gender: String? = null
    var Profile_Image: String? = null

    constructor():this("","",""){

    }

    constructor(Name: String?, Gender: String?, Profile_Image: String){
        this.Name = Name
        this.Gender = Gender
        this.Profile_Image = Profile_Image
    }
}