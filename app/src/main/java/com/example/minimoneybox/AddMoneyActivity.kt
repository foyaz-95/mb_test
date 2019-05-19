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
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_money.*
import org.json.JSONArray


/**
 * A login screen that offers login via email/password.
 */
class AddMoneyActivity : AppCompatActivity() {
    lateinit var tv_acc_name: TextView
    lateinit var tv_plan_val: TextView
    lateinit var tv_moneybox_val: TextView
    lateinit var btn_add_ten: Button
    private var bearerToken=""
    private var productID = 0
    var plan_value = 0
    var moneybox_value = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money)

        //get passed variables
        val intent = intent
        val acc_name = intent.getStringExtra("acc_name")
        productID = intent.getIntExtra("prod_id", 0)
        plan_value = intent.getIntExtra("plan_val", 0)
        moneybox_value = intent.getIntExtra("moneybox_val", 0)
        bearerToken = intent.getStringExtra("bearerToken")
        println(bearerToken)
        //set textviews
        tv_acc_name = findViewById(R.id.tv_acc_name)
        tv_acc_name.text = acc_name
        tv_plan_val = findViewById(R.id.tv_plan_val)
        tv_plan_val.text = "Plan Value: £$plan_value"
        tv_moneybox_val = findViewById(R.id.tv_moneybox_val)
        tv_moneybox_val.text = "Moneybox Value: £$moneybox_value"

        //button to add  £10
        btn_add_ten = findViewById(R.id.btn_add_ten)
        btn_add_ten.setOnClickListener {
            println("Adding £10 to product with ID: $productID...")
            //network task to add £10
            AsyncTask.execute {
                run {
                    // Create URL
                    val moneyboxEndpoint = URL("https://api-test01.moneyboxapp.com/oneoffpayments")
                    // Create connection
                    val myConnection = moneyboxEndpoint.openConnection() as HttpsURLConnection
                    myConnection.requestMethod = "POST"
                    myConnection.setRequestProperty("AppId", "3a97b932a9d449c981b595")
                    myConnection.setRequestProperty("Content-Type", "application/json")
                    myConnection.setRequestProperty("appVersion", "5.10.0")
                    myConnection.setRequestProperty("apiVersion", "3.0.0")
                    myConnection.setRequestProperty("Authorization", "Bearer $bearerToken")
                    // Enable writing
                    myConnection.doOutput = true
                    myConnection.doInput = true
                    //info to send
                    val jsonParam = JSONObject()
                    jsonParam.put("Amount", 10)
                    jsonParam.put("InvestorProductId", productID)
                    //open stream and write
                    val os = DataOutputStream(myConnection.outputStream)
                    os.writeBytes(jsonParam.toString())
                    os.flush()
                    os.close()

                    if (myConnection.responseCode == 200) {
                        runOnUiThread{Toast.makeText(this, R.string.add_ten, Toast.LENGTH_LONG).show()}
                        finish()
                    }else{
                        runOnUiThread{Toast.makeText(this, R.string.add_ten_error, Toast.LENGTH_LONG).show()}
                        finish()
                    }
                    myConnection.disconnect()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }


}
