package com.mansoorali.i212502

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.screenshotdetection.ScreenshotDetectionDelegate
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val MIC_PERMISSION_CODE = 200
private const val Camera_PERMISSION_CODE = 100
private const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 3009
/*
* message type =1
* audio type= 2*/


class page12_Activity_chat2 : AppCompatActivity(), ScreenshotDetectionDelegate.ScreenshotDetectionListener {

    private lateinit var database: DatabaseReference
    private lateinit var user: String
    private lateinit var personID: String
    private var defulturi: String =
        "https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"
    private var msgid: String = ""
    var audioBool = false
    private lateinit var imageuri: Uri
    private lateinit var fileuri: Uri


    private var recorder: MediaRecorder? = null
    private var outputFile: String? = null

    private val selectimg = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            imageuri = it
            saveimage()
        }
    }
    private val selectfile = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            fileuri = it
            savefile()
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page12_chat2)

        checkReadExternalStoragePermission()

        database = FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        val mentorID = intent.getStringExtra("PROFILE")
        user = userID.toString()
        personID = mentorID.toString()
        if (mentorID != null) {
            fetchUser(mentorID)
        }

        val assignAnID = if (user.hashCode() < mentorID.hashCode()) {
            "$user$mentorID"
        } else {
            "$mentorID$user"
        }

//------------------------clicks-----------------
        val send = findViewById<ImageView>(R.id.send_msg)
        send.setOnClickListener {
            val msg = findViewById<EditText>(R.id.message)
            if (msg.text.isNotEmpty()) {
                sendMessage(mentorID.toString(), 1, "")
            }
            LoadMsg(assignAnID.toString())

        }


        val databaseReference = FirebaseDatabase.getInstance().getReference("chat")

// Add a ValueEventListener to listen for changes in the Commchat node
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                LoadMsg(assignAnID.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Failed to read value.", databaseError.toException())
            }
        })

        LoadMsg(assignAnID.toString())


        // Create an ArrayList of chatMessageModel objects


        val home = findViewById<LinearLayout>(R.id.home)

        home.setOnClickListener {
            val intent = Intent(this, page4_Activity_home::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }
        val add = findViewById<LinearLayout>(R.id.plus)

        add.setOnClickListener {
            val intent = Intent(this, page9_Activity_add::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }
        val search = findViewById<LinearLayout>(R.id.search)

        search.setOnClickListener {
            val intent = Intent(this, page5_Activity_search::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }

        val chat = findViewById<LinearLayout>(R.id.chat)

        chat.setOnClickListener {
            val intent = Intent(this, page11_Activity2_chats::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }

        val vidcall = findViewById<ImageView>(R.id.vidaCal)
        vidcall.setOnClickListener {
            val intent = Intent(this, page22_Activity_vidCall::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }

        val call = findViewById<ImageView>(R.id.call)
        call.setOnClickListener {
            val intent = Intent(this, page23_Activity_audioCall::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }

        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
        val profile = findViewById<LinearLayout>(R.id.profile)
        profile.setOnClickListener {
            val intent = Intent(this, page17_Activity_profileView::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }


        val mic = findViewById<ImageView>(R.id.mic)
        mic.setOnClickListener {
            if (isMic())
                getMic()

            if (audioBool == false) {
                audioBool = true
                startRecording()
            } else if (audioBool == true) {
                audioBool = false
                stopRecording()
            }
        }
        val image = findViewById<ImageView>(R.id.photo)
        image.setOnClickListener {
            selectimg.launch("image/*")     //uploading to fire base

        }
        val file=findViewById<ImageView>(R.id.files)
        file.setOnClickListener {
            selectfile.launch("*/*")
        }

        val camera=findViewById<ImageView>(R.id.camera)

        camera.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, Camera_PERMISSION_CODE)
        }


        //--------------keyboard open hide footer
        val footerView = findViewById<LinearLayout>(R.id.footer)
        val footer = findViewById<LinearLayout>(R.id.plus)
        val head = findViewById<LinearLayout>(R.id.header)
        val msgView = findViewById<LinearLayout>(R.id.messageview)
        // Add a global layout listener to detect changes in layout
        val rootView = findViewById<LinearLayout>(R.id.root)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)

                // Get the height of the visible content area
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom


                if (keypadHeight > screenHeight * 0.15) { // Keyboard is open
                    footerView.visibility = View.GONE
                    footer.visibility = View.GONE
                    head.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

                } else { // Keyboard is closed
                    footerView.visibility = View.VISIBLE
                    footer.visibility = View.VISIBLE
                    head.visibility = View.VISIBLE
                    head.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

                }
            }
        })



    }


