package com.example.minimoneybox

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import java.util.regex.Pattern
import kotlin.concurrent.thread
import android.os.AsyncTask
import android.util.JsonReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject
import java.io.*
import android.content.Intent



/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    lateinit var btn_sign_in: Button
    lateinit var til_email: TextInputLayout
    lateinit var et_email: EditText
    lateinit var til_password: TextInputLayout
    lateinit var et_password: EditText
    lateinit var til_name: TextInputLayout
    lateinit var et_name: EditText
    lateinit var pigAnimation: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        setupAnimation()
    }

    private fun setupViews() {
        btn_sign_in = findViewById(R.id.btn_sign_in)
        til_email = findViewById(R.id.til_email)
        et_email = findViewById(R.id.et_email)
        til_password = findViewById(R.id.til_password)
        et_password = findViewById(R.id.et_password)
        til_name = findViewById(R.id.til_name)
        et_name = findViewById(R.id.et_name)
        pigAnimation = findViewById(R.id.animation)

        btn_sign_in.setOnClickListener {
            if (allFieldsValid()) {
                Toast.makeText(this, R.string.input_valid, Toast.LENGTH_LONG).show()
                //network task execute on separate thread
                AsyncTask.execute {
                    run {
                        // Create URL
                        val moneyboxEndpoint = URL("https://api-test01.moneyboxapp.com/users/login")
                        // Create connection
                        val myConnection = moneyboxEndpoint.openConnection() as HttpsURLConnection
                        myConnection.requestMethod = "POST"
                        myConnection.setRequestProperty("AppId", "3a97b932a9d449c981b595")
                        myConnection.setRequestProperty("Content-Type", "application/json")
                        myConnection.setRequestProperty("appVersion", "5.10.0")
                        myConnection.setRequestProperty("apiVersion", "3.0.0")
                        // Enable writing
                        myConnection.doOutput = true
                        myConnection.doInput = true
                        //info to send
                        val jsonParam = JSONObject()
                        jsonParam.put("Email", et_email.text.toString().trim())
                        jsonParam.put("Password", et_password.text.toString().trim())
                        jsonParam.put("Idfa", "ANYTHING")
                        //open stream and write
                        val os = DataOutputStream(myConnection.outputStream)
                        os.writeBytes(jsonParam.toString())
                        os.flush()
                        os.close()
                            //read response
                            if (myConnection.responseCode == 200) {
                                println("success!")
                                //read json response
                                val bR = BufferedReader(InputStreamReader(myConnection.inputStream))
                                var line: String? = null
                                val responseStrBuilder = StringBuilder()
                                while ({ line = bR.readLine(); line }() != null) {
                                    responseStrBuilder.append(line)
                                }
                                myConnection.inputStream.close()
                                //extract bearer token
                                val result = JSONObject(responseStrBuilder.toString())
                                val sessionJson = JSONObject(result.getString("Session"))
                                val userJson = JSONObject(result.getString("User"))
                                val bearerToken = sessionJson.getString("BearerToken")
                                val fname = userJson.getString("FirstName")
                                println("bearer token: $bearerToken")
                                println("fname $fname")
                                //send to next screen
                                val intent = Intent(this@LoginActivity, HomeAreaActivity::class.java)
                                intent.putExtra("fname", fname.trim())
                                intent.putExtra("bearerToken",bearerToken.trim())
                                this@LoginActivity.startActivity(intent)
                                finish()
                            }else {
                                println("HTTP Error:\n" +myConnection.responseCode.toString() + "\n" + myConnection.responseMessage.toString())
                            }
                        myConnection.disconnect()
                    }//end run
                } //end async
            }//end if
        }//end button click
    }//end fun setupViews()

    private fun allFieldsValid(): Boolean {
        var allValid = false
        //if email validation fails then display error and return
        if (Pattern.matches(EMAIL_REGEX, et_email.text.toString())) {
            til_email.error = null
            allValid = true
        } else {
            til_email.error = getString(R.string.email_address_error)
        }

        if (Pattern.matches(PASSWORD_REGEX, et_password.text.toString())) {
            til_password.error = null
            //println("password ok")
            allValid = true
        } else {
            til_password.error = getString(R.string.password_error)
            //println("password error")
        }
        //if any text entered then validate
        if (et_name.length() > 0) {
            println(et_name.length())
            if (Pattern.matches(NAME_REGEX, et_name.text.toString())) {
                til_name.error = null
                println("name fine")
                allValid = true
            } else {
                println("name error")
                til_name.error = getString(R.string.full_name_error)
            }
        }
        return allValid
    }

    private fun setupAnimation() {
        pigAnimation.setMinFrame(0)
        pigAnimation.setMaxFrame(109)
        pigAnimation.playAnimation()

        //to loop after certain frame reached
        var isAnimating = true
        thread {
            while (isAnimating) {
                if (pigAnimation.frame >= 109) {
                    runOnUiThread {
                        pigAnimation.pauseAnimation()// pause Animation
                    }
                    runOnUiThread {
                        pigAnimation.setMinFrame(131)
                        pigAnimation.setMaxFrame(158)
                        pigAnimation.playAnimation() // Resume your animation from frame 131 - 158
                    }
                    isAnimating = false
                }
            }
        }
    }

    private fun loginCheck(): Boolean {
        var valid = false;




        return valid
    }

    companion object {
        val EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
        val NAME_REGEX = "^([a-z]+[,.]?[ ]?|[a-z]+['-]?)+\$"
        val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[A-Z]).{8,50}$"
        val firstAnim = 0 to 109
        val secondAnim = 131 to 158
    }


}
