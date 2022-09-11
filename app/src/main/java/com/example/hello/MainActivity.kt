package com.example.hello

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hello.ui.theme.HelloTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth= FirebaseAuth.getInstance()

        setContent {
            HelloTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RegisterUserFull()
                }
            }
        }


    }
//cheching logging
    private fun checkLoggedInState(){
        if (auth.currentUser==null){
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_LONG).show()
        }else {
            Toast.makeText(this,"You are logged in ",Toast.LENGTH_LONG).show()
        }
    }
    //registring User
    private fun registerUser (){
        if(ToSave.email.isNotEmpty() && ToSave.pass.isNotEmpty() ) {
            Toast.makeText(this@MainActivity,"They are not empty",Toast.LENGTH_SHORT).show()
//            Log.e("ilyas","They are not empty")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    //we wait for this action
                    auth.createUserWithEmailAndPassword(ToSave.email,ToSave.pass)
                        .await()
//                    Log.e("ilyas","after await")
//                    Log.e("ilyas","before check logged in state")

                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }

                }catch (e:Exception){
                    Toast.makeText(this@MainActivity,"Error Happened",Toast.LENGTH_SHORT).show()

                }
            }
        }else{
            Toast.makeText(this@MainActivity,"Texts Are empty",Toast.LENGTH_SHORT).show()

        }
    }
    //Logging in
    //Login
    private fun loginUser(){
        if(ToSave.email.isNotEmpty() && ToSave.pass.isNotEmpty() ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    //this is the only thing we need to change
                    auth.signInWithEmailAndPassword(ToSave.email,ToSave.pass)
                        .await()

                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }

                }catch (e:Exception){
                    Toast.makeText(this@MainActivity,"Error Happened",Toast.LENGTH_SHORT).show()

                }
            }
        }else{
            Toast.makeText(this@MainActivity,"Texts Are empty",Toast.LENGTH_SHORT).show()

        }
    }
    ////
    private fun updateProfile() {
        val user = auth.currentUser
        user?.let { user ->
           val username = ToSave.userName
            val photoURI = Uri.parse("android.resource://$packageName/${ToSave.photoUrl}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Successfully updated profile",
                            Toast.LENGTH_LONG).show()
                    }
                } catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }

    @Composable
fun RegisterUserFull(){

    var emailTF by remember { mutableStateOf("") }
    var passTF by remember { mutableStateOf("") }
    val context = LocalContext.current

        var userNameTF by remember { mutableStateOf("") }

        val imageFromSources =painterResource(R.drawable.empty)
        var mutableImageFromResources by remember { mutableStateOf(imageFromSources) }

        var imageState by remember { mutableStateOf(ToSave.alertState) }

        //Unlike remember this will be set in every recomposition
//no it's better to use it like others and chnage it inside to get a proper Recomposition
        //for the Composables that are using it
        var random by remember { mutableStateOf((1..6).random()) }




        //MY First Higher Order Function

//        StayAlerted {
//            remember { mutableStateOf(ToSave.alertState) }
//
//            if(ToSave.alertState){
//                mutableImageFromResources = painterResource(R.drawable.filled)
//
//            }

        Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            value = emailTF,
            onValueChange = { newText ->
                emailTF = newText
            },
            label =   { Text("Enter Email")}

        )
        TextField(
            value = passTF,
            onValueChange = { newText ->
                passTF = newText
            },
            label =   { Text("Enter Password")}
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =Arrangement.Center
        ){
            Button(onClick = {
                Toast.makeText(context,"Wait",Toast.LENGTH_SHORT).show()
                //taking the data from the user(txt field) and setting them on the storage)
                ToSave.email =emailTF
                ToSave.pass =passTF
                //then registering

                registerUser()
                Toast.makeText(context,"Registering",Toast.LENGTH_SHORT).show()

            }) {
                Text(text = "Register in ")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(onClick = {
                Toast.makeText(context,"Wait",Toast.LENGTH_SHORT).show()
                //taking the data from the user(txt field) and setting them on the storage)
                ToSave.email =emailTF
                ToSave.pass =passTF
                //then logging in

                loginUser()
                Toast.makeText(context,"Logging In",Toast.LENGTH_SHORT).show()

            }) {
                Text(text = "Log In ")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(onClick = {
                Toast.makeText(context,"Wait",Toast.LENGTH_SHORT).show()
                //taking the data from the user(txt field) and setting them on the storage)
                ToSave.email =emailTF
                ToSave.pass =passTF
                //then signing out

                auth.signOut()
                Toast.makeText(context,"Signing out",Toast.LENGTH_SHORT).show()
                checkLoggedInState()

            }) {
                Text(text = "Sign Out ")

            }
        }



        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

         //   var imageName by remember { mutableStateOf("") }


            Card(

            ) {

            Image( painter =  mutableImageFromResources,
                contentDescription ="",
            modifier = Modifier.clip(CircleShape)
            )
            //MY First Higher Order Function
            StayAlerted {


                if (imageState) {

                   Toast.makeText(context,"Image $random",Toast.LENGTH_SHORT).show()
                    when(random){
                        1 ->{ mutableImageFromResources = painterResource(R.drawable.filled1)}
                        2 ->{mutableImageFromResources = painterResource(R.drawable.filled2) }
                        3 ->{ mutableImageFromResources = painterResource(R.drawable.filled3)}
                        4 ->{ mutableImageFromResources = painterResource(R.drawable.filled4)}
                        5 ->{ mutableImageFromResources = painterResource(R.drawable.filled5)}
                        6 ->{ mutableImageFromResources = painterResource(R.drawable.filled6)}
                    }

                }else{
                    mutableImageFromResources = painterResource(R.drawable.empty)
                }
            }
            }


            TextField(value = userNameTF, onValueChange = { new->
                userNameTF = new
            },
                label = { Text("Enter Your User Name") }
            )
        }


       Row(
           modifier = Modifier.fillMaxWidth(),
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.SpaceBetween
       ) {


            Button(onClick = {
                ToSave.userName =userNameTF
//                ToSave.alertState =!ToSave.alertState
                imageState =!imageState
                random =(1..6).random()


                Toast.makeText(context,"Profile Updated",Toast.LENGTH_SHORT).show()

            }) {
                Text(text = "Update Profile")

//                //MY First Higher Order Function
//                StayAlerted {
//                    remember { mutableStateOf(ToSave.alertState) }
//
//                    if (ToSave.alertState) {
//                        mutableImageFromResources = painterResource(R.drawable.filled)
//
//                    }else{
//                        mutableImageFromResources = painterResource(R.drawable.empty)
//                    }
//                }


            }
                Button(onClick = {

                }) {
                    Text(text = "Reset Profile")
                }

      }
    }
            }



}
        // not sure yet what that but i got the idea that since i cant call Composable function
        //in normal functions like Clicks i ll in the buttons Fire an alert which changes a state
        //which itself calle our composable and Update what we want

        //we here Used A higher Order Function When track the changes and Alert And the Others Updated the Ui
        //All are Composables
       @Composable
       fun StayAlerted (
            ChangeImage : @Composable ()-> Unit
       ){
            //and taking in mind that this function is recomposed when ever the State changes so :


           //Genius ides here setting the state in a 3rd place

              ChangeImage()
       }

            //this Was Unblievably Hard the problem of A function doesns accept a composable
            //this happens on the onClick function in the buttons ..etc

//        @Composable
//          fun ChangeImage( ){
//            //u can use a when statement but for now i ll keep things simple
//            painterResource(R.drawable.filled)
//          }