////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Camera_PERMISSION_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageuri = getImageUri(imageBitmap)
            sendMessage(personID,3,imageuri.toString())
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Image", null)
        return Uri.parse(path)
    }

    private fun isMic(): Boolean {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
            return true
        else return false
    }

    private fun getMic() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MIC_PERMISSION_CODE
            )
        }
    }


    private fun startRecording() {
        Log.d("My audio", "Entered function")
        outputFile = "${externalCacheDir?.absolutePath}/audio.3gp"

        recorder = MediaRecorder().apply {
            Log.d("My audio", "Entered recorder")
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)

            try {
                prepare()
                start()
                Log.d("My audio", "Recording started")
                Toast.makeText(this@page12_Activity_chat2, "Audio Recording", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Log.e("My audio", "Error starting recording: ${e.message}")
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            try {
                stop()
                release()
                recorder = null
                Log.d("My audio", "Recording stopped")

                Toast.makeText(this@page12_Activity_chat2, "Recording Stops", Toast.LENGTH_SHORT)
                    .show()
                uploadAudioToFirebase()
            } catch (e: Exception) {
                Log.e("My audio", "Error stopping recording: ${e.message}")
            }
        }
    }

    private fun uploadAudioToFirebase() {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("audio")
            .child("${UUID.randomUUID()}.3gp") // Use a random UUID as the file name

        val file = Uri.fromFile(File(outputFile))

        storageRef.putFile(
            file, StorageMetadata.Builder()
                .setContentType("audio/3gpp")
                .build()
        )
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL asynchronously
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d(
                        "My audio",
                        "Audio uploaded to Firebase Storage. Download URL: $downloadUrl"
                    )
                    sendMessage(personID, 2, downloadUrl)

                    // Now you have the download URL, you can use it as needed, such as saving it to the database
                    //saveAudioUrlToDatabase(downloadUrl)
                }.addOnFailureListener { exception ->
                    // Handle failures in getting the download URL
                    Log.e("My audio", "Error getting download URL: ${exception.message}")
                }
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful uploads
                Log.e("My audio", "Error uploading audio to Firebase Storage: ${exception.message}")
            }
    }


    private fun sendMessage(receiverID: String, type: Int, messageuri: String) {
        val msg = findViewById<EditText>(R.id.message)
        val time = getCurrentTime()


        val assignAnID = if (user.hashCode() < receiverID.hashCode()) {
            "$user$receiverID"
        } else {
            "$receiverID$user"
        }

        val database = FirebaseDatabase.getInstance().getReference("chat").child(assignAnID)
        val messageId = database.push().key // Generate a unique key for the message
        if (msgid == "") {
            var chat = chatMessageModel("", "", "", "", "", "", "", "", "", "1")
            if (type == 1) {
                chat = chatMessageModel(
                    messageId!!,
                    personID, user, time, "",msg.text.toString(), "", "", "", "1"
                )
                sendNotification(msg.text.toString())
            } else if (type == 2) {
                chat = chatMessageModel(
                    messageId!!, personID, user, time, "", "", messageuri, "", "", "2"
                )
                sendNotification("Voice Message")
            } else if (type == 3) {
                chat = chatMessageModel(messageId!!, personID, user, time, "", "", "", messageuri, "", "3"
                )
                //sendNotification("Picture")
            }
            else if (type == 4) {
                chat = chatMessageModel(messageId!!, personID, user, time, "", "", "", "",messageuri, "4"
                )
                //sendNotification("Files")

            }
            Log.d("My picture", messageuri)

            database.child(messageId!!).setValue(chat)
                .addOnSuccessListener {
                    //Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                    msg.setText("")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to send message: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
        else
        {

            val database = FirebaseDatabase.getInstance().getReference("chat").child(assignAnID)
            database.child(msgid!!).child("message").setValue(msg.text.toString())
                .addOnSuccessListener {
                    //Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                    msg.setText("")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to send message: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            msgid=""
        }


        LoadMsg(assignAnID.toString())

    }

    private fun LoadMsg(userID: String) {
        //--------------------------------- read chats

        Log.d("My tag", "Entered function")
        var chatlist = ArrayList<chatMessageModel>()
        //chatlist.clear()

        val dbMentor = FirebaseDatabase.getInstance().getReference("chat")

        dbMentor.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userID2 = userSnapshot.key
                    Log.d("My tag", userID2.toString())

                    // Iterate through each booking for this user
                    if (userID == userID2) {
                        for (chatSnapshot in userSnapshot.children) {
                            Log.d("My tag", chatSnapshot.toString())
                            val mentor = chatSnapshot.child("id").getValue(String::class.java)

                            val time = chatSnapshot.child("time").getValue(String::class.java)
                            val tmsg = chatSnapshot.child("message").getValue(String::class.java)
                            val messgid = chatSnapshot.child("msgID").getValue(String::class.java)
                            val audio = chatSnapshot.child("audio").getValue(String::class.java)
                            val pic = chatSnapshot.child("picture").getValue(String::class.java)
                            val file = chatSnapshot.child("file").getValue(String::class.java)
                            val type = chatSnapshot.child("type").getValue(String::class.java)
                            val namebase = FirebaseDatabase.getInstance().getReference("user")
                            var uri: String = ""

                            namebase.child(mentor.toString()).get()
                                .addOnSuccessListener { dataSnapshot ->

                                    uri = dataSnapshot.child("imguri").value.toString()

                                }

                            // Create a Bookdetail object and add it to the list
                            val booking = chatMessageModel(
                                messgid!!,
                                "",
                                mentor!!,
                                time!!,
                                uri,
                                tmsg.toString(),
                                audio.toString(),
                                pic.toString(), file.toString(),
                                type.toString()
                            )
                            chatlist.add(booking)
                            //Toast.makeText(this@page12_Activity_chat2,tmsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseError", "Error reading chats: ${error.message}")
            }
        })

        val handler = Handler(Looper.getMainLooper())
        super.onResume()
        handler.postDelayed({
            chatMentor(chatlist, userID)
        }, 1500)


        Log.d("My tag", "out of function")

    }

    @SuppressLint("MissingInflatedId")
    private fun chatMentor(chatlist: MutableList<chatMessageModel>, chatid: String) {
//        val scrollView1 = findViewById<ScrollView>(R.id.scroll)
//
//        scrollView1.post(Runnable { // This method works but animates the scrolling
//            // which looks weird on first load
//            // scroll_view.fullScroll(View.FOCUS_DOWN);
//
//            // This method works even better because there are no animations.
//            scrollView1.scrollTo(0, scrollView1.getBottom())
//        })

        val container = findViewById<LinearLayout>(R.id.thechats)
        Log.d("MyTag:", "entered function")
        var r = ""
        container.removeAllViews()
        for (chat in chatlist) {
            if (chat.id == user) {        //for sent messages
                //Toast.makeText(this@page4_Activity_home,mntrlist[0].mail, Toast.LENGTH_SHORT).show()
                val itemView = layoutInflater.inflate(R.layout.chat_send_recyler, null)
                Log.d("MyTag:", chat.id.toString())

                var mediaPlayer: MediaPlayer? = null
                val audioUrl = chat.audio

                // Retrieve img2 within the itemView

                val msg = itemView.findViewById<TextView>(R.id.msg)
                val time = itemView.findViewById<TextView>(R.id.time)
                val edit = itemView.findViewById<TextView>(R.id.edit)
                val del = itemView.findViewById<TextView>(R.id.del)
                val img = itemView.findViewById<ImageView>(R.id.pic)
                val enter = findViewById<EditText>(R.id.message)



                if (chat.type == "1") {
                    msg.text = chat.message

                } else if (chat.type == "2")     //audio
                {
                    msg.setBackgroundResource(R.drawable.volume)
                    val params = msg.layoutParams
                    params.width = 55
                    params.height = 55
                    msg.layoutParams = params
                } else if (chat.type == "3") {
                    val urii = chat.picture
                    Glide.with(itemView)
                        .load(urii)
                        .into(img)
                    val params = img.layoutParams
                    params.width = 425
                    params.height = 425
                    img.layoutParams = params
                    msg.setPadding(0, 0, 0, 0) // Set padding to 0 for all sides
                    msg.textSize = 0f // Set text size to 0


                }
                else if (chat.type == "4") {
                    val urii = chat.file
                    Glide.with(itemView)
                        .load(urii)
                        .into(img)
                    val params = img.layoutParams
                    params.width = 425
                    params.height = 275
                    img.layoutParams = params
                    msg.setPadding(0, 0, 0, 0) // Set padding to 0 for all sides
                    msg.textSize = 0f // Set text size to 0


                }
                time.text = chat.time.toString()

                Log.d("MyTag:", chat.message.toString())
                msg.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        if (chat.type == "1")
                            edit.text = "Edit"
                        del.text = "Delete"
                        return true // Return true to consume the long-click event
                    }
                })
                img.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        del.text = "Delete"
                        return true // Return true to consume the long-click event
                    }
                })

                edit.setOnClickListener {
                    edit.text = ""
                    del.text = ""
                    enter.setText(msg.text)
                    msgid = chat.msgID
                    enter.requestFocus()


                }
                img.setOnClickListener{
                    del.text = ""
                }

                msg.setOnClickListener {        //play voice
                    edit.text = ""
                    del.text = ""
                    if (chat.type == "2") {
                        //setting up audio
                        mediaPlayer = MediaPlayer().apply {
                            setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
                            )
                            setDataSource(audioUrl)
                            prepareAsync()
                            setOnPreparedListener {
                                // Start playing audio when prepared
                                it.start()
                            }
                            setOnErrorListener { mp, what, extra ->
                                // Handle errors
                                false
                            }
                        }

                    }

                }
                del.setOnClickListener {
                    edit.text = ""
                    del.text = ""
                    msg.text = ""
                    val database = FirebaseDatabase.getInstance().getReference("chat")
                    val childRef = database.child(chatid).child(chat.msgID)
                    childRef.removeValue()
                        .addOnSuccessListener {
                            // Child node deleted successfully
                        }
                        .addOnFailureListener { error ->
                            // Handle any errors that occurred
                            Log.e("Firebase", "Error deleting child node: ${error.message}")
                        }
                    if (chat.type == "2") {
                        val storageRef =
                            FirebaseStorage.getInstance().getReferenceFromUrl(chat.audio)
                        storageRef.delete()
                    }
//                    else if (chat.type == "3") {
//                        val storageRef =
//                            FirebaseStorage.getInstance().getReferenceFromUrl(chat.picture)
//                        storageRef.delete()
//                    }
//                    else if (chat.type == "4") {
//                        val storageRef =
//                            FirebaseStorage.getInstance().getReferenceFromUrl(chat.file)
//                        storageRef.delete()
//                    }

                }


                // Add the inflated item view to the container

                container.addView(itemView)

            } else {          //reciving end
                //Toast.makeText(this@page4_Activity_home,mntrlist[0].mail, Toast.LENGTH_SHORT).show()
                val itemView = layoutInflater.inflate(R.layout.chat_recieve_recyler, null)
                var mediaPlayer: MediaPlayer? = null
                val audioUrl = chat.audio

                // Retrieve img2 within the itemView
                val msg = itemView.findViewById<TextView>(R.id.msg)
                val time = itemView.findViewById<TextView>(R.id.time)
                val img = itemView.findViewById<ImageView>(R.id.pic)

                val pic =
                    itemView.findViewById<com.google.android.material.imageview.ShapeableImageView>(
                        R.id.profilepic
                    )
                Log.d("MyTag:", chat.message.toString())

                if (chat.type == "1")
                    msg.text = chat.message
                else if (chat.type == "2") {
                    msg.setBackgroundResource(R.drawable.volume)
                    val params = msg.layoutParams
                    params.width = 55
                    params.height = 55
                    msg.layoutParams = params
                } else if (chat.type == "3") {
                    val urii = chat.picture
                    Glide.with(itemView)
                        .load(urii)
                        .into(img)
                    val params = img.layoutParams
                    params.width = 425
                    params.height = 425
                    img.layoutParams = params
                    msg.setPadding(0, 0, 0, 0) // Set padding to 0 for all sides
                    msg.textSize = 0f // Set text size to 0

                }
                else if (chat.type == "4") {
                    val urii = chat.file
                    Glide.with(itemView)
                        .load(urii)
                        .into(img)
                    val params = img.layoutParams
                    params.width = 425
                    params.height = 275
                    img.layoutParams = params
                    msg.setPadding(0, 0, 0, 0) // Set padding to 0 for all sides
                    msg.textSize = 0f // Set text size to 0

                }
                time.text = chat.time.toString()
                val namebase1 = FirebaseDatabase.getInstance().getReference("mentor")
                namebase1.child(chat.id).get().addOnSuccessListener { dataSnapshot ->

                    val picM:String=dataSnapshot.child("uri").value.toString()
                    if (!picM.isNullOrEmpty())
                    {
                        defulturi=picM

                    }
                }
                if (chat.uri.length > 10)
                    Glide.with(this)
                        .load(chat.uri)
                        .into(pic)
                else
                    Glide.with(this)
                        .load(defulturi)
                        .into(pic)
                msg.setOnClickListener {
                    if (chat.type == "2") {
                        //setting up audio
                        mediaPlayer = MediaPlayer().apply {
                            setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
                            )
                            setDataSource(audioUrl)
                            prepareAsync()
                            setOnPreparedListener {
                                // Start playing audio when prepared
                                it.start()
                            }
                            setOnErrorListener { mp, what, extra ->
                                // Handle errors
                                false
                            }
                        }

                    }

                }

                // Add the inflated item view to the container

                container.addView(itemView)

            }
        }

        val scrollView = findViewById<ScrollView>(R.id.scroll)

        scrollView.post(Runnable {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            Log.d("Scroll view:", "out function")
//           scrollView.scrollTo(0, scrollView.getBottom())
        })



        Log.d("MyTag:", "out function")

    }

    fun getCurrentTime(): String {
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    private fun saveimage() {
        val randomString = "chat_${UUID.randomUUID()}"
        val storeref = FirebaseStorage.getInstance().getReference("Pictures")
            .child(user)
            .child(randomString)

        Log.d("MyTag", "entered save image function")

        storeref.putFile(imageuri!!)
            .addOnSuccessListener {
                storeref.downloadUrl.addOnSuccessListener {
                    //save Path


                    Toast.makeText(
                        this,
                        "Sending picture",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendMessage(personID, 3, it.toString())

                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
            }

        Log.d("MyTag", "out of function")
    }

    private fun fetchUser(userID: String) {
        val namebase = FirebaseDatabase.getInstance().getReference("user")
        val nameTextView = findViewById<TextView>(R.id.name)
        namebase.child(userID.toString()).get().addOnSuccessListener { dataSnapshot ->
            nameTextView.text = dataSnapshot.child("namee").value.toString()
            if (nameTextView.text == "null")
                nameTextView.text = userID.toString()
            var uri = dataSnapshot.child("imguri").value.toString()
            if (uri.length > 10)
                defulturi = uri

        }
    }
    private fun savefile() {
        val randomString = "chat_${UUID.randomUUID()}"
        val storeref = FirebaseStorage.getInstance().getReference("files")
            .child(user)
            .child(randomString)

        Log.d("MyTag", "entered save file function")

        storeref.putFile(fileuri!!)
            .addOnSuccessListener {
                storeref.downloadUrl.addOnSuccessListener {
                    //save Path


                    Toast.makeText(
                        this,
                        "Sending picture",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendMessage(personID, 4, it.toString())

                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
            }

        Log.d("MyTag", "out of function")
    }



    private fun sendNotification(message: String) {
        // Variables to hold username and other user's token
        var username: String? = null
        var othertoken: String? = null
        var str=user.toString()+" took a screenShot of chat"
        if(message!=str)
        {
            val str = "You have a new message"
            val database = FirebaseDatabase.getInstance().getReference("notifics").child(personID)
            val newMessageRef = database.push()
            newMessageRef.setValue(str)

        }

        // Reference to the current user's node in the database
        val currentUserRef =
            FirebaseDatabase.getInstance().getReference("user").child(user.toString())

        // Retrieve the current user's username asynchronously
        currentUserRef.child("namee").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                username = dataSnapshot.value.toString()

                // Once the username is retrieved, proceed to retrieve the other user's token
                val otherUserRef =
                    FirebaseDatabase.getInstance().getReference("user").child(personID.toString())
                otherUserRef.child("token")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(otherTokenSnapshot: DataSnapshot) {
                            othertoken = otherTokenSnapshot.value.toString()

                            // Now, you have both the username and the other user's token, proceed to send the notification
                            if (username != null && othertoken != null) {
                                sendNotificationToOtherUser(username!!, othertoken!!, message)
                            } else {
                                // Handle the case where either the username or the other user's token is null
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors that occur while retrieving the other user's token
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur while retrieving the current user's username
            }
        })
    }

    private fun sendNotificationToOtherUser(username: String, othertoken: String, message: String) {
        try {
            // Create JSON objects for notification and data
            val notificationObj = JSONObject()
            notificationObj.put("title", username)
            notificationObj.put("body", message)

            val dataObj = JSONObject()
            dataObj.put("userId", user.toString())

            // Create the main JSON object for the FCM request
            val jsonObject = JSONObject()
            jsonObject.put("notification", notificationObj)
            jsonObject.put("data", dataObj)
            jsonObject.put("to", othertoken)

            callAPI(jsonObject)
        } catch (e: Exception) {
            // Handle any exceptions that occur during JSON object creation
        }
    }

    fun callAPI(jsonObject: JSONObject) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"

        val requestBody = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header(
                "Authorization",
                "Bearer AAAAjvnlv6Y:APA91bHgDlJZ2h504qWrXafG89CzgU6VQJQQkBfuU_53Rj0Y-ZMMjmFDvmNNw1vR2sFeERHELSsman0236A48Bl_bplCvmn_d2TAY2kJSTVhrcrLNB9vGEaK51c4uqBl_HMWZB1RFaMG"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                // Handle success response
                val responseData = response.body?.string()
                // Process responseData as needed
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
            }
        })



    }

    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(this, this)

    override fun onStart() {
        super.onStart()
        screenshotDetectionDelegate.startScreenshotDetection()
    }

    override fun onStop() {
        super.onStop()
        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    override fun onScreenCaptured(path: String) {
        Log.d("Screen shot", "screen captured")
        sendNotification("Screen shot taken")
        val str=user.toString()+" took a screenShot of chat"
        val database = FirebaseDatabase.getInstance().getReference("notifics").child(personID)
        val newMessageRef = database.push()
        newMessageRef.setValue(str)
    }

    override fun onScreenCapturedWithDeniedPermission() {
        Log.d("Screen shot", "screen permission denied")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_DENIED) {
                    showReadExternalStoragePermissionDeniedMessage()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
        )
    }

    private fun showReadExternalStoragePermissionDeniedMessage() {
        Toast.makeText(this, "Read external storage permission has denied", Toast.LENGTH_SHORT)
            .show()
    }
}


